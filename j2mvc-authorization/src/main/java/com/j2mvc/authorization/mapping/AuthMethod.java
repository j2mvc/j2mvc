package com.j2mvc.authorization.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.j2mvc.authorization.global.AuthConstants;

/**
 * 
 * 系统权限类
 * 
 * 2014-2-26 创建@杨朔
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  AuthMethod {

	/** 系统权限名 */
	public String name();
	/** 默认为允许编辑，不允许编辑的权限为系统初始化时创建。 */
	public boolean enable() default true;
	/**
	 * 权限开启状态,默认为1,需要权限控制
	 * 1:权限控制开启
	 * 0:权限控制关闭
	 */
	public String status() default AuthConstants.AUTH_STATUS_Y;
}
