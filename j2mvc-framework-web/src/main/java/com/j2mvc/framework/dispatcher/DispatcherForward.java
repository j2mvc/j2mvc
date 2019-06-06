package com.j2mvc.framework.dispatcher;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j2mvc.framework.Constants;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.framework.dao.DataSourceJndi;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.Foreign;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Sql;
import com.j2mvc.util.FieldUtil;
import com.j2mvc.util.InvokeUtils;
import com.j2mvc.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**  
 * 页面调配
 * @author 杨朔
 * @version 1.0 2014-2-23
 * @version 1.1.6 2014-8-17
 */
public class DispatcherForward {
	static final Logger  log = Logger.getLogger(DispatcherForward.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private JSONObject jsonData;
	private ActionBean bean;
	private String file;
	/**
	 * 构造器
	 * @param request
	 * @param response
	 * @param bean
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public DispatcherForward(HttpServletRequest request,
			HttpServletResponse response, ActionBean bean) throws ServletException, IOException {
		super();
		this.request = request;
		this.response = response;
		this.bean = bean;
		init();
	}
	/**
	 * 初始化Action
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void init() throws ServletException, IOException{
		execute();
	}
	/**
	 * 执行方法
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void execute() throws ServletException, IOException{
		
		String className = bean.getClassName().indexOf(".")!=-1?bean.getClassName():
								bean.getPackageName() + "."+bean.getClassName();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.error("执行方法>>"+e.getMessage());
		}
		if(clazz!=null){
			try {
				Object obj = clazz.newInstance();
				InvokeUtils.invoke(clazz, "setRequest", obj, new Object[]{request},HttpServletRequest.class);
				InvokeUtils.invoke(clazz, "setResponse", obj,  new Object[]{response},HttpServletResponse.class);
				InvokeUtils.invoke(clazz, "setBean", obj,  new Object[]{bean},ActionBean.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"path",request.getContextPath()},String.class,Object.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"WEB_ROOT",request.getContextPath()},String.class,Object.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"PATH",request.getContextPath()},String.class,Object.class);
				Object result = InvokeUtils.invoke(clazz, "onStart", obj, null);
				if(result == null)
					result = invoke(bean.getMethod(), obj);
				file = result instanceof String?(String) result:null;
				// 获取JSON方式提交的数据，必须放到最后获取，否则影响获取其它方式提交的数据
				getJsonData();
				InvokeUtils.invoke(clazz, "setJsonData", obj,  new Object[]{jsonData},JSONObject.class);
				forward(file);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	private void getJsonData(){
		try {
			if(request.getInputStream()!=null)
				try {
					jsonData = new JSONObject(readInputStream(request.getInputStream()));
				} catch (JSONException e) {
					log.warn("解析JSON格式错误:"+e.getMessage());
				}
		} catch (IOException e) {
			log.warn("获取输入流错误:"+e.getMessage());
		}
	}
	/**
	 * 跳转页面
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void forward(String file) throws ServletException, IOException{
		if(file==null)
			return;
		file = file.startsWith("/")?file:bean.getDir()+file;
		if(file!=null && !response.isCommitted()){
			if(bean.isIncude()){
				log.info(file+" is includePage");
				request.getRequestDispatcher(file).include(request, response);
			}else{
				request.getRequestDispatcher(file).forward(request, response);
			}
			response.getWriter().flush();
		}
	}

	/**
	 * 过滤器销毁
	 */
	public void destroy() {
		DataSourceJndi.destroy();
	}

	/**
	 * 执行具体方法
	 * @param method
	 * @param object
	 * 
	 * @throws NotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object invoke(Method method,Object object){
		Class<?> clazz = object.getClass();
		Class<?>[] types = method.getParameterTypes();
		int length = types.length;
		if(length>0){
			String[] names = getParameterNames(clazz,method.getName(), length);
			Object[] values = new Object[types.length];
			if(names!=null){
				for (int i = 0; i < types.length; i++) {
					Class<?> type = types[i];
					values[i] = getParameterValue(type, names[i]);
				}
				try {
					return method.invoke(object,values);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else {
			return InvokeUtils.invoke(clazz, bean.getMethod().getName(), object, null);
		}
		return null;
	}
	/**
	 * 获取参数名
	 * @param methodName
	 * @param length
	 * 
	 * @throws NotFoundException
	 */
	private  String[] getParameterNames(Class<?> clazz,String methodName,int length){
        ClassPool pool = ClassPool.getDefault(); 
        CtClass cc;
		try {
			// web下必须注册类路径，否则找不到类文件
			pool.insertClassPath(new ClassClassPath(clazz)); 
			cc = pool.getCtClass(clazz.getName());
	        CtMethod cm = cc.getDeclaredMethod(methodName);
	        // 使用javaassist的反射方法获取方法的参数名  
	        MethodInfo methodInfo = cm.getMethodInfo();  
	        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
			if (attr != null) {
				// exception
				String[] paramNames = new String[length];
				int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
				for (int i = 0; i < length; i++) {
					paramNames[i] = attr.variableName(i + pos);
				}
				return paramNames;
			}    
		} catch (NotFoundException e) {
			log.error(e.getMessage());
		}  
        return null;
	}
	/**
	 * 获取参数值
	 * @param type
	 * @param name
	 * 
	 */
	private Object getParameterValue(Class<?> type,String name){
		if(RequestUri.class.isAssignableFrom(type) ){
			String path = request.getContextPath();
			String requestUri = request.getRequestURI();
			requestUri = StringUtils.deleRepeat(requestUri, "/");
			requestUri = requestUri.substring(path.length(),requestUri.length());
			requestUri = requestUri.startsWith("/")?requestUri.substring(1,requestUri.length()):requestUri;
			requestUri = requestUri.endsWith("/")?requestUri.substring(0,requestUri.length()-1):requestUri;
			String[] values = requestUri.split("/");
			return new RequestUri(values);
		}else if(String.class.isAssignableFrom(type)
				|| Integer.class.isAssignableFrom(type) 
				|| Short.class.isAssignableFrom(type)
				|| short.class.isAssignableFrom(type)
				|| int.class.isAssignableFrom(type)
				|| Long.class.isAssignableFrom(type) 
				|| long.class.isAssignableFrom(type)
				|| Float.class.isAssignableFrom(type)
				|| float.class.isAssignableFrom(type)
				|| Double.class.isAssignableFrom(type)
				|| double.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type)
				|| boolean.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| byte[].class.isAssignableFrom(type)
				|| String[].class.isAssignableFrom(type)){
			return getValue(type, name);
		}else {
			try {
				List<Field> fields = FieldUtil.getFields(null, type);
				Object object = getObjectByPrimaryKey(fields, type);
				if(object == null){
					object = type.newInstance();
				}
				invoke(fields, object);
				return object;
			} catch (InstantiationException e) {
				log.error(e.getMessage());
			} catch (IllegalAccessException e) {
				log.error(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 查询指定主键实体对象
	 * @param fields
	 * @param clazz
	 * 
	 */
	private Object getObjectByPrimaryKey(List<Field> fields,Class<?> clazz){
		/* 主键 */
		PrimaryKey primaryKey = clazz.getAnnotation(PrimaryKey.class);
		int size = fields.size();
		for(int i=0;i<size;i++){
			Field field = fields.get(i);
			if(field!=null){
				/* 注释字段,对应数据表字段 */
				Column column = field.getAnnotation(Column.class);
				/* 主键字段 */
				if(column!=null && field.getName().equals(primaryKey.name())){
					// 查询出系统数据
					try {
						DaoSupport daoSupport = new DaoSupport(clazz);
						Object key = getParameterValue(field.getType(),field.getName());
						return daoSupport.get(key);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取值
	 * @param type
	 * @param name
	 * 
	 */
	private Object getValue(Class<?> type,String name){
		Object value = null;
		String str = getParam(name);
		if(String.class.isAssignableFrom(type))
			value = str;				
		else if(Integer.class.isAssignableFrom(type) 
				|| Short.class.isAssignableFrom(type)
				|| short.class.isAssignableFrom(type)
				|| int.class.isAssignableFrom(type)){
			try{
				value = Integer.valueOf(str);
			}catch(Exception e){}
		}else if(Long.class.isAssignableFrom(type) 
				|| long.class.isAssignableFrom(type)){
			try{
				value = Long.valueOf(str);
			}catch(Exception e){}
		}else if(Float.class.isAssignableFrom(type)
				|| float.class.isAssignableFrom(type)){
			try{
				value = Float.valueOf(str);
			}catch(Exception e){}
		}
		else if(Double.class.isAssignableFrom(type)
				|| double.class.isAssignableFrom(type)){
			try{
				value = Double.valueOf(str);
			}catch(Exception e){}
		}else if(Boolean.class.isAssignableFrom(type)
				|| boolean.class.isAssignableFrom(type)){
			if(str!=null && str.equalsIgnoreCase("true")){
				value = true;
			}else if(str!=null && str.equalsIgnoreCase("false")){
				value = false;
			}
		}else if(Time.class.isAssignableFrom(type))
			try {
				value = Constants.DEFAULT_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		else if(Date.class.isAssignableFrom(type)){
			try {
				value = Constants.DEFAULT_DATE_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}else if(byte[].class.isAssignableFrom(type)){
			value = str;
		}else if(String[].class.isAssignableFrom(type)){
			value = request.getParameterValues(name);
		}	
		return value;
	}
	/**
	 * 写入字段数组值
	 * @param fields
	 * @param obj
	 */
	public void invoke(List<Field> fields,Object obj){
		int size = fields.size();
		for(int i=0;i<size;i++){
			Field field = fields.get(i);
			if(field!=null)
				invoke(field,obj);
		}
	}
	/**
	 * 写入字段值
	 * @param field
	 * @param obj
	 * @param rs
	 */	
	private void invoke(Field field,Object obj) {
		/* 注释字段,对应数据表字段 */
		Column column = field.getAnnotation(Column.class);
		
		/* 注释字段,对应关联对象字段 */
		Foreign foreign =  field.getAnnotation(Foreign.class);

		/* 注释字段,根据SQL语句查询值，仅返回基础数据类型 */
		Sql sql =  field.getAnnotation(Sql.class);
		
		if(column == null){
			return;
		}
		String name = field.getName();
		/* 数据表数据类型 */		
		Class<?> type = field.getType();	
		try {
			Object value = null;
			if(foreign!=null){
				// 外键对象之主键属性数据类型
				type = FieldUtil.getForeignKey(type).getType();
			}
			value = getValue(type,name);
			if(foreign!=null){
				// 外键,字段为对象,获取类主键,并实例
				try {
					DaoSupport daoSupport = new DaoSupport(field.getType());
					value = daoSupport.get(value);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}else if(sql!=null){
				// 根据SQL语句查询值
				try {
					DaoSupport daoSupport = new DaoSupport(field.getType());
					value = daoSupport.get(sql.value());
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			// 执行写入操作
			PropertyDescriptor pd = new PropertyDescriptor(name, obj.getClass());
			Method wm = pd.getWriteMethod();
			if(value!=null && !value.equals(""))
				wm.invoke(obj, value);			
		} catch (IntrospectionException e) {
			log.error(e.getMessage());		
		}catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		}catch (IllegalAccessException e) {
			log.error(e.getMessage());
		}catch (InvocationTargetException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 返回指定页面参数值
	 * @param request
	 * @param name
	 * 
	 */
	private String getParam(String name){
		String value =  request.getParameter(name);
		if(StringUtils.isEmpty(value) && jsonData!=null){
			try {
				value = jsonData.getString(name);
			} catch (JSONException e) {
				log.warn("读取JSON值错误:"+e.getMessage());
			}
		}
		return getUtf8(value);
	}
	/**
	 * 获取UTF8格式
	 * @param value
	 * 
	 */
	private String getUtf8(String value){
		if(value == null)
			return null;
		try {
			if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value)){
				value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
			}else if(java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(value)){
			}else if(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value)){
				value = new String(value.getBytes("GBK"),"UTF-8");
			}else  if(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(value)){
				value = new String(value.getBytes("GB2312"),"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
		}
		return value;
	}


	/** 
     * @功能 读取流 
     * @param is 输入流
     *  字节数组 
	 * @throws IOException 
     */  
    private String readInputStream(InputStream is) throws IOException {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		while ((len = is.read(b)) != -1 ) {
			baos.write(b, 0, len);
        }  
		baos.flush();
		baos.close();
		String result = new String(baos.toByteArray());
        return result;  
    }  
}
