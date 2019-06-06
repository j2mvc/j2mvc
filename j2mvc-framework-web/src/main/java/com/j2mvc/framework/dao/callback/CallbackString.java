package com.j2mvc.framework.dao.callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 预编译语句返回字符
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackString extends PreparedStatementCallBack<String> {

	private int executeType;
	
	public CallbackString(int executeType) {
		
		this.executeType = executeType;
	}
	
	@Override
	public String execute(PreparedStatement pstmt) throws SQLException{
		String s = null;
		if(executeType == EXECUTE_UPDATE){
			pstmt.executeUpdate();
		}else if(executeType == EXECUTE_QUERY){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				s = rs.getString(1);
			}
			rs.last();
			rs.close();
		}
		pstmt.close();
		return s;
	}
}
