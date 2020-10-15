package com.j2mvc.framework.dao.callback;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 预编译语句返回输入流
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackInputStream extends PreparedStatementCallBack<InputStream> {

	private int executeType;
	
	public CallbackInputStream(int executeType) {
		
		this.executeType = executeType;
	}
	
	@Override
	public InputStream execute(PreparedStatement pstmt) throws SQLException{
		InputStream s = null;
		if(executeType == EXECUTE_UPDATE){
			pstmt.executeUpdate();
		}else if(executeType == EXECUTE_QUERY){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				s = rs.getBinaryStream(1);
			}
			rs.last();
			rs.close();
		}
		pstmt.close();
		return s;
	}
}
