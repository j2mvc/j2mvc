package com.j2mvc.framework.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.ActionMatch;
import com.j2mvc.framework.interceptor.Interceptor;

/**
 * 调配Servlet
 * 
 * 2014-2-23 创建@杨朔
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 4351854781111498245L;

	/**
	 * doGet
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	/**
	 * doPost
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	/**
	 * 执行Actions
	 * @param request
	 * @param response
	 */
	public void doAction(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
		uri = !contextPath.equals("") && uri.startsWith(contextPath)?
						uri.substring(contextPath.length(),uri.length()):uri;
		/** 执行拦截器 */
		Interceptor dispatcherInterceptor = new Interceptor(request, response);
		boolean success = dispatcherInterceptor.isSuccess();
		if(success){
			if(uri.endsWith("/")){
				uri = uri.substring(0,uri.lastIndexOf("/"));
			}else{
				uri = uri.substring(0,uri.lastIndexOf("."));
			}			
			ActionBean bean = new ActionMatch(uri).getBean();
			if(bean!=null){
				/** 执行Action */
				new DispatcherForward(request, response, bean);
			}
		}
	}
}
