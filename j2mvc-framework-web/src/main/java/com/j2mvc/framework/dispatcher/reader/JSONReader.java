package com.j2mvc.framework.dispatcher.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.j2mvc.framework.Constants;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.RequestUri;
import com.j2mvc.framework.util.InvokeUtils;
import com.j2mvc.util.StringUtils;
import com.j2mvc.util.json.JSONParse;

/**
 * 读JSON数据并解析
 * 
 * @author 杨朔 2019-6-7
 */
public class JSONReader extends BaseReader {
	static final Logger log = Logger.getLogger(JSONReader.class);

	public JSONReader(HttpServletRequest request, Method method, Object object) {
		super(request, method, object);
	}

	/**
	 * 读取请求参数的数据 需要区分请求方法和数据提交类型 读取JSON数据，并解析出对象返回
	 * 
	 * @param method
	 * @param object
	 * @return Object
	 */
	public Object result() {
		clazz = object.getClass();
		types = method.getParameterTypes();
		int length = types.length;
		if (length > 0) {
			// 请求参数名数组
			names = getParameterNames(clazz, method.getName(), length);
			values = new Object[types.length];
			if (names != null) {
				// 将json字符串转换为json对象
				requestBody();
				try {
					jsonData = JSONObject.parseObject(requestBody);
				} catch (JSONException e) {
					log.error("JSON格式解析错误JSONException：" + e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					log.error("JSON格式解析错误，请求内容：" + requestBody);
					e.printStackTrace();
				}
				// 解析JSON数据
				for (int i = 0; i < types.length; i++) {
					Class<?> type = types[i];
					values[i] = getParameterValue(type, names[i]);
				}
				InvokeUtils.invoke(clazz, "setJsonData", object, new Object[] { jsonData }, JSONObject.class);
				InvokeUtils.invoke(clazz, "setRequestBody", object, new Object[] { requestBody }, String.class);
				log.info(method.getName() + values);
				try {
					return method.invoke(object, values);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		// 没有参数，执行当前Action方法
		return InvokeUtils.invoke(clazz, method.getName(), object, null);
	}

	/**
	 * 读取数据流字符串
	 */
	public void requestBody() {
		try {
			request.setCharacterEncoding(Session.encoding);
		} catch (UnsupportedEncodingException e1) {
			log.error("字符编码错误！");
		}
		// 读取请求内容
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), Session.encoding));
		} catch (UnsupportedEncodingException e1) {
			log.error("读取字符编码错误！");
		} catch (IOException e1) {
			log.error("读取数据失败！");
		}
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			requestBody = sb.toString();
		} catch (IOException e1) {
			log.error("读取数据失败！");
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取参数值
	 * 
	 * @param type
	 * @param name
	 * 
	 */
	@Override
	protected Object getParameterValue(Class<?> type, String name) {
		if (RequestUri.class.isAssignableFrom(type)) {
			String path = request.getContextPath();
			String requestUri = request.getRequestURI();
			requestUri = StringUtils.deleRepeat(requestUri, "/");
			requestUri = requestUri.substring(path.length(), requestUri.length());
			requestUri = requestUri.startsWith("/") ? requestUri.substring(1, requestUri.length()) : requestUri;
			requestUri = requestUri.endsWith("/") ? requestUri.substring(0, requestUri.length() - 1) : requestUri;
			String[] values = requestUri.split("/");
			return new RequestUri(values);
		} else if (String.class.isAssignableFrom(type)) {
			return jsonData.getString(name);
		} else if (Integer.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)
				|| short.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
			return jsonData.getInteger(name);
		} else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
			return jsonData.getLong(name);
		} else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
			return jsonData.getFloat(name);
		} else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
			return jsonData.getDouble(name);
		} else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
			return jsonData.getBoolean(name);
		} else if (Time.class.isAssignableFrom(type))
			try {
				String str = jsonData.getString(name);
				return Constants.DEFAULT_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
				return null;
			}
		else if (Date.class.isAssignableFrom(type)) {
			try {
				String str = jsonData.getString(name);
				return Constants.DEFAULT_DATE_TIME_FORMAT.parse(str);
			} catch (ParseException e) {
				log.error(e.getMessage());
				return null;
			}
		} else {
			String value = jsonData.getString(name);
			if (value == null) {
				return null;
			}
			if (type.isArray()) {
				Class<?> clazz;
				try {
					clazz = Class.forName(type.getCanonicalName().replace("[]", ""));
					return JSONParse.parseArray(value, clazz);
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
				return JSONParse.parseObject(value, type);
			}
		}

	}

	@Override
	protected Object getValue(Class<?> type, String name) {
		return null;
	}
}
