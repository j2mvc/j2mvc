package com.j2mvc.framework.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 字段注解类
 * 
 * 2014-2-24 创建@杨朔
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD})
public @interface Column {

	public String name();
	
	public int length() default 0;

	public boolean notnull() default false;
}
