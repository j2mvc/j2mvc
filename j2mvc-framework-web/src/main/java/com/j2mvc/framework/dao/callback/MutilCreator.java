package com.j2mvc.framework.dao.callback;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.dao.DataSourceJndi;
import com.j2mvc.framework.dao.DataSourceJndi;

/**
 * 创建预编译语句
 * @author 杨朔
 * 2014年3月28日 创建
 */
public class MutilCreator{
	static final Logger log = Logger.getLogger(MutilCreator.class);

	private List<String> sqls;
	private String dataSourceName;
		
	public MutilCreator(List<String> sqls,String dataSourceName) {
		this.sqls = sqls;
		this.dataSourceName = dataSourceName;
	}
	public int[] execute(){
		int[] num = null;
		Connection con = null;
		if(dataSourceName!=null && !dataSourceName.equals("")){
			con = DataSourceJndi.getConnection(dataSourceName);
		}
		if(con == null)
			con = DataSourceJndi.getConnection();
		try {
			Statement stmt = con.createStatement();
			con.setAutoCommit(false);
			for(String sql : sqls){
				if(Session.sqlLog)
					log.info(sql);
				stmt.addBatch(sql);
			}
			num = stmt.executeBatch();
			stmt.executeBatch();  
			con.commit();
			con.setAutoCommit(true);
			stmt.close();
			con.close();
		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ex.getMessage();
		}finally{
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					log.error(e.getMessage()); 
				}
   		}
		return num;
	}
}
