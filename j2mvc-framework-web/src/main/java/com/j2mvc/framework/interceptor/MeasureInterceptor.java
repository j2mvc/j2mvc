package com.j2mvc.framework.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 拦截器实现
 * 
 * 2014-4-12 创建@杨朔
 */
public abstract class MeasureInterceptor extends DispatcherInterceptor{
	/**
	 * 拦截，允许DispatcherFilter继续处理请求 
	 * @param request
	 * @param response
	 * 
	 */
	public abstract boolean execute(HttpServletRequest request,HttpServletResponse response);

	/**
	 * 拦截通过
	 * @param request
	 * @param response
	 */
	public abstract void success(HttpServletRequest request,HttpServletResponse response);

	/**
	 * 拦截不通过
	 * @param request
	 * @param response
	 */
	public abstract void error(HttpServletRequest request,HttpServletResponse response);
}
