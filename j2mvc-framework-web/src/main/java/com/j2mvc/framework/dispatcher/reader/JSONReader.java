package com.j2mvc.framework.dispatcher.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.j2mvc.framework.Constants;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.StringUtils;
/**
 * 读JSON数据并解析
 * @author 杨朔
 * 2019-6-7
 */
public class JSONReader extends BaseReader{
	static final Logger  log = Logger.getLogger(JSONReader.class);
	public JSONReader(HttpServletRequest request, Method method, Object object) {
		super(request, method, object);
	}
	/**
	 *   读取请求参数的数据
	 *   需要区分请求方法和数据提交类型
	 *   读取JSON数据，并解析出对象返回
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
				//将json字符串转换为json对象
				try {
					requestBody();
					jsonData = JSONObject.parseObject(requestBody);
					// 解析JSON数据
					for (int i = 0; i < types.length; i++) {
						Class<?> type = types[i];
						values[i] = getParameterValue(type, names[i]);
					}
					InvokeUtils.invoke(clazz, "setJsonData", object,  new Object[]{jsonData},JSONObject.class);
					InvokeUtils.invoke(clazz, "setRequestBody", object,  new Object[]{requestBody},String.class);
					try {
						return method.invoke(object,values);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException e) {
					log.error("JSON格式解析错误！");
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
	 * 读取数据流字符串
	 * @return
	 */
	public void requestBody() {
		try {
			request.setCharacterEncoding(Session.encoding);
		} catch (UnsupportedEncodingException e1) {
			log.error("字符编码错误！");
		}
		// 读取请求内容
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(),Session.encoding));
		} catch (UnsupportedEncodingException e1) {
			log.error("读取字符编码错误！"); 
		} catch (IOException e1) {
			log.error("读取数据失败！");
		}
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			requestBody = sb.toString();
		} catch (IOException e1) {
			log.error("读取数据失败！");
		}
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
			// 解析JSON对象
			log.info("解析JSON对象:"+type);
			return JSONObject.parseObject(requestBody, type);
		}
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
		if(String.class.isAssignableFrom(type)) {
			value =  jsonData.getString(name);
		}				
		else if(Integer.class.isAssignableFrom(type) 
				|| Short.class.isAssignableFrom(type)
				|| short.class.isAssignableFrom(type)
				|| int.class.isAssignableFrom(type)){
			value = jsonData.getInteger(name);
		}else if(Long.class.isAssignableFrom(type) 
				|| long.class.isAssignableFrom(type)){
			value = jsonData.getLong(name);
		}else if(Float.class.isAssignableFrom(type)
				|| float.class.isAssignableFrom(type)){
			value = jsonData.getFloat(name);
		}
		else if(Double.class.isAssignableFrom(type)
				|| double.class.isAssignableFrom(type)){
			value = jsonData.getDouble(name);
		}else if(Boolean.class.isAssignableFrom(type)
				|| boolean.class.isAssignableFrom(type)){
			value = jsonData.getBoolean(name);
		}else if(Time.class.isAssignableFrom(type))
			try {
				String str = jsonData.getString(name);
				value = Constants.DEFAULT_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		else if(Date.class.isAssignableFrom(type)){
			try {
				String str = jsonData.getString(name);
				value = Constants.DEFAULT_DATE_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}else if(byte[].class.isAssignableFrom(type)){
			// 无法解析
		}else if(String[].class.isAssignableFrom(type)){
			value = JSONObject.parseArray(requestBody, String.class);
		}	
		return value;
	}
}
