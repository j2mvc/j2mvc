package com.j2mvc.framework.interceptor;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j2mvc.framework.Session;
import com.j2mvc.util.InvokeUtils;

/**
 * 
 * 拦截器调配
 * 
 * 2014-2-24 创建@杨朔
 */
public class Interceptor {
	static final Logger log = Logger.getLogger(Interceptor.class);
	HttpServletRequest request;
	HttpServletResponse response;
	private boolean success = true;
	/**
	 * 构造器
	 * @param request
	 * @param response
	 */
	public Interceptor(
			HttpServletRequest request,
			HttpServletResponse response){
		this.request = request;
		this.response = response;
		init();
	}
	/**
	 * 初始化Action
	 */
	private void init(){
		if(Session.interceptors!=null){
			for(int i=0;i<Session.interceptors.size();i++){
				execute(Session.interceptors.get(i));		
			}
		}
	}
	/**
	 * 执行方法
	 */
	private void execute(DispatcherInterceptor interceptor){
		String requestUri = request.getRequestURI();
		String urlPattern = interceptor.getUrlPattern();
		if(!urlPattern.equals("") && requestUri.indexOf(urlPattern) ==-1){
			return;
		}		
		Class<?> clazz = interceptor.getRefClass();
		if(clazz!=null){
			try {
				Object obj = clazz.newInstance();
				// 执行拦截过程
				Object object = InvokeUtils.invoke(clazz, "execute", obj,  new Object[]{request,response}, HttpServletRequest.class,HttpServletResponse.class);
				success = success?object !=null && object instanceof Boolean?(Boolean) object:success:false;
				if(success)
					InvokeUtils.invoke(clazz,"success", obj,new Object[]{request,response},HttpServletRequest.class,HttpServletResponse.class);
				else
					InvokeUtils.invoke(clazz,"error", obj,new Object[]{request,response},HttpServletRequest.class,HttpServletResponse.class);
			} catch (InstantiationException e) {
				log.error(e.getMessage());
			} catch (IllegalAccessException e) {
				log.error(e.getMessage());
			}catch (SecurityException e) {
				log.error(e.getMessage());
			} catch (IllegalArgumentException e) {
				log.error(e.getMessage());
			}
		}
	}
	public boolean isSuccess() {
		return success;
	}	
}