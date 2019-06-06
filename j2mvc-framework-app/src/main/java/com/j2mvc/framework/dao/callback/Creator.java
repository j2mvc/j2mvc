package com.j2mvc.framework.dao.callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;

/**
 * 创建预编译语句
 * @author 杨朔
 * 2014年3月28日 创建
 */
public class Creator extends PreparedStatementCreator {
	Logger log = Logger.getLogger(getClass().getName());

	private String sql;
	private Object[] values;
	
	public Creator(String sql, Object[] values) {
		super();
		this.sql = sql;
		this.values = values;
	}
	@Override
	public PreparedStatement execute(Connection con)throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		if(Session.sqlLog)
			log.info("编译前语句 >> "+pstmt.toString().split(":")[1].replaceAll("\\*\\* NOT SPECIFIED \\*\\*", "?"));
		if(values!=null)
		for(int i=0;i<values.length;i++){
			Object value = values[i];
			value = value!=null?value:"";
			Class<?> clazz = value.getClass();
			int index = i+1;
			// 得到数据类型
			if(String.class.isAssignableFrom(clazz) ||
					Date.class.isAssignableFrom(clazz)){
				pstmt.setString(index, (String) value);
			}else if(Integer.class.isAssignableFrom(clazz)){
				pstmt.setInt(index, !value.equals("")?(Integer)value:0);
			}else if(Long.class.isAssignableFrom(clazz))
				pstmt.setLong(index, !value.equals("")?(Long)value:0);
			else if(Float.class.isAssignableFrom(clazz))
				pstmt.setFloat(index, (Float) value);
			else if(Double.class.isAssignableFrom(clazz))
				pstmt.setDouble(index, (Double) value);
			else if(Boolean.class.isAssignableFrom(clazz))
				pstmt.setBoolean(index, (Boolean) value);
			else{
				pstmt.setBytes(index, StreamUtil.objectToBytes(value));		
			}
		}
		if(Session.sqlLog)
			log.info("编译后 >> "+pstmt);
		return pstmt;
	}
}
