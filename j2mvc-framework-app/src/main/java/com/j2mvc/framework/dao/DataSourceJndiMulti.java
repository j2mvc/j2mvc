package com.j2mvc.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.config.Config;

/**
 * 绑定多个数据源
 * 
 * 2014-8-18 创建@杨朔
 * @version 1.1.6
 */
public class DataSourceJndiMulti {
	static Logger log = Logger.getLogger(DataSourceJndiMulti.class.getName());

	static Map<String, Connection> connectionMap = new HashMap<String, Connection>();
	static Map<String, DataSourceBean> beanMap = Session.dataSourceBeanMap;
	static Map<String, BasicDataSource>  dataSourceMap = new HashMap<String, BasicDataSource>();
	static Context ctx;
	/**
	 * 绑定数据源
	 */
	public static void init() {
		Config.init();
		
		Hashtable<String, String> params = new Hashtable<String,String>();
		params.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
		
		Set<Entry<String, DataSourceBean>> set = beanMap.entrySet();
		Iterator<Entry<String, DataSourceBean>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, DataSourceBean> entry = iterator.next();
			DataSourceBean bean = entry.getValue();
			String name = bean.getName().replaceAll("/", ":");
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

				// JNDI配置  
				ctx = new InitialContext(params); 
				
				// 数据源绑定到JNDI
				ctx.bind(name, dataSource);
				
				dataSourceMap.put(name, dataSource);
				
				log.info("绑定JNDI，数据源名称："+bean.getName());
			} catch (Exception e) {		
				String message = e.getMessage();
				if(message.indexOf("already")==-1)
					log.error("app:"+message);
			}
		}
	}
	/**
	 * 销毁数据源
	 */
	public static void destroy(){
		try {			
			Set<Entry<String, DataSourceBean>> set = beanMap.entrySet();
			Iterator<Entry<String, DataSourceBean>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, DataSourceBean> entry = iterator.next();
				DataSourceBean bean = entry.getValue();
				// 数据源解绑定
				String name = bean.getName().replaceAll("/", ":");
				ctx.unbind(name);	
				log.info("app:解除绑定JNDI，数据源名称："+bean.getName());
			}		
		} catch (Exception e) {
			log.error(e.getMessage()); 
		}
	}
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection(String name) {
		if(dataSourceMap.isEmpty())
			init();
		try {
			name = name.replaceAll("/", ":");
			// 实例上下文目录
			Hashtable<String, String> params = new Hashtable<String,String>();
			params.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			
			Context context = new InitialContext(params);
			// 在命名空间和目录空间中查找 数据源名称 返回数据库连接池对象 JNDI
			DataSource dataSource =(DataSource)context.lookup(name);
			connectionMap.put(name, dataSource.getConnection());
		} catch (SQLException e) { 
			log.error("app:"+e.getMessage()); 
		} catch (NamingException e) {
			log.error("app:"+e.getMessage());
		}
		return connectionMap.get(name);
	}
}
