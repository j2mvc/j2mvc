package com.j2mvc.util.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 主键注解类
 * 
 * 2014-2-24 创建@杨大江
 */
@Retention(RetentionPolicy.RUNTIME) 
public @interface PrimaryKey {
	// 主键名称
	public String name() default "id";
	// 自动增长
	public boolean autoIncrement() default true;
}
