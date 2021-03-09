package com.j2mvc.framework.dispatcher.reader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.j2mvc.framework.Constants;
import com.j2mvc.framework.action.ActionUpload;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.action.UploadBean;
import com.j2mvc.framework.upload.Upload;
import com.j2mvc.framework.util.FieldUtil;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.StringUtils;

public class FileReader extends BaseReader {
	
	private ActionUpload actionUpload;
	private HttpServletResponse response;
	private UploadBean uploadBean;
	

	static final Logger  log = Logger.getLogger(DefaultReader.class);

	public FileReader(HttpServletRequest request, Method method, Object object,
			HttpServletResponse response,
			ActionUpload actionUpload) {
		super(request, method, object);
		this.response = response;
		this.actionUpload = actionUpload;
		
		// 接收上传
		upload();
	}

	@Override
	public synchronized  Object result() {
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
		}else {
			// 没有参数，执行当前Action方法
			return InvokeUtils.invoke(clazz, method.getName(), object, null);
		}
		return null;
	}

	private synchronized void upload() {
		Upload upload =  new Upload(request, response);
		
		if(!StringUtils.isEmpty(actionUpload.getDirname()))
			upload.setDirName(actionUpload.getDirname());
		
		if(!StringUtils.isEmpty(actionUpload.getExts()))
			upload.setExts(actionUpload.getExts());
		
		if(!StringUtils.isEmpty(actionUpload.getSavePath()))
			upload.setSavePath(actionUpload.getSavePath());
		
		if(!StringUtils.isEmpty(actionUpload.getSaveUrl()))
			upload.setSaveUrl(actionUpload.getSaveUrl());

		if(actionUpload.getMaxSize() > 0)
			upload.setMaxSize(actionUpload.getMaxSize());
		
		upload.setKeepOriginName(actionUpload.isKeepOriginName());
		upload.setInputStreamOnly(actionUpload.isInputStreamOnly());
		
		try { 
			upload.execute();
			uploadBean = new UploadBean();
			uploadBean.setErrors(upload.getErrors());
			uploadBean.setFileList(upload.getFileList());
			uploadBean.setTextData(upload.getTextData());
		} catch (Exception e) { 
			e.printStackTrace();  
		}  
	}

	public UploadBean getUploadBean() {
		return uploadBean;
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
