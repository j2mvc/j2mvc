package com.j2mvc.framework.dispatcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.ActionMatch;
import com.j2mvc.framework.interceptor.Interceptor;


/**
 * URL过滤器
 * 
 * 2014-2-22 创建@杨朔
 */
public class DispatcherFilter implements Filter {
	
	static final Logger log = Logger.getLogger(DispatcherFilter.class);

	/** 过滤后缀参数名*/
	public final static String SUBFIXES = "subfixes";
	
	public static ArrayList<String> subfixes = new ArrayList<String>();
	
	public void init(FilterConfig fConfig) throws ServletException {
		String subfix = fConfig.getInitParameter(SUBFIXES);
		String[] args = subfix!=null?subfix.split(","):null;
		if(args!=null)
		for(String s:args)
			subfixes.add(s);
	}
	/**
	 * 执行过滤逻辑
	 */
	public void doFilter(ServletRequest servletRequest,ServletResponse servletResponse, FilterChain chain){
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		String uri = request.getServletPath();
		// 不需要拦截，通常后缀为.css,.js,图片后缀等。				
		if(!filter(uri)){
			try {
				chain.doFilter(request, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}		

		try {
			request.setCharacterEncoding(Session.encoding);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/html;charset=" + Session.encoding);
		response.setCharacterEncoding(Session.encoding);
		try {
			doAction(request, response,chain);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行Actions
	 * @param request
	 * @param response
	 * @param chain
	 */
	public void doAction(HttpServletRequest request,HttpServletResponse response, FilterChain chain) throws IOException, ServletException  {
		// 获取Action元数据
		String uri = request.getServletPath();
		String queryString = request.getQueryString();
		ActionMatch match = new ActionMatch(uri,queryString);
		ActionBean bean = match.getBean();
		/** 执行拦截器 */
		Interceptor dispatcherInterceptor = new Interceptor(request, response,bean);
		boolean success = dispatcherInterceptor.isSuccess();
		if(success){
			if(bean!=null){
				/** 执行Action */
				new DispatcherForward(request, response, bean);
			}else{
				if(Session.uriLog)
					log.info("正在访问>>" + uri +(queryString!=null&&!queryString.equals("")?"?"+queryString:"")+ ",未找到映射."
							+ "如果配置uri为正则表达式,请检是否正确,"
							+ "如果配置正确,请检查uri是否正确,例如是否漏了工程路径.");
				chain.doFilter(request, response);
			}
		}
	}
	
	/**
	 * 判断当前URI是否需要拦截
	 * @param uri
	 * 
	 */
	public boolean filter(String uri){
		if(uri.lastIndexOf(".")!= -1){
			String subfix = uri.substring(uri.lastIndexOf("."),uri.length());
			return subfixes.contains(subfix);
		}else {
			return true;
		}
	}
	/**
	 * 销毁
	 */
	@Override
	public void destroy() {
		log.info("销毁Filter...");
	}
}
