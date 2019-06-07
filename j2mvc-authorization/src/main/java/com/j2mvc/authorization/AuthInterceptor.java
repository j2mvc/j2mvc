package com.j2mvc.authorization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.ActionMatch;
import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.Auth;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.service.AuthService;
import com.j2mvc.framework.interceptor.MeasureInterceptor;
import com.j2mvc.util.MD5;
import com.j2mvc.util.StringUtils;

/**
 * 
 * 权限拦截器
 * 
 * 2014-4-12 创建@杨朔
 */
public abstract class AuthInterceptor extends MeasureInterceptor{	
	
	Logger log = Logger.getLogger(getClass().getCanonicalName());
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected AuthService authService = new AuthService();
	protected Auth auth;
	
	protected String uri;
	// 权限控制
	protected boolean isAuth = true;
	
	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		if(!AuthConfig.enable)		
			return true;

		uri = getUri(request);
		log.info("已开启权限控制，执行权限拦载过程...当前URI:"+uri);
		if(uri.indexOf(AuthConfig.pathAdmin)!=-1){
			// 系统管理员不在权限控制范围内，但管理员必须登陆，且拦载系统管理员路径
			return authAdmin();
		}else{ 
			
			// 当前URI是否为已开启了路径权限下URI
			// 截取父路径权限判断
			Auth auth = getAuthPath();
			if(auth!=null){
				return auth(auth);
			}
			// 路径权限判断
			if(isPath()){
				String path = uri.endsWith("/")?uri:uri+"/";
				isAuth = auth(path, AuthConstants.AUTH_TYPE_PATH, AuthConstants.AUTH_STATUS_N) ;
				if(!isAuth) 
					isAuth = auth(path, AuthConstants.AUTH_TYPE_PATH, AuthConstants.AUTH_STATUS_Y);
				if(!isAuth){
					path = path.substring(0,uri.length());
					isAuth = auth(path, AuthConstants.AUTH_TYPE_PATH, AuthConstants.AUTH_STATUS_N);
					if(!isAuth)  
						isAuth = auth(path, AuthConstants.AUTH_TYPE_PATH, AuthConstants.AUTH_STATUS_Y);
				}
			}
			// URI权限判断
			if(isAuth && !isPath()){
				isAuth = auth(uri, AuthConstants.AUTH_TYPE_URI, AuthConstants.AUTH_STATUS_N) ;
				if(!isAuth)  
					isAuth = auth(uri, AuthConstants.AUTH_TYPE_URI, AuthConstants.AUTH_STATUS_Y);
			}
			// 带参URI权限判断
			if(isAuth && !isPath()){
				String queryString = request.getQueryString();
				ActionBean bean = new ActionMatch(uri,queryString).getBean();
				Map<String,String> map = bean!=null? bean.getQuerys():null;
				if(map!=null && map.size()>0){
					// 当前为带参URI
					if(isQuery(request,map)){
						String value = buildQuery(map);// 访问query字符串
						isAuth = auth(uri+"?"+value, AuthConstants.AUTH_TYPE_URL, AuthConstants.AUTH_STATUS_N);
						if(!isAuth)  
							isAuth = auth(uri+"?"+value, AuthConstants.AUTH_TYPE_URL, AuthConstants.AUTH_STATUS_Y);
				
					}
				}
			}
			return isAuth;
		}
	}
	/**
	 * 当前URI是否包含在已开启路径权限的路径下
	 * 返回路径权限
	 * 
	 */
	public Auth getAuthPath(){
		uri = getUri(request);
		String[] array = uri.split("/");
		Object o = Session.auths.get(MD5.md5(uri));
		// 从缓存获取已判断过的权限
		// 如果不为空，则直接返回权限对象
		if(o!=null){
			if(o instanceof Auth){
				return (Auth) o;
			}else{
				return null;
			}
		}
		Auth auth = null;
		String arr = "";
		if(array!=null)
		for(int i=0;i<array.length;i++){
			if(!StringUtils.isEmpty(array[i])){
				arr += "/"+array[i];
				auth = getAuthPath(arr);
				if(auth != null){
					return auth;
				}
			}
		}
		// 存入缓存
		Session.auths.put(MD5.md5(uri),auth!=null?auth:uri);
		return auth;
	}
	/**
	 * 查找当前路径是否开启权限，是则返回当前权限
	 * @param path
	 * 
	 */
	public Auth getAuthPath(String path){
		uri = getUri(request);
		Auth auth = authService.getAuthPath(path);
		return auth;
	}
	/**
	 * 判断当前URI是路径还是链接
	 */
	public boolean isPath() {
		uri = getUri(request);
		return Session.paths.contains(uri) || Session.paths.contains(uri+"/") ;
	}
	
	/**
	 * 系统管理员权限
	 * 
	 */
	protected boolean authAdmin(){
		Object o = request.getSession().getAttribute(AuthConfig.sessionAdminParamName);
		if(o!=null){
			request.getSession().setAttribute(AuthConfig.sessionAdminParamName, o);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 是否已登陆，返回userId
	 * 字类获取当前会话用户
	 * HttpSession session = request.getSession();
	 * User user =  (User)session.getAttribute(AuthConfig.sessionUserParamName);
	 * 
	 */
	protected abstract String getUserId();
	/**
	 * 权限查询
	 * @param auth 权限
	 * 
	 */
	protected boolean auth(Auth auth){
		String userId = getUserId();
		log.info("开启中的路径权限:"+auth.getValue()+"，需要判断用户是否拥有此权限，用户"+(!StringUtils.isEmpty(getUserId())?"已":"未")+"登录。");
		// 开启中的权限，需要判断用户是否拥有此权限
		if(!StringUtils.isEmpty(getUserId())){
			return authService.exists(auth.getId(), userId);
		}
		return false;
	}
	/**
	 * 权限查询
	 * @param value 权限值
	 * @param authType 权限类型
	 * @param status 权限开启状态
	 * 
	 */
	protected boolean auth(String value,int authType,String status){
		if(status.equals(AuthConstants.AUTH_STATUS_N)){
			// 关闭中的权限，权限不控制
			auth = authService.get(value,authType,status);
			return auth!=null;
		}else{
			String userId = getUserId();
			log.info("开启中的权限:"+value+"，需要判断用户是否拥有此权限，用户"+(!StringUtils.isEmpty(getUserId())?"已":"未")+"登录。");
			// 开启中的权限，需要判断用户是否拥有此权限
			if(!StringUtils.isEmpty(getUserId())){
				return authService.exists(value, authType,status,userId);
			}
		}
		return false;
	}

	/**
	 * 获取不含参数的URI
	 * @param request
	 * 
	 */
	protected String getUri(HttpServletRequest request){
		String uri = request.getServletPath();
		return uri;
	}

	/**
	 * 获取Path
	 * @param request
	 * 
	 */
	protected String getPath(String uri){
		if(isPath()){
			return uri.substring(0, uri.lastIndexOf("/"));
		}
		return "";
	}
	/**
	 * 解析参数，并装入Map
	 * @param request
	 * 
	 */
	protected boolean isQuery(HttpServletRequest request,Map<String,String> map){
		int sameNum = map.size();
		String queryString = request.getQueryString();
		if(queryString!=null){
			String[] querys = queryString.split("&");
			for(int i=0;i<querys.length;i++){
				String [] param = querys[i].split("=");
				if(param.length>1){
					String value = map.get(param[0]);
					if(value!=null && value.equals(param[1])){
						// 当前参数键包含，且值相同
						sameNum --;
					}
				}
			}
		}
		return sameNum < 1;
	}

    /**
     * 生成query字符串
     * @param qString
     * 
     */
	private String  buildQuery(Map<String,String> map){
		String query = "";
		if(map!=null && map.size() > 0){
			Set<Entry<String,String>> set = map.entrySet();
			Iterator<Entry<String,String>> iter = set.iterator();
			 
			while(iter.hasNext()){
				Entry<String,String> entry = iter.next();
				query += (!query.equals("")?"&":"")+entry.getKey()+"="+entry.getValue();
			}
		}
		return query;
	}

	/**
	 * 可以由子类重写,也可以不用重写,默认为跳转正确的有权限的页面或方法等
	 * 默认不做任何操作
	 */
	@Override
	public void success(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		if(AuthConfig.authLog){
			log.info("权限通过");
		}
	}

	
	/**
	 * 可由子类覆盖此方法
	 */
	@Override
	public void error(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		if(uri == null)
			uri = getUri(request);
		if(uri.indexOf(AuthConfig.pathAdmin)!=-1){
			if(AuthConfig.authLog){
				log.info("系统管理员未登陆,权限不通过,跳转系统管理员登陆界面...");
			}
			// 跳转超级管理员登陆界面
			adminLogin();
		}else if(StringUtils.isEmpty(getUserId())){
			if(AuthConfig.authLog){
				log.info("用户未登陆,权限不通过,跳转用户登陆页面...");
			}
			// 跳转用户登陆页面
			userLogin();
		}else{
			if(AuthConfig.authLog){
				log.info("用户已登陆,自定义无权限处理...");
			}
			// 自定义无权限处理,可以输出JSON格式,也可以执行跳转操作
			noaccess();
		}
	}
	public void adminLogin(){
		String reponseType = request.getParameter("reponseType");
		if(!StringUtils.isEmpty(reponseType) && reponseType.equalsIgnoreCase("JSON")){
			try {
				response.getWriter().println("{\"code\":\"601\",\"message\":\"未登录系统！\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				response.sendRedirect(request.getContextPath()+"/"+AuthConfig.loginAdminUri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void userLogin(){
		String reponseType = request.getParameter("reponseType");
		if(!StringUtils.isEmpty(reponseType) && reponseType.equalsIgnoreCase("JSON")){
			try {
				response.getWriter().println("{\"code\":\"601\",\"message\":\"未登录系统！\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				response.sendRedirect(request.getContextPath()+"/"+AuthConfig.loginUserUri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * 自定义无权限处理,可以输出JSON格式,也可以执行跳转操作
	 */
	public void noaccess(){
		// 在此实现除登陆权限以外的其他操作
		log.error(request.getRequestURL()+" >> 无权限");
		String reponseType = request.getParameter("reponseType");
		if(!StringUtils.isEmpty(reponseType) && reponseType.equalsIgnoreCase("JSON")){
			// JSON数据显示
			try {
				response.getWriter().println("{\"code\":\"602\",\"message\":\"当前账号无操作权限\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				response.sendRedirect(request.getContextPath()+"/noaccess");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}