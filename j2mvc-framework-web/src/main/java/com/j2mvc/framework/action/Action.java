package com.j2mvc.framework.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.j2mvc.util.json.JSONFactory;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.j2mvc.framework.ContentType;
import com.j2mvc.framework.Session;
import org.w3c.dom.Document;
 
/** 
 * Action
 * 
 * @author 杨朔
 * @version 1.0 2014-2-23
 * @version 1.1.6 2014-8-17
 */
public abstract class Action {
	
	
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected ActionBean bean;
	protected PrintWriter out;
	protected String path;
	protected HttpSession session;
	protected JSONObject jsonData;
	protected Document xmlData;
	protected String requestBody;
	
	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
		try {
			this.out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
		this.path = request.getContextPath();
		this.session = request.getSession();
		put("path", path);
		put("PATH", path);
		put("ROOT_PATH", path);
	}

	public JSONObject getJsonData() {
		return jsonData;
	}

	public void setJsonData(JSONObject jsonData) {
		this.jsonData = jsonData;
	}

	public Document getXmlData() {
		return xmlData;
	}

	public void setXmlData(Document xmlData) {
		this.xmlData = xmlData;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public abstract String onStart();

	public ActionBean getBean() {
		return bean;
	}

	public void setBean(ActionBean bean) {
		this.bean = bean;
		put("title", bean.getTitle());
		put("TITLE", bean.getTitle());
		put("keywords", bean.getKeywords());
		put("KEYWORDS", bean.getKeywords());
		put("DESCRIPTION", bean.getDescription());
		put("description", bean.getDescription());
	}

	public void setAttribute(String name, Object value) {
		request.setAttribute(name, value);
	}

	/**
	 * 获取数组参数
	 * 
	 * @param name
	 * 
	 */
	protected Object[] getParams(String name) {
		return request.getParameterValues(name);
	}

	/**
	 * 获取参数值
	 * 
	 * @param name
	 * 
	 */
	protected String getParam(String name) {
		String contentType = bean.getContentType();
		String value = null;
		if(contentType!=null && ContentType.JSON.equalsIgnoreCase(contentType)) {
			try {
				value = jsonData.getString(name);
			} catch (JSONException e) {
				Logger.getLogger(Action.class).warn("读取JSON值错误:"+e.getMessage());
			}
		}else {
			value = request.getParameter(name) != null ? request.getParameter(name).trim() : "";
		}
		if(value != null) {
			// 编码转换
			value = getCharset(value);
		}
		return value;
	}

	protected JSONObject getJsonBody(){
		return jsonData;
	}
	/**
	 * 获取UTF8格式
	 * 
	 * @param value
	 * 
	 */
	protected String getCharset(String value) {
		if (value == null)
			return "";
		try {
			if (java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value)) {
				value = new String(value.getBytes("ISO-8859-1"), Session.encoding);
			} else if (java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(value)) {
				value = new String(value.getBytes("UTF-8"), Session.encoding);
			} else if (java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value)) {
				value = new String(value.getBytes("GBK"), Session.encoding);
			} else if (java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(value)) {
				value = new String(value.getBytes("GB2312"), Session.encoding);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 输出JSON字符串
	 * 
	 * @param object
	 */
	public void printJson(Object object) {
		Object jsonObject =  new JSONFactory().toJsonObject(object, true);
		try {
			// 为保证输出正确的JSON格式，需要先清除之前输出的所有内容
			HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
			wrapper.resetBuffer();
			wrapper.getResponse().getWriter().print(jsonObject != null ? jsonObject.toString() : "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出JSON字符串
	 * 
	 * @param s
	 */
	public void print(String s) {
		try {
			response.getWriter().print(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置页面参数
	 * 
	 * @param name
	 * @param value
	 */
	public void put(String name, Object value) {
		request.setAttribute(name, value);
	}

}