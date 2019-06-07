package com.j2mvc.framework.dispatcher.reader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSONObject;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Sql;
import com.j2mvc.framework.util.FieldUtil;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 *  读取请求数据
 * 
 * @author 杨朔
 *
 */
public abstract  class BaseReader {
	static final Logger  log = Logger.getLogger(BaseReader.class);
	
	protected HttpServletRequest request;
	// 当前返回Action对象
	protected Object object;
	// 当前方法
	protected Method method;
	// 当前class泛型对象
	protected Class<?> clazz ;
	// 参数类型数组
	protected Class<?>[] types;
	// 参数名称数组
	protected String[] names;
	// 参数值数组
	protected Object[] values;
	// 数据流字符串
	protected String requestBody;
	// JSON数据
	protected JSONObject jsonData;
	// XML数据
	protected Document xmlData;

	public BaseReader(HttpServletRequest request, Method method, Object object) {
		super();
		this.request = request;
		this.method = method;
		this.object = object;
	}
	/**
	 * 返回反射对象
	 * @return
	 */
	public abstract Object result();
	/**
	 * 	获取参数值
	 * @param type
	 * @param name
	 */
	protected abstract Object getParameterValue(Class<?> type,String name);
	
	public JSONObject getJsonData() {
		return jsonData;
	}
	public Document getXmlData() {
		return xmlData;
	}
	public String getRequestBody() {
		return requestBody;
	}
	/**
	 * 获取参数名
	 * @param methodName
	 * @param length
	 * 
	 * @throws NotFoundException
	 */
	protected  String[] getParameterNames(Class<?> clazz,String methodName,int length){
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
	 * 查询指定主键实体对象
	 * @param fields
	 * @param clazz
	 * 
	 */
	protected Object getObjectByPrimaryKey(List<Field> fields,Class<?> clazz){
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
	 * 写入字段数组值
	 * @param fields
	 * @param obj
	 */
	protected void invoke(List<Field> fields,Object obj){
		int size = fields.size();
		for(int i=0;i<size;i++){
			Field field = fields.get(i);
			if(field!=null)
				invoke(field,obj);
		}
	}
	/**
	 * 由子类实现
	 * @param type
	 * @param name
	 * @return
	 */
	protected abstract Object getValue(Class<?> type,String name);
	/**
	 * 写入字段值
	 * @param field
	 * @param obj
	 * @param rs
	 */	
	protected void invoke(Field field,Object obj) {
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
	 * 获取UTF8格式
	 * @param value
	 * 
	 */
	protected String getCharset(String value){
		if(value == null)
			return null;
		try {
			if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value)){
				value = new String(value.getBytes("ISO-8859-1"),Session.encoding);
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
}
