package com.j2mvc.framework.dao.callback;

import java.io.IOException;
import java.io.InputStream;
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
	static final Logger log = Logger.getLogger(Creator.class);

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
			int index = i+1;
			Object value = values[i];
			if(value == null){
				pstmt.setObject(index, null);
				continue;
			}
			Class<?> clazz = value.getClass();
			// 得到数据类型
			if(String.class.isAssignableFrom(clazz) ||
					Date.class.isAssignableFrom(clazz)){
				pstmt.setString(index, value!=null?String.valueOf(value):null);
			}else if(Integer.class.isAssignableFrom(clazz)){
				pstmt.setInt(index, (Integer)value);
			}else if(Long.class.isAssignableFrom(clazz)){
				pstmt.setLong(index, value!=null?(Long)value:null);
			}
			else if(Float.class.isAssignableFrom(clazz))
				pstmt.setFloat(index, (Float) value);
			else if(Double.class.isAssignableFrom(clazz))
				pstmt.setDouble(index, (Double) value);
			else if(Boolean.class.isAssignableFrom(clazz))
				pstmt.setBoolean(index, (Boolean) value);
			else if(InputStream.class.isAssignableFrom(clazz))
				try {
					pstmt.setBinaryStream(index, (InputStream)value,
					        ((InputStream)value).available());
				} catch (IOException e) {
					e.printStackTrace();
				}
			else{
				pstmt.setBytes(index, StreamUtil.objectToBytes(value));		
			}
		}
		if(Session.sqlLog)
			log.info("编译后 >> "+pstmt);
		return pstmt;
	}
}
