package com.j2mvc.framework.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.j2mvc.framework.RequestMethod;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.ActionMatch;
import com.j2mvc.framework.dao.DataSourceJndi;
import com.j2mvc.framework.interceptor.Interceptor;

/**
 * 调配Servlet
 * 
 * 2014-2-23 创建@杨朔
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 4351854781111498245L;
	static final Logger  log = Logger.getLogger(DispatcherServlet.class);

	private static final String METHOD_GET = "GET";;
	private static final String METHOD_POST = "POST";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding(Session.encoding);
		resp.setContentType("text/html;charset=" + Session.encoding);
		resp.setCharacterEncoding(Session.encoding);
		
		String method = req.getMethod();

		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		if ("/".equals(contextPath)) {
			contextPath = "";
		}
		uri = !contextPath.equals("") && uri.startsWith(contextPath) ? uri.substring(contextPath.length(), uri.length())
				: uri;
		/** 执行拦截器 */
		Interceptor dispatcherInterceptor = new Interceptor(req, resp);
		boolean success = dispatcherInterceptor.isSuccess();
		ActionBean bean = null;
		if (success) {
			if (uri.endsWith("/")) {
				uri = uri.substring(0, uri.lastIndexOf("/"));
			} else {
				uri = uri.substring(0, uri.lastIndexOf("."));
			}
			bean = new ActionMatch(uri).getBean();
		}
		if (bean != null) {
			String requestMethod = bean.getRequestMethod();
			requestMethod = requestMethod!=null?requestMethod:"";
			if (method.equals(METHOD_GET) && 
					(requestMethod.equalsIgnoreCase(RequestMethod.GET)||
							requestMethod.equals(""))) {
				doAction(req, resp,bean);
			} else if (method.equals(METHOD_POST) && 
					(requestMethod.equalsIgnoreCase(RequestMethod.POST)||
							requestMethod.equals(""))) {
				doAction(req, resp,bean);
			}  else {
				log.warn("服务器限制了请求模式，客户端请求方式是GET或POST,与服务器requestMothod不一致.");
				super.service(req, resp);
			}
		}else{
			String queryString = req.getQueryString();
			if(Session.uriLog)
				log.warn("正在访问>>" + uri +(queryString!=null&&!queryString.equals("")?"?"+queryString:"")+ ",未找到映射."
						+ "如果配置uri为正则表达式,请检是否正确,"
						+ "如果配置正确,请检查uri是否正确,例如是否漏了工程路径.");
			super.service(req, resp);
		}
	}
	/**
	 * 执行Actions
	 * 
	 * @param request
	 * @param response
	 * @param bean
	 */
	public void doAction(HttpServletRequest request, HttpServletResponse response,ActionBean bean)
			throws IOException, ServletException {
		/** 执行Action */
		new DispatcherForward(request, response, bean);
	}
	/**
	 * 销毁
	 */
	@Override
	public void destroy() {
		DataSourceJndi.destroy();
		if (Session.beans != null)
			Session.beans.clear();
		if (Session.interceptors != null)
			Session.interceptors.clear();
		if (Session.paths != null)
			Session.paths.clear();
		if (Session.uris != null)
			Session.uris.clear();
		if (Session.auths != null)
			Session.auths.clear();
		if (Session.queryUris != null)
			Session.queryUris.clear();
		if (Session.queryUriBeans != null)
			Session.queryUriBeans.clear();
		if (Session.paths != null)
			Session.paths.clear();
		if (Session.pathMap != null)
			Session.pathMap.clear();
		Session.sqlLog = false;
		super.destroy();
	}
}
