package com.j2mvc.framework.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * SQL语句注解类,仅返回基础数据类型
 * 
 * 2014-2-24 创建@杨朔
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD})
public @interface Sql {

	/* SQL语句 */
	public String value();

}
