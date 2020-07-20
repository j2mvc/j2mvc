package com.j2mvc.util.json;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工具类
 * @author 杨朔 * 2014年6月10日
 */
public class JSONUtils {
	public static final String JSON_TYPE_STRING = "string";
	public static final String JSON_TYPE_NUMBER = "number";
	public static final String JSON_TYPE_ARRAY = "array";
	public static final String JSON_TYPE_OBJECT = "object";
	public static final String JSON_TYPE_BOOLEAN = "boolean";

	/**
	 * 获取JSON字符串的类型
	 * @param json
	 */
	public static String getJSONType(String json){
		if(json != null && !json.equals("")){
			if(json.startsWith("{") && json.endsWith("}")){
				// 为JSON对象
				return JSON_TYPE_OBJECT;
			}else if(json.startsWith("[") && json.endsWith("]")){
				return JSON_TYPE_ARRAY;
			}else if(json.matches("([-]?\\d+)|([-]?[\\d+][.][\\d+])")){
				return JSON_TYPE_NUMBER;
			}else if(json.equals("true")|| json.equals("false")){
				return JSON_TYPE_BOOLEAN;
			}else {
				return JSON_TYPE_STRING;
			}
		}
		return "";
	}

	/**
	 * 获取所有包括父类字段
	 * @param fields
	 * @param clazz
	 */
	public static List<Field> getFields(List<Field> fields,Class<?> clazz){
		fields = fields == null?new ArrayList<Field>():fields;
		Field[] thisFields  = clazz.getDeclaredFields();
		for(int i= 0 ;i<thisFields.length;i++){
			Field field = thisFields[i];;
			if(!fields.contains(field) 
					&& !Modifier.isPublic(field.getModifiers())
					&& !Modifier.isFinal(field.getModifiers())){
				fields.add(field);
			}
		}
		if(clazz.getSuperclass()!=null){
			getFields(fields,clazz.getSuperclass());
		}
		return fields;
	}
	/**
	 * 写入字段值
	 * @param field
	 * @param obj
	 * @param value
	 */
	public static <T> void setValue(Field field,Object obj,Object value) {
		try {
			Class<?> fieldType = field.getType();
			// 执行写入操作
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
			Method wm = pd.getWriteMethod();
			if (String.class.isAssignableFrom(fieldType)) {
				wm.invoke(obj, value.toString());	
			} else if (Float.class.isAssignableFrom(fieldType) || float.class.isAssignableFrom(fieldType)) {
				wm.invoke(obj, Float.parseFloat(value.toString()));
			} else if (Double.class.isAssignableFrom(fieldType) ) {
				wm.invoke(obj, Double.valueOf(value.toString()) );
			} else if (Integer.class.isAssignableFrom(fieldType) ) {
				wm.invoke(obj, Integer.valueOf(value.toString()) );
			} else if (Long.class.isAssignableFrom(fieldType) ) {
				wm.invoke(obj, Long.valueOf(value.toString()) );
			} else if (Number.class.isAssignableFrom(fieldType) ) {
				wm.invoke(obj, (Number) value);
			} else if (List.class.isAssignableFrom(fieldType) ) {
				wm.invoke(obj, value);
			} else if (Set.class.isAssignableFrom(fieldType) ) {
				if(value instanceof List){
					Set<T> set = new HashSet<T>();
					set.addAll((Collection<? extends T>) value);
					wm.invoke(obj, set);
				}
			} else if (value instanceof List && Object[].class.isAssignableFrom(fieldType)) {
				wm.invoke(obj,((List<T>)value).toArray());	
			} else if (Date.class.isAssignableFrom(fieldType)) {	
				try {
					wm.invoke(obj, JSONParse.formater.parse(value.toString()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else {
				wm.invoke(obj, value);	
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取泛型类型
	 * @param field
	 */
	public static List<Class<?>> getGenericTypes(Field field){
		List<Class<?>> types = new ArrayList<Class<?>>();
		Type fieldType = field.getGenericType();
		if (fieldType instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) fieldType;
			Type[] genericTypes = paramType.getActualTypeArguments();	
			if (genericTypes != null) {
				for(int i=0;i<genericTypes.length;i++){
					try{
						types.add((Class<?>)genericTypes[i]);
					}catch(Exception e) {
						types.add((Class<?>)genericTypes[i].getClass());
					}
				}
			}
		}
		return types;
	}
}
