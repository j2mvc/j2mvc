package com.j2mvc.framework.dispatcher.reader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.j2mvc.framework.util.InvokeUtils;

public class FormDataReader extends BaseReader {

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

	@Override
	protected Object getParameterValue(Class<?> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getValue(Class<?> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}


}
