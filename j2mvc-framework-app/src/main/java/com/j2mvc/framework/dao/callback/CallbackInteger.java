package com.j2mvc.framework.dao.callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 预编译语句返回数值
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackInteger extends PreparedStatementCallBack<Integer> {

	private int executeType;
	
	public CallbackInteger(int executeType) {
		
		this.executeType = executeType;
	}
	
	@Override
	public Integer execute(PreparedStatement pstmt) throws SQLException{
		int num = 0;
		if(executeType == EXECUTE_UPDATE){
			num = pstmt.executeUpdate();
		}
		if(executeType == EXECUTE_QUERY){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				num = rs.getInt(1);
			}
			rs.close();
		}
		pstmt.close();
		return num;
	}
}
