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
public class J2mvcListener implements ServletContextListener {
	static final Logger log = Logger.getLogger(J2mvcListener.class);

	/** action配置参数名 */
	public final static String DISPATCH_NAME_CONFIG = "works";
	/** SQL日志参数名 */
	public final static String SQL_LOG_CONFIG = "sqlLog";
	/** 开启日志 */
	private final static String ENABLE_LOG = "uriLog";
	/** 语言编码 */
	private final static String ENCODING = "encoding";

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		/** 加载配置 */
		try {
			String sqlLogConfig = servletContext.getInitParameter(SQL_LOG_CONFIG);
			Session.sqlLog = Boolean.valueOf(sqlLogConfig);
		} catch (Exception e) {
			log.error(e.getMessage());
		} catch (NoSuchFieldError e) {
			log.error(e.getMessage());
		}
		try {
			String logConfig = servletContext.getInitParameter(ENABLE_LOG);
			Session.uriLog = Boolean.valueOf(logConfig);
		} catch (NoSuchFieldError e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		try {
			String encoding = servletContext.getInitParameter(ENCODING);
			Session.encoding = encoding != null ? encoding : Session.encoding;
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// URI注入
		String customConfig = servletContext.getInitParameter(DISPATCH_NAME_CONFIG);
		String config = customConfig != null && !customConfig.equals("") ? customConfig : Constants.DISPATCH_CONFIG;
		new Config(servletContext, config);
		new InjectUri().execute();

		// 全局变量设置
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
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
	}

}
