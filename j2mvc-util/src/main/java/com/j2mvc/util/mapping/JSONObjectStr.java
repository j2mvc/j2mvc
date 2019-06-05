package com.j2mvc.util.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 字段对象注解类
 * 
 * 2014-2-24 创建@杨朔
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONObjectStr {
	// 表名
	public String value();
}