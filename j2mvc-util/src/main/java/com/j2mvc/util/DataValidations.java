package com.j2mvc.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.j2mvc.util.Error;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.Foreign;

/**
 * 验证类 @author 杨朔
 * 2014/1/21
 *
 */
public class DataValidations {

	/**
	 * 验证对象内容长度，返回错误Map或true;
	 * @param object
	 */
	public static List<Error> dataTooLong(Object object){
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<Error> errors = new ArrayList<Error>();
		for(int i=0;i<fields.length;i++){
			/* 注释字段,对应数据表字段 */
			Column column = fields[i].getAnnotation(Column.class);

			/* 注释字段,对应关联对象字段 */
			Foreign foreign =  fields[i].getAnnotation(Foreign.class);
			if(column!=null && foreign == null){
				String name = fields[i].getName();
				String value = getValue(fields[i].getName(),object);
				Integer length = column.length();
				if(length>0){
					if(value.length() > length){
						Error error = new Error(Error.ERROR_TOOLONG, "字段“"+name+"”值太长，"
								+ "字数不能超过"+length+"个字.");
						errors.add(error);
					}
				}
			}
		}
		if(errors.isEmpty()){
			return null;
		}else{
			return errors;
		}
	}
	private static String getValue(
			String name,
			Object object){
		// 获取值
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(name, object.getClass());
			Method method = pd.getReadMethod();
			Object value = method.invoke(object);			
			return value!=null?String.valueOf(value):"";
		}catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}
}

