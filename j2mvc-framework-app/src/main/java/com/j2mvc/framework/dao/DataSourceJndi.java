package com.j2mvc.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.config.Config;

/**
 * 绑定数据源
 * 
 * 2014-2-24 创建@杨朔
 */
public class DataSourceJndi {
	static Logger log = Logger.getLogger(DataSourceJndi.class.getName());
	public static Connection connection;
	public static Context ctx;
	/**
	 * 绑定数据源
	 */
	public static void init() {
		Config.init();
		DataSourceBean bean = Session.dataSourceBean;
		String name = Session.dataSourceBean.getName().replaceAll("/", ":");
		try {
			BasicDataSource dataSource = new BasicDataSource();
			// 设置数据库驱动
			dataSource.setDriverClassName(bean.getDriverClassName());
			// 设置JDBC的URL
			dataSource.setUrl(bean.getUrl());
			dataSource.setUsername(bean.getUsername());
			dataSource.setPassword(bean.getPassword());
			if(bean.getMaxActive()>0)
				dataSource.setMaxActive(bean.getMaxActive());
			if(bean.getMaxIdle()>0)
				dataSource.setMaxIdle(bean.getMaxIdle());
			if(bean.getMaxWait()>0)
				dataSource.setMaxWait(bean.getMaxWait());
			// 设置连接池初始大小
			if(bean.getInitialSize()>0)
				dataSource.setInitialSize(bean.getInitialSize());
			
			Hashtable<String, String> params = new Hashtable<String,String>();
			params.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			// JNDI配置  
			ctx = new InitialContext(params); 
			
			// 数据源绑定到JNDI
			ctx.bind(name, dataSource);
			
			log.info("绑定JNDI，数据源名称："+bean.getName());	
		} catch (Exception e) {
			String message = e.getMessage();
			if(message.indexOf("already")==-1)
				log.error(message);
		}
	}
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection() {
		if(ctx == null)
			init();
			
		try {
			// 实例上下文目录
			Hashtable<String, String> params = new Hashtable<String,String>();
			params.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			// JNDI配置  
			Context context = new InitialContext(params);
			// 在命名空间和目录空间中查找 数据源名称 返回数据库连接池对象 JNDI
			String name = Session.dataSourceBean.getName().replaceAll("/", ":");
			if(null == name || "".equals(name))
				log.error("未找到数据源配置");
			else{
				DataSource dataSource =(DataSource)context.lookup(name);
				connection = dataSource.getConnection();
			}
				
		} catch (SQLException e) { 
			log.error(e.getMessage()); 
		} catch (NamingException e) {
			log.error(e.getMessage()); 
		}
		return connection;
	}
}
