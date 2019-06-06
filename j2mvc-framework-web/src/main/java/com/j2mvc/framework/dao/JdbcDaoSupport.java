package com.j2mvc.framework.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.j2mvc.framework.dao.callback.PreparedStatementCallBack;
import com.j2mvc.framework.dao.callback.PreparedStatementCreator;
/**
 * 
 * JDBC访问，通过预编译创建器创建语句,通过回调类返回查询结果
 * 
 * 2014-2-24 创建@杨朔
 */
public class JdbcDaoSupport {
	static final Logger log = Logger.getLogger(JdbcDaoSupport.class);
	protected String dataSourceName;
	public JdbcDaoSupport(){}
	/**
	 * 
	 * @param <T>
	 * @param creator 自定义创建器
	 * @param callBack 自定义回调
	 * 
	 */
	public <T> T execute(PreparedStatementCreator creator,PreparedStatementCallBack<T> callBack){
		T t = null;
		Connection con = null;
//		log.debug("dataSourceName is "+dataSourceName);
		if(dataSourceName!=null && !dataSourceName.equals("")){
			con = DataSourceJndiMulti.getConnection(dataSourceName);
		}
//		log.debug("con is "+con);
		if(con == null)
			con = DataSourceJndi.getConnection();
		try {
			if(con != null && !con.isClosed())
			try {
				PreparedStatement pstmt = creator.execute(con);
				t =  callBack.execute(pstmt);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			finally{
				if (con != null && !con.isClosed())
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return t;
	}
}
