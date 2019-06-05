package com.j2mvc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.PrimaryKey;

public class FieldUtil {
	static Logger log = Logger.getLogger(Field.class.getName());

	/**
	 * 获取所有包含父类的属性字段
	 * @param fields
	 * @param clazz
	 */
	public static List<Field> getFields(List<Field> fields,Class<?> clazz){
		if(fields == null){
			fields = new ArrayList<Field>();
		}
		Field[] thisFields = clazz.getDeclaredFields();
		for(Field field:thisFields){
			if(!fields.contains(field))
				fields.add(field);
		}
		if(clazz.getSuperclass()!=null){
			fields = getFields(fields, clazz.getSuperclass());
		}
		return fields;
	}
	/**
	 * 获取指定属性名的属性字段
	 * @param name
	 */
	public static Field getField(String name,Class<?> clazz){
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (SecurityException e) {
			//log.warning("at " + clazz+" SecurityException:"+e.getMessage());
		} catch (NoSuchFieldException e) {
			//log.warning("在类 " + clazz+" 未找到字段 "+e.getMessage()+",将递归父类查找。");
		}
		if(field == null && clazz.getSuperclass()!=null){
			field = getField(name, clazz.getSuperclass());
		}
		return field;
	}
	/**
	 * 根据属性名查找column注释
	 * @param name
	 * @param clazz
	 */
	public static Column getColumn(String name,Class<?> clazz){
		Field field = getField(name, clazz);
		if(field!=null){
			/* 注释字段,对应数据表字段 */
			return field.getAnnotation(Column.class);			
		}
		return null;
	}
	/**
	 * 获取外键对象之主键数据类型
	 * @param foreignClass
	 */
	public static Field getForeignKey(Class<?> foreignClass){
		// 外键对象之主键注解
		PrimaryKey primaryKey = foreignClass.getAnnotation(PrimaryKey.class);
		// 主键对应的属性
		return getField(primaryKey.name(), foreignClass);
	}
}
