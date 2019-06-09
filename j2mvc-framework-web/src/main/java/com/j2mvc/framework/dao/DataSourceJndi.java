package com.j2mvc.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import com.j2mvc.framework.Session;

/**
 * 绑定多个数据源
 * 
 * 2014-8-18 创建@杨朔
 * @version 1.1.6
 */
public class DataSourceJndi {
	static final Logger log = Logger.getLogger(DataSourceJndi.class);

	static InitialContext ctx; 
	/**
	 * 绑定数据源
	 */
	public static void init() {
		Set<Entry<String, DataSourceBean>> set = Session.dataSourceBeanMap.entrySet();
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
				dataSource.setValidationQuery(bean.getValidationQuery());
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
				ctx = new InitialContext(); 
				
				// 数据源绑定到JNDI
				ctx.bind(name, dataSource);
				
				log.info("绑定JNDI，数据源名称："+bean.getName().replaceAll(":", "/"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 销毁数据源
	 */
	public static void destroy(){
		try {			
			Set<Entry<String, DataSourceBean>> set = Session.dataSourceBeanMap.entrySet();
			Iterator<Entry<String, DataSourceBean>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, DataSourceBean> entry = iterator.next();
				DataSourceBean bean = entry.getValue();
				// 数据源解绑定
				ctx.unbind(bean.getName().replaceAll("/", ":"));	
				log.info("解除绑定JNDI，数据源名称："+bean.getName().replaceAll(":", "/"));
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection() {
		try {
			// 在命名空间和目录空间中查找 数据源名称 返回数据库连接池对象 JNDI
			String name = Session.dataSourceBean.getName().replaceAll("/", ":");
			DataSource dataSource =(DataSource)ctx.lookup(name);	
			return dataSource.getConnection();
		} catch (SQLException e) { 
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection(String name) {
		try {
			// 在命名空间和目录空间中查找 数据源名称 返回数据库连接池对象 JNDI
			DataSource dataSource =(DataSource)ctx.lookup(name.replaceAll("/", ":"));	
			return dataSource.getConnection();
		} catch (SQLException e) { 
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
