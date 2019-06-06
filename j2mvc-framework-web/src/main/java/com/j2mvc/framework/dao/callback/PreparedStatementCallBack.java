package com.j2mvc.framework.dao.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 预编译语句回调
 * 
 * 实现更新或查询数据库
 *  
 * 2014-3-29 创建@杨朔
 */
public abstract class PreparedStatementCallBack<T> {
	public static final int EXECUTE_QUERY = 0;
	public static final int EXECUTE_UPDATE = 1;
	
	public abstract T execute(PreparedStatement pstmt)  throws SQLException;
}
