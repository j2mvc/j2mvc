package com.j2mvc.framework.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 请求的文档类型
 * 
 * @author 杨朔
 * @version 1.0@date2014-5-26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  ContentType {
	String XWwwFormUrlencoded = "application/x-www-form-urlencoded";
	String FormData = "multipart/form-data";
	String JSON = "application/json";
	String XML = "application/xml";
	String XML_TEXT = "text/xml";
	String TEXT = "text/plain";
	String FILE= "multipart/file";
	// 方法
	public String value();
}
