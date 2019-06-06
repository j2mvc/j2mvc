package com.j2mvc.framework.dao.callback;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.FieldUtil;

/**
 * 预编译语句返回对象
 * @author 杨朔
 * 2014年1月13日
 */
public class CallbackObject extends PreparedStatementCallBack <Object> {
	Logger log = Logger.getLogger(getClass().getName());

	private int executeType;
	private Class<?> clazz;
	private Object object;
	private String dataSourceName;
	
	public CallbackObject(int executeType, Class<?> clazz,String dataSourceName) {
		super();
		this.executeType = executeType;
		this.clazz = clazz;
		this.dataSourceName = dataSourceName;
	}
	public CallbackObject(int executeType, Object object,String dataSourceName) {
		super();
		this.executeType = executeType;
		this.object = object;
		this.dataSourceName = dataSourceName;
		this.clazz = object!=null?object.getClass():null;
	}

	@Override
	public Object execute(PreparedStatement pstmt) throws SQLException{
		if(executeType == EXECUTE_UPDATE){
			if(clazz !=null && clazz.isAssignableFrom(Integer.class)){
				object = pstmt.executeUpdate();
			}else if(object!=null){
				int i = pstmt.executeUpdate();				
				object = getKey(object, pstmt,i);
			}
		}else if(executeType == EXECUTE_QUERY){
			object = setObject(pstmt);
		}
		pstmt.close();
		return object;
	}
	
	/**
	 * 获取刚插入的数据
	 * @param pstmt
	 * 
	 * @throws SQLException
	 */
	private Object getKey(Object obj,PreparedStatement pstmt,int i) throws SQLException{

		/** 如是自增主键，则获取刚插入主键*/
		PrimaryKey primaryKey = obj.getClass().getAnnotation(PrimaryKey.class);
		if(!primaryKey.autoIncrement()){
			if(i>0)
				return obj;
		}
		ResultSet rsKey = pstmt.getGeneratedKeys();
		rsKey.next();
		try{
			PropertyDescriptor pd = new PropertyDescriptor(primaryKey.name(), obj.getClass());
			Method method = pd.getWriteMethod();

			Field field = FieldUtil.getField(primaryKey.name(), clazz);
			if(field!=null){
				Class<?> type = field.getType();
				/** 设置主键*/				
				if(Integer.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type))
					method.invoke(obj, rsKey.getInt(1));
				else if(Long.class.isAssignableFrom(type) )
					method.invoke(obj, rsKey.getLong(1));
				else{
					method.invoke(obj, rsKey.getString(1));
				}
			}else{
				log.error("未在类属性里找到主键字段");
			}
		}catch (IntrospectionException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		} 
		rsKey.close();
		return obj;
	}

	/**
	 * 对象setter
	 * @param pstmt
	 * 
	 * @throws SQLException
	 */
	private Object setObject(PreparedStatement pstmt) throws SQLException{
		// 主键
		Object obj = null;
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			try {
				obj = clazz.newInstance();
				List<Field> fields = FieldUtil.getFields(null, clazz);
				InvokeObject.invoke(fields, obj, rs,dataSourceName);

				/** 获取刚插入主键*/
				PrimaryKey primaryKey = clazz.getAnnotation(PrimaryKey.class);
				ResultSet rsKey = pstmt.getGeneratedKeys();
				if(rsKey.first())
				try{
					PropertyDescriptor pd = new PropertyDescriptor(primaryKey.name(), clazz);
					Method method = pd.getWriteMethod();

					Field field;
					try {
						field = clazz.getDeclaredField(primaryKey.name());
						Class<?> type = field.getType();
						/** 设置主键*/				
						if(Integer.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type))
							method.invoke(obj, rsKey.getInt(1));
						else if(Long.class.isAssignableFrom(type) )
							method.invoke(obj, rsKey.getLong(1));
						else{
							method.invoke(obj, rsKey.getString(1));
						}
					} catch (SecurityException e) {
						log.error(e.getMessage());
					} catch (NoSuchFieldException e) {
						log.error(e.getMessage());
					}
				}catch (IntrospectionException e) {
					e.printStackTrace();
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				}catch (IllegalAccessException e) {
					e.printStackTrace();
				}catch (InvocationTargetException e) {
					e.printStackTrace();
				}catch (SQLException e) {
					e.printStackTrace();
				} 
				rsKey.close();
			} catch (InstantiationException e) {
				log.error(e.getMessage());
			} catch (IllegalAccessException e) {
				log.error(e.getMessage());
			}catch (IllegalArgumentException e) {
				log.error(e.getMessage());
			} 
		}
		rs.close();
		return obj;
	}
	
}
