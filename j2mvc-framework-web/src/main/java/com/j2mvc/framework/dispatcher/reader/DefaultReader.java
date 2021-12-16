package com.j2mvc.framework.dispatcher.reader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.j2mvc.framework.Constants;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.util.FieldUtil;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.StringUtils;
/**
 * GET请求方法读取数据
 * @author 杨朔
 * 2019-6-7
 *
 */
public class DefaultReader extends BaseReader{

	static final Logger  log = Logger.getLogger(DefaultReader.class);


	public DefaultReader(HttpServletRequest request, Method mothod, Object object) {
		super(request, mothod, object);
		// TODO Auto-generated constructor stub
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
		}
		// 没有参数，执行当前Action方法
		return InvokeUtils.invoke(clazz, method.getName(), object, null);
	}
	/**
	 * 	获取参数值
	 * @param type
	 * @param name
	 * 
	 */
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
	 * 获取值
	 * @param type
	 * @param name
	 * 
	 */
	@Override
	protected Object getValue(Class<?> type,String name){
		Object value = null;
		String str =  request.getParameter(name);
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
