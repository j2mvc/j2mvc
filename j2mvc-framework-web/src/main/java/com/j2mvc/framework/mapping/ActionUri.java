package com.j2mvc.framework.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Action uri声明
 * 
 * @author 杨朔
 * @version 1.0@date2014-5-26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  ActionUri {

	/** 访问URI*/
	public String uri() default "";

	/** 标题 */
	public String title() default "";

	/** 关键字 */
	public String keywords() default "";

	/** 描述 */
	public String description() default "";

	/** 标签 */
	public String tag() default "";

	/** 是否开启权限控制 */
	public boolean auth() default false;

	/** 无权限代码 */
	public String authNone() default "";
	
	/** URL请求参数串 */
	public String query() default "";

	/** 请求方法 */
	public String requestMethod() default "";
	
	/** 请求数据类型 */
	public String contentType() default "";
}
