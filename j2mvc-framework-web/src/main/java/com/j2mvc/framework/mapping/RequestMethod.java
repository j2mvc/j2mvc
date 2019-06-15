package com.j2mvc.framework.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 请求方法
 * 
 * @author 杨朔
 * @version 1.0@date2014-5-26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  RequestMethod {
	String GET = "GET";
	String POST = "POST";
	// 方法
	public String value() default GET;
}
