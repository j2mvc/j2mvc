package com.j2mvc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class InvokeUtils {
	private static Logger log = Logger.getLogger("fixwork InvokeUtils");

	/**
	 * 执行方法
	 * @param clazz
	 * @param methodName
	 * @param obj
	 * @param invokeObject
	 */
	public static Object invoke(Class<?> clazz,String methodName,Object obj, Object[]invokeObj,Class<?>...invokeClass){
		Method method = getMethod(clazz,methodName,invokeClass);
		if(method == null){
			log.warning("在类"+clazz.getCanonicalName()+"以及父类未找到方法:"+methodName+".");
			return null;
		}
		try {
			if(invokeObj!=null)
				return method.invoke(obj,invokeObj);
			else {
				return method.invoke(obj);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取类以及所有父类的指定方法
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static Method getMethod(Class<?> clazz,String methodName, Class<?>...invokeClass){
		Method method = null;
		try {
			if(invokeClass!=null)
				method = clazz.getMethod(methodName, invokeClass);
			else
				method = clazz.getDeclaredMethod(methodName);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		if(method == null && clazz.getSuperclass()!=null){
			method = getMethod(clazz.getSuperclass(), methodName,invokeClass);
		}
		return method;
	}
	
}
