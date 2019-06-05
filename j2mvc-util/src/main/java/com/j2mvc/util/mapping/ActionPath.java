package com.j2mvc.util.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Actions类声明
 * 
 * @author 杨朔
 * @version 1.0@date2014-5-26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  ActionPath {

	/** 访问路径 */
	public String path() default "";

	/** 映射目录 */
	public String dir() default "";
	
	/** 说明 */
	public String description() default "";

	/** 是否开启权限控制 */
	public boolean auth() default false;
}
