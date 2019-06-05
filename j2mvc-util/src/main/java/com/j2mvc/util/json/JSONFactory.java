package com.j2mvc.util.json;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j2mvc.util.mapping.JSONField;
import com.j2mvc.util.mapping.JSONObjectStr;
import com.j2mvc.util.mapping.NotJSONField;

/**
 * JSON工厂，需要在对象添加注解JSONField,JSONObjectStr 
 * @author 杨朔
 * 2014年1月14日
 */
public class JSONFactory{
	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	Logger logger = Logger.getLogger(getClass().getSimpleName());
	/**
	 * 简单对象生成JSON对象
	 * @param jsonName 
	 * @param object
	 *  
	 * @throws JSONException 
	 */
	public JSONObject toSimpleJsonObject(String jsonName, Object object) {		
		if(jsonName!=null && !jsonName.trim().equals("") && object != null) {
			JSONObject json = new JSONObject();
			put(json, jsonName, "", object);
			return json;
		}
		return null;
	}

	/**
	 * objects集合生成JSON对象
	 * @param objects 需要生成JSON的对象集合
	 */
	public JSONObject toJsonObject(Object... objects) {
		
		JSONObject json = new JSONObject();		
		for(int i=0;i<objects.length;i++){
			Object object = objects[i];
			if(object!=null){
				Class<?> clazz = object.getClass();
				// JSON对象名
				JSONObjectStr jsonObjectStr = clazz.getAnnotation(JSONObjectStr.class);
				// 创建JSON对象
				try {
					String jsonName = jsonObjectStr!=null?jsonObjectStr.value():"";
					if(jsonName !=null && !jsonName.equals("")){
						json.put(jsonName, toJsonObject(object));
					}else{
						build(json,object);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}
	/**
	 * 生成JSONArray对象，对象可以任意
	 * @param object
	 */
	public JSONArray toSimpleJsonArray(Object...object){
		return toJsonArray(object);
	}
	
	/**
	 * 生成JSON对象
	 * @param object 
	 * @param hasOuter 如果hasOuter为真，则在JSON对象外层嵌套顶层JSON
	 */
	public Object toJsonObject(Object object,boolean hasOuter) {
		if(object == null)
			return null;
		Class<?> clazz = object.getClass();
		// JSON对象名
		JSONObjectStr jsonObjectStr = clazz.getAnnotation(JSONObjectStr.class);
		
		// 创建JSON对象
		try {
			if(hasOuter && jsonObjectStr!=null && !jsonObjectStr.value().equals("")){
				JSONObject json = new JSONObject();
				json.put(jsonObjectStr.value(), toJsonObject(object));
				return json;
			}else{
				return toJsonObject(object);
			}
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * 获取值，装入参数集合
	 * @param json
	 * @param jsonName
	 * @param name
	 * @param object
	 */
	@SuppressWarnings("unchecked")
	private void put(
			JSONObject json,
			String jsonName,
			String name,
			Object object){
		// 获取值
		PropertyDescriptor pd;
		try {
			Object value = null;
			// 如果name为空，创建object
			if(name!=null && !name.trim().equals("")){
				try {
					pd = new PropertyDescriptor(name, object.getClass());
					Method method = pd.getReadMethod();
					if(method!=null){
						value = method.invoke(object);			
						value = value!=null?value:"";
					}
				} catch (IntrospectionException e) {
					//logger.warning(e.getMessage());
				} catch (IllegalAccessException e) {
					//logger.warning(e.getMessage());
				} catch (InvocationTargetException e) {
					//logger.warning(e.getMessage());
				}
			}else{
				value = object!=null?object:"";
			}
			if(value == null || value.equals("")){
				return;
			}
			// 判断字段类型
			if (value instanceof Object[]) {
				// 如果是数组
				JSONArray array = toJsonArray((Object[])value);
				if(array!=null)
					json.put(jsonName,array);
			} else if(value instanceof Collection) {
				// 如果是集合,生成JSONArray
				JSONArray array = toJsonArray((Collection<?>)value);
				if(array!=null)
					json.put(jsonName,array);
			} 
			else if (value instanceof Map) {
				// 如果是Map,生成JSONArray
				JSONObject jsonObject = toJsonMap((Map<Object,Object>)value);
				if(jsonObject!=null)
					json.put(jsonName,jsonObject);
			} else if(value instanceof Date) {
				// 如果是日期
				json.put(jsonName,formater.format(value));
			}else if(value instanceof Integer 
					|| value instanceof Long
					|| value instanceof Boolean
					|| value instanceof Double
					|| value instanceof Number
					|| value instanceof String
					|| value instanceof Double
					|| value instanceof Float
					|| value instanceof Short) {
				// 如果是基础数据类型
				json.put(jsonName,value);
			}else{
				// 如果是其他对象
				Object jsonObject = toJsonObject(value);
				if(jsonObject!=null)
					json.put(jsonName,toJsonObject(value));
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 根据对象生成JSON对象
	 * @param object
	 */
	@SuppressWarnings("unchecked")
	public Object toJsonObject(Object object){
		if(object == null)
			return null;
		JSONObject json = new JSONObject();
		if(object instanceof Object[] || object instanceof Collection){
			return toJsonArray(object);
		}else if (object instanceof Map) {
			buildMap(json,(Map<Object, Object>)object);
		}else{
			build(json,object);
		}
		return json;
	}

	/**
	 * 根据对对象数组生成JSON数组
	 * @param object
	 */
	public JSONArray toJsonArray(Object object){
		List<Object> jsons = new ArrayList<Object>();
		if(object instanceof Collection){
			Iterator<?> iter = ((Collection<?>)object).iterator();
			while(iter.hasNext()){
				Object value = iter.next();
				if(value instanceof Integer 
						|| value instanceof Long
						|| value instanceof Boolean
						|| value instanceof Double
						|| value instanceof Number
						|| value instanceof String
						|| value instanceof Double
						|| value instanceof Float
						|| value instanceof Short){
					jsons.add(value);
				}else{
					jsons.add(toJsonObject(value));
				}
			}
		}else if(object instanceof Object[]){
			Object[] objects = (Object[])object;
			for(int i=0;i<objects.length;i++){
				Object value = objects[i];
				if(value instanceof Integer 
						|| value instanceof Long
						|| value instanceof Boolean
						|| value instanceof Double
						|| value instanceof Number
						|| value instanceof String
						|| value instanceof Double
						|| value instanceof Float
						|| value instanceof Short){
					jsons.add(value);
				}else{
					jsons.add(toJsonObject(value));
				}
			}
		}
		return jsons!=null ? new JSONArray(jsons):null;
	}

	/**
	 * 根据对象解析字段生成JSON对象
	 */
	private void build(JSONObject json,Object object){
		List<Field> fields = JSONUtils.getFields(null,object.getClass());
		for(Field field:fields){
			/* 不输出JSON字段名 */
			NotJSONField notJSONField = field.getAnnotation(NotJSONField.class);
			if(notJSONField == null){
				/* JSON字段名 */
				JSONField jsonField = field.getAnnotation(JSONField.class);
				String jsonName = jsonField!=null?jsonField.value():field.getName();
				put(json,jsonName,field.getName(),object);
			}
		}
	}
	/**
	 * 根据MAP对象解析字段生成JSON对象
	 */
	private void buildMap(JSONObject json,Map<Object, Object> map){
		Set<Entry<Object,Object>> set =  map.entrySet();
		Iterator<Entry<Object,Object>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<Object,Object> entry = iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			try {
				if(value instanceof Integer 
						|| value instanceof Long
						|| value instanceof Boolean
						|| value instanceof Double
						|| value instanceof Number
						|| value instanceof String
						|| value instanceof Double
						|| value instanceof Float
						|| value instanceof Short
						|| value instanceof Date){
					try {
						json.put(key.toString(), value);
					} catch (JSONException e) {
						logger.warning(e.getMessage());
					}
				}else {
					json.put(key.toString(), toJsonObject(value));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 根据MAP对象解析字段生成JSON对象
	 */
	public JSONObject toJsonMap(Map<Object, Object> map){
		Set<Entry<Object,Object>> set =  map.entrySet();
		Iterator<Entry<Object,Object>> iterator = set.iterator();
		JSONObject jsonObject = new JSONObject();
		while (iterator.hasNext()) {
			Entry<Object,Object> entry = iterator.next();
			String key = entry.getKey().toString();
			Object value = entry.getValue();

			if(value instanceof Integer 
					|| value instanceof Long
					|| value instanceof Boolean
					|| value instanceof Double
					|| value instanceof Number
					|| value instanceof String
					|| value instanceof Double
					|| value instanceof Float
					|| value instanceof Short
					|| value instanceof Date){
				try {
					jsonObject.put(key, value);
				} catch (JSONException e) {
					logger.warning(e.getMessage());
				}
			}else{
				try {
					jsonObject.put(key, toJsonObject(value));
				} catch (JSONException e) {
					logger.warning(e.getMessage());
				}
			}
		}
		return jsonObject;
	}
}
