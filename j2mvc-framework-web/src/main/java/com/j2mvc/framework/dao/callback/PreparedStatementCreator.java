package com.j2mvc.framework.dao.callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * 预编译语句PreparedStatement创建器
 * 
 * 2014-3-29 创建@杨朔
 */
public abstract class PreparedStatementCreator {
	 
	public abstract PreparedStatement execute(Connection con)throws SQLException;
}
