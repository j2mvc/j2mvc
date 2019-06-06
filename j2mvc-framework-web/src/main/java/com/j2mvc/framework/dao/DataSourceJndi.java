package com.j2mvc.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.j2mvc.framework.Session;

/**
 * 绑定数据源
 * 
 * 2014-2-24 创建@杨朔
 */
public class DataSourceJndi {
	static Logger log = Logger.getLogger(DataSourceJndi.class);
	public static Connection connection;
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection() {
		try {
			// 实例上下文目录
			Context context = new InitialContext();
			// 在命名空间和目录空间中查找 数据源名称 返回数据库连接池对象 JNDI
			String name = Session.dataSourceBean.getName().replaceAll("/", ":");
			if(null == name || "".equals(name))
				log.error("未找到数据源配置");
			else{
				DataSource dataSource =(DataSource)context.lookup(name);
				connection = dataSource.getConnection();
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
