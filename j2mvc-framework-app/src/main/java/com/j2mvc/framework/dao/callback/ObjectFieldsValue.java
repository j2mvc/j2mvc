package com.j2mvc.framework.dao.callback;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.util.FieldUtil;
/**
 * 读取对象的值
 * @author 杨朔
 * 2014年1月13日
 */
public class ObjectFieldsValue { 
	Logger log = Logger.getLogger(getClass().getName());
	
	private Object object;

	/**
	 * 构造器
	 * @param object
	 */
	public ObjectFieldsValue(Object object) {
		super();
		this.object = object;
	}

	/**
	 * 读取对象属性值
	 *  Object[]
	 */
	public Object[] getValues(boolean addKey){
		if(object == null)
			return null;
		List<Object> params = new ArrayList<Object>();
		Class<?> clazz = object.getClass();
		// 主键
		PrimaryKey primaryKey = clazz.getAnnotation(PrimaryKey.class);

		List<Field> fields = FieldUtil.getFields(null,clazz);
		for(Field field:fields){
			/* 注释字段,对应数据表字段 */
			Column column = field.getAnnotation(Column.class);
			/* 注释字段,对应关联对象字段 */
			Foreign foreign =  field.getAnnotation(Foreign.class);

			if(column != null){
				String name = field.getName();
				if(name.equals(primaryKey.name())){
					if(primaryKey.autoIncrement()){
						// 如果当前字段为主键，且自动增长，则忽略
						continue;
					}else{
						// 如果当前字段为主键，不为自动增长，主键不能为空
						Object value = getValue(name,clazz);
						value = value!=null?value:"";
						if(value.equals("")){
							log.error("主键为空");
							return null;
						}
					}
				}
				Object value = getValue(name,clazz);
				if(foreign != null){
					// 有关联对象，获取对象主键值
					Class<?> foreignClass = field.getType();
					PrimaryKey foreignKey = foreignClass.getAnnotation(PrimaryKey.class);
					value = getValue(foreignKey.name(), foreignClass, value);
				}				
				params.add(value);
			}
			
		}	
		/* 如果最后需要增加主键值 */
		if(addKey){
			Object value = getValue(primaryKey.name(),clazz);
			params.add(value);
		}
		return params!=null?params.toArray():null;
	}

	/**
	 * 获取值
	 * @param name
	 * @param clazz
	 */
	public Object getValue(String name,Class<?> clazz){
		// 获取值
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(name, clazz);
			Method method = pd.getReadMethod();
			return method.invoke(object);
		} catch (IntrospectionException e) {
			e.printStackTrace();
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
	 * 获取值
	 * @param name
	 * @param clazz
	 * @param object
	 */
	public Object getValue(String name,Class<?> clazz,Object object){
		// 获取值
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(name, clazz);
			Method method = pd.getReadMethod();
			return object!=null?method.invoke(object):null;
		} catch (IntrospectionException e) {
			log.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
		}
		return null;
	}
}
