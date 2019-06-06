package com.j2mvc.framework.dao.callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 预编译语句返回字符串列表
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackArrayList extends PreparedStatementCallBack<List<String>> {

	
	@Override
	public List<String> execute(PreparedStatement pstmt) throws SQLException{
		List<String> list = new ArrayList<String>();
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String s = rs.getString(1);
			list.add(s);
		}
		rs.last();
		rs.close();
		pstmt.close();
		return list;
	}
}
