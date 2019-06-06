package com.j2mvc.framework;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.j2mvc.framework.config.Config;
import com.j2mvc.framework.config.InjectUri;
import com.j2mvc.framework.dao.DataSourceJndi;

/**
 * fixwork监听器
 * 
 * 2014-4-27 创建@杨朔
 */
public class FixworkListener implements ServletContextListener {
	static final Logger log = Logger.getLogger(FixworkListener.class);

	/** action配置参数名*/
	public final static String DISPATCH_NAME_CONFIG= "works";
	/** SQL日志参数名*/
	public final static String SQL_LOG_CONFIG= "sqlLog";
	/** 开启日志*/
    private final static String ENABLE_LOG = "uriLog";
	/** 语言编码*/
    private final static String DEFAULT_ENCODING= "defaultEncoding";
	
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	ServletContext servletContext = servletContextEvent.getServletContext();
		/** 加载配置 */
		try {
			String sqlLogConfig = servletContext.getInitParameter(SQL_LOG_CONFIG);
			Session.sqlLog = Boolean.valueOf(sqlLogConfig);
		} catch (Exception e) {
			log.error(e.getMessage()); 
		}catch (NoSuchFieldError e) {
			log.error(e.getMessage());
		}
		try {
			String logConfig = servletContext.getInitParameter(ENABLE_LOG);
			Session.uriLog = Boolean.valueOf(logConfig);
		} catch (NoSuchFieldError e) {
			log.error(e.getMessage());
		}catch (Exception e) {
			log.error(e.getMessage());
		}

		try {
			String defaultEncoding = servletContext.getInitParameter(DEFAULT_ENCODING);
			Session.defaultEncoding = defaultEncoding != null ? defaultEncoding:"UTF-8";
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		// URI注入
		String customConfig = servletContext.getInitParameter(DISPATCH_NAME_CONFIG);
		String config = customConfig!=null && !customConfig.equals("")?customConfig : Constants.DISPATCH_CONFIG;
		new Config(servletContext,config);
    	new InjectUri().execute();
    	
    	// 全局变量设置
    	
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    		DataSourceJndi.destroy();
    		if(Session.beans!=null)
    			Session.beans.clear();
    		if(Session.interceptors!=null)
    			Session.interceptors.clear();
    		if(Session.paths!=null)
    			Session.paths.clear();
    		if(Session.uris!=null)
    			Session.uris.clear();
    		Session.sqlLog = false;
    }
	
}
