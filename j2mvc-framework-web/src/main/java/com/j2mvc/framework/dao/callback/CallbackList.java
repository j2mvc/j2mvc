package com.j2mvc.framework.dao.callback;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.j2mvc.framework.util.FieldUtil;

/**
 * 预编译语句返回列表
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackList extends PreparedStatementCallBack<List<Object>> {
	static final Logger log = Logger.getLogger(CallbackList.class);

	private Class<?> clazz;
	private String dataSourceName;

	public CallbackList(Class<?> clazz,String dataSourceName){
		this.clazz = clazz;
		this.dataSourceName = dataSourceName;
	}

	@Override
	public List<Object> execute(PreparedStatement pstmt) throws SQLException{
		List<Object> list = new ArrayList<Object>();
		List<Field> fields = FieldUtil.getFields(null, clazz);
		if(fields==null){
			log.error(clazz+" fields is null.");
			return list;
		}
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			try {
				Object obj = clazz.newInstance();
				InvokeObject.invoke(fields, obj, rs,dataSourceName);
				list.add(obj);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
		}
		rs.last();
		rs.close();
		pstmt.close();
		return list;
	}
}
