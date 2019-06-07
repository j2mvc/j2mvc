package com.j2mvc.framework.dispatcher.reader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.j2mvc.framework.Constants;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.util.DOMUtils;
import com.j2mvc.framework.util.FieldUtil;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.StringUtils;

/**
 * XML数据读取
 * 
 * @author 杨朔 2019-6-7
 *
 */
public class XMLReader extends BaseReader {

	static final Logger log = Logger.getLogger(XMLReader.class);

	
	public XMLReader(HttpServletRequest request, Method method, Object object) {
		super(request, method, object);
	}
	/**
	 *   读取请求参数的数据
	 *   需要区分请求方法和数据提交类型
	 * @param method
	 * @param object
	 * @return 
	 */
	public Object result(){
		clazz = object.getClass();
		types = method.getParameterTypes();
		int length = types.length;
		if(length>0){
			// 请求参数名数组
			names = getParameterNames(clazz,method.getName(), length);
			values = new Object[types.length];
			if(names!=null){
				loadDom();
				for (int i = 0; i < types.length; i++) {
					Class<?> type = types[i];
					values[i] = getParameterValue(type, names[i]);
				}
				InvokeUtils.invoke(clazz, "setXmlData", object,  new Object[]{xmlData},Document.class);
				InvokeUtils.invoke(clazz, "setRequestBody", object,  new Object[]{requestBody},String.class);
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
			// 没有参数，执行当前Action方法
			return InvokeUtils.invoke(clazz, method.getName(), object, null);
		}
		return null;
	}

	/**
	 * 	获取参数值
	 * @param type
	 * @param name
	 * 
	 */
	@Override
	protected Object getParameterValue(Class<?> type,String name){
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
			// 基础类型获取值
			return getValue(type, name);
		}else {
			// 解析XML为对象
			try {
				List<Field> fields = FieldUtil.getFields(null, type);
				Object object = type.newInstance();
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
	 * 写入字段数组值
	 * @param fields
	 * @param obj
	 */
	@Override
	protected void invoke(List<Field> fields,Object obj){
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
	@Override
	protected void invoke(Field field,Object obj) {
		String name = field.getName();
		/* 数据表数据类型 */		
		Class<?> type = field.getType();	
		try {
			Object value = getValue(type,name);
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
	 * 加载XML
	 * 
	 * @param inputStream
	 */
	private void loadDom() {
		try {
			InputStream inputStream = request.getInputStream();
			xmlData = DOMUtils.parseXMLDocument(inputStream);
			requestBody = DOMUtils.toStringFromDoc(xmlData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取值
	 * XML必须设置ID，才能获取到值
	 * @param type
	 * @param name
	 * 
	 */
	@Override
	protected Object getValue(Class<?> type,String name){
		Object value = null;
		String str = null;
		Element elem =  xmlData.getElementById(name);
		log.info(name+"="+(elem!=null?elem.getTextContent():"elem is null."));
		if(elem!=null) {
			str = elem.getTextContent();
		}
		if(str == null) {
			return null;
		}
		str = getCharset(str);
		if(String.class.isAssignableFrom(type)) {
			value = str;	
		}else if(Integer.class.isAssignableFrom(type) 
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
}
