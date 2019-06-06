package com.j2mvc.framework.interceptor;

import javax.servlet.ServletContext;

/**
 * 
 * 拦截器实现
 * 
 * 2014-2-24 创建@杨朔
 */
public class DispatcherInterceptor{
	protected ServletContext context;
	protected Class<?> refClass;
	protected String urlPattern;
	
	public ServletContext getContext() {
		return context;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}

	public Class<?> getRefClass() {
		return refClass;
	}

	public void setRefClass(Class<?> refClass) {
		this.refClass = refClass;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
}
