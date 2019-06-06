package com.j2mvc.authorization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.config.InjectAuth;
import com.j2mvc.authorization.entity.Auth;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.service.AuthService;

/**
 * 
 * 权限过滤器
 * 
 * 2014-4-12 创建@杨朔
 */
public class AuthFilter implements Filter {

	/** 配置参数名*/
    private final static String CONFIG_NAME = "config";
	/** 开启状态参数名*/
    private final static String ENABLE_NAME = "enable";
	/** 权限自动更新 */
    private final static String ENABLE_UPDATE_NAME = "enableUpdate";
	/** 权限开启自动更新 */
    private final static String ENABLE_status_UPDATE_NAME = "enablestatusUpdate";
	/** 权限日志开启 */
    private final static String AUTH_LOG_NAME = "authLog";

	public void init(FilterConfig filterConfig) throws ServletException {
    	ServletContext context = filterConfig.getServletContext();
		String customConfig =  filterConfig.getInitParameter(CONFIG_NAME);
		String config = customConfig!=null && !customConfig.equals("")?customConfig : AuthConstants.authConfig;
		new AuthConfig(context, config);	
		new InjectAuth().execute();
		
		String enableConfig = filterConfig.getInitParameter(ENABLE_NAME);
		if(enableConfig!=null && enableConfig.trim().equalsIgnoreCase("false")){
			AuthConfig.enable = false;
		}

		String enableUpdateConfig = filterConfig.getInitParameter(ENABLE_UPDATE_NAME);
		if(enableUpdateConfig!=null && enableUpdateConfig.trim().equalsIgnoreCase("false")){
			AuthConfig.enableUpdate = false;
		}

		String enablestatusConfig = filterConfig.getInitParameter(ENABLE_status_UPDATE_NAME);
		if(enablestatusConfig!=null && enablestatusConfig.trim().equalsIgnoreCase("false")){
			AuthConfig.enablestatusUpdate = false;
		}
		
		String authLogConfig = filterConfig.getInitParameter(AUTH_LOG_NAME);
		if(authLogConfig!=null && authLogConfig.trim().equalsIgnoreCase("true")){
			AuthConfig.authLog = true;
		}
		
		AuthService authService = new AuthService();
		List<Auth> auths = new ArrayList<Auth>();
		auths.addAll(AuthConfig.sysAuths);
		authService.callSave(auths);
    }


	public void destroy() {
		AuthConfig.sysAuths.clear();
		AuthConfig.enable = true;
	}


	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}
	
}
