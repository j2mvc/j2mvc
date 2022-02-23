package com.j2mvc.framework.dispatcher.reader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.j2mvc.framework.Constants;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.json.JSONParse;

public class FormDataReader extends BaseReader {
	Map<String,String> textData;
	static final Logger  log = Logger.getLogger(DefaultReader.class);

	public FormDataReader(HttpServletRequest request, Method method, Object object) {
		super(request, method, object);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object result() {
		clazz = object.getClass();
		types = method.getParameterTypes();
		int length = types.length;
		log.debug("length="+length);
		if(length>0){
			// 请求参数名数组
			names = getParameterNames(clazz,method.getName(), length);
			log.debug("names="+names.length); 
			values = new Object[types.length];
			if(names!=null){
				getTextData();
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
		}// 没有参数，执行当前Action方法
		return InvokeUtils.invoke(clazz, method.getName(), object, null);
	}

	@Override
	protected Object getParameterValue(Class<?> type, String name) {
		// 基础类型获取值
		return getValue(type, name);
	}

	private void getTextData() {
		textData = new HashMap<String,String>();
		// 上传实例
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

		List<FileItem> list = null;
		try {
			list = servletFileUpload.parseRequest(request);
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		for(int i=0;i<list.size();i++) {
			// 得到文件对象
			FileItem item = list.get(i);
			// 文本数据
			if(item.getFieldName()!=null) {
				log.info("正在接收文本数据："+item.getFieldName()+":"+item.getString());
				textData.put(item.getFieldName(),item.getString());
			}
		}
	}
	
	@Override
	protected Object getValue(Class<?> type, String name) {
		Object value = null;
		String str =  textData.get(name);
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
		}else {
			if (type.isArray()) {
				Class<?> clazz;
				try {
					clazz = Class.forName(type.getCanonicalName().replace("[]", ""));
					return JSONParse.parseArray(str, clazz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			} else if (Map.class.isAssignableFrom(type) || HashMap.class.isAssignableFrom(type)) {
				log.error("无法解析Map类型，请使用对象模式接收：" + name);
				return null;
			} else if (Set.class.isAssignableFrom(type) || HashSet.class.isAssignableFrom(type)
					|| List.class.isAssignableFrom(type) || ArrayList.class.isAssignableFrom(type)) {
				log.error("不支集合类型，请使用数组类型接收：" + name);
				return null;
			} else {
				// 尝试对象模式接收
				return JSONParse.parseObject(str, type);
			}
		}
		return value;
	}


}
