package com.j2mvc.util.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析JSON字符为对象，需要在对象添加注解JSONField,JSONObjectStr 
 * @author 杨朔
 * 2014年6月10日
 */
public class JSONParse{
	public static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static Logger log = Logger.getLogger(JSONParse.class.getName());

	/**
	 * 解析未知的对象,可以是任意对象
	 * @param json
	 * @param clazz
	 */
	public static Object parse(String json, Class<?> clazz){
		String jsonType = JSONUtils.getJSONType(json);
		if(clazz.isAssignableFrom(String.class)){
			return json;
		}else if(jsonType.equals(JSONUtils.JSON_TYPE_BOOLEAN)){
			return Boolean.parseBoolean(json);
		}else if(jsonType.equals(JSONUtils.JSON_TYPE_NUMBER)){
			return parseNumber(json, clazz);
		}else if(jsonType.equals(JSONUtils.JSON_TYPE_ARRAY)){
			return parseArray(json, clazz);
		}else if(jsonType.equals(JSONUtils.JSON_TYPE_OBJECT)){
			return parseObject(json, clazz);
		}else{
			return json;
		}
	}
	/**
	 * 解析为实体对象
	 * @param json
	 * @param clazz
	 */
	public static Object parseObject(String json,Class<?> clazz){
		List<Field> fields = JSONUtils.getFields(null,clazz);
		JSONObject jsonObject = null;
		Object object = null;
		try {
			jsonObject = new JSONObject(json);
			object = clazz.newInstance();
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (InstantiationException e) {
			log.warning("实例化失败，必须有空构造方法."+e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if(object!=null)
		for(final Field field:fields){
			/* JSON字段名 */
			JSONField jsonField = field.getAnnotation(JSONField.class);
			String jsonName = jsonField!=null?jsonField.value():field.getName();
			try {
				Object value = parseField(field, jsonObject.getString(jsonName));
				if(value!=null){
					JSONUtils.setValue(field, object, value);
				}
			} catch (JSONException e) {
				//log.warning(e.getMessage());
			}
		}
		return object;
	}
	/**
	 * 解析字段
	 * @param field
	 * @param value
	 */
	private static Object parseField(Field field,String json){
		if(json == null){
			return null;
		}
		Class<?> type = field.getType();
		String jsonType = JSONUtils.getJSONType(json);
		if(jsonType.equals(JSONUtils.JSON_TYPE_ARRAY)){
			return parseArray(json, field);
		}else if(jsonType.equals(JSONUtils.JSON_TYPE_OBJECT) && Map.class.isAssignableFrom(type)){
			return parseMap(json, field);
		}else {
			return parse(json, type);
		}
	}
	/**
	 * 解析为MAP对象
	 * @param json
	 * @param clazz
	 */
	private static Map<Object, Object> parseMap(String json,Field field){
		Map<Object, Object> map = null;
		String clazzName = field.getClass().getCanonicalName();
		Class<?> clazz;
		try {
			clazz = Class.forName(clazzName);
			if(clazz.getCanonicalName().equals("java.util.Hashtable")){
				map = new Hashtable<Object, Object>();
			}else {
				map = new HashMap<Object, Object>();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		List<Class<?>> subTypes = JSONUtils.getGenericTypes(field);
		if(subTypes.size()>1){
			Class<?> keyType = subTypes.get(0);
			Class<?> valueType = subTypes.get(1);
			return parseMap(json, keyType, valueType,map);
		}
		return map;
	}
	/**
	 * 解析为MAP对象
	 * @param <K>
	 * @param <V>
	 * @param json
	 * @param keyType
	 * @param valueType
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> parseMap(String json,Class<?> keyType,Class<?> valueType,Map<K, V> map){
		map = map!=null?map:new HashMap<K, V>();
		try {	
			JSONObject jsonObject = new JSONObject(json);
			Iterator<String> iterator =  jsonObject.keys();
			while (iterator.hasNext()) {
				String keyString = (String) iterator.next();
				K key = (K) parse(keyString, keyType);
				V value = (V) parse(jsonObject.getString(keyString), valueType);
				map.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return map;
	}
	/**
	 * 解析为数组
	 * @param json
	 * @param field  字段
	 */
	private static Object parseArray(String json,Field field){
		Class<?> clazz = field.getType();
		String subTypeName = clazz.getCanonicalName().replace("[]", "");
		Class<?> subType = null;
		try {
			subType = Class.forName(subTypeName);
		} catch (ClassNotFoundException e) {
			return null;
		}
		if(String[].class.isAssignableFrom(clazz)
					|| Integer[].class.isAssignableFrom(clazz)
					|| Long[].class.isAssignableFrom(clazz)
					|| Double[].class.isAssignableFrom(clazz)
					|| Float[].class.isAssignableFrom(clazz)
					|| Boolean[].class.isAssignableFrom(clazz)
					|| Number[].class.isAssignableFrom(clazz)
					|| int[].class.isAssignableFrom(clazz)
					|| long[].class.isAssignableFrom(clazz)
					|| double[].class.isAssignableFrom(clazz)
					|| float[].class.isAssignableFrom(clazz)
					|| boolean[].class.isAssignableFrom(clazz)){
			return parseArray(json, subType);
		}else {
			List<Class<?>> subTypes = JSONUtils.getGenericTypes(field);
			if(subTypes.size()>0){
				return parseList(json, subTypes.get(0));
			}else {
				return parseArray(json, subType);
			}
		}
	}
	/**
	 * 解析为数组
	 * @param json
	 * @param clazz 字段元类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> Object[] parseArray(String json,Class<T> clazz){
		Object[] array = null;
		try {
			JSONArray jsonArray = new JSONArray(json);
			int length = jsonArray.length();
			if(String.class.isAssignableFrom(clazz)){
				array = new String[length];
			}else if(Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)){
				array = new Integer[length];
			}else if(Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)){
				array = new Long[length];
			}else if(Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)){
				array = new Double[length];
			}else if(Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)){
				array = new Float[length];
			}else if(Boolean.class.isAssignableFrom(clazz)|| boolean.class.isAssignableFrom(clazz)){
				array = new Boolean[length];
			}else if(Number.class.isAssignableFrom(clazz) ){
				array = new Number[length];
			}else {
				array = (T[]) Array.newInstance( clazz , length ) ;
			}
			if(array!=null)
			for (int i = 0; i < jsonArray.length(); i++) {
				 String jsonString = jsonArray.getString(i);
				 array[i] = parse(jsonString,clazz);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}
	/**
	 * 解析为集合
	 * @param json
	 * @param clazz 字段元类型
	 */
	public static <T> List<T> parseList(String json,Class<T> clazz){
		try {
			JSONArray jsonArray = new JSONArray(json);
			List<T> list = new ArrayList<T>();
			for (int i = 0; i < jsonArray.length(); i++) {
				 String jsonString = jsonArray.getString(i);
				 @SuppressWarnings("unchecked")
				 T object = (T) parse(jsonString, clazz);
				 list.add(object);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return null;
	}

	/**
	 * 解析为字符
	 * @param json
	 */
	public static String parseString(String json){
		return json;
	}
	/**
	 * 解析为数值
	 * @param json
	 */
	public static Number parseNumber(String json,Class<?> type){
		if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
			return Long.parseLong(json.toString());
		} else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
			return Integer.parseInt(json.toString());
		}  else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
			return Double.parseDouble(json.toString());
		} else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
			return Float.parseFloat(json.toString());
		}else {
			return null;
		}
	}
}
