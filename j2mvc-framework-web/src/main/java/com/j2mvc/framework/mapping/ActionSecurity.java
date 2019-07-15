package com.j2mvc.framework.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Action 安全声明
 * 
 * @author 杨朔
 * @version 2.1@date2019-7-14
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  ActionSecurity {

	/** 是否开启权限控制 */
	public boolean auth() default true;

	/** 是否开启JWT */
	public boolean jwt() default true;
	
}
