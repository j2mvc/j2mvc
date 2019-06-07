package com.j2mvc.framework.dao.callback;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.DataSourceName;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.framework.mapping.Sql;
import com.j2mvc.framework.util.FieldUtil;


public class InvokeObject {

	static Logger log = Logger.getLogger(InvokeObject.class.getName());
	/**
	 * 写入字段数组值
	 * @param fields
	 * @param obj
	 * @param rs
	 */
	public static void invoke(List<Field> fields,Object obj,ResultSet rs,String dataSourceName){
		for(int i=0;i<fields.size();i++){
			Field field = fields.get(i);
			if(field!=null)
				invoke(field,obj,rs,dataSourceName);
		}
	}
	/**
	 * 写入字段值
	 * @param field
	 * @param obj
	 * @param rs
	 */	
	private static void invoke(Field field,Object obj,ResultSet rs,String dataSourceName) {

		/* 注释字段,对应数据表字段 */
		Column column = field.getAnnotation(Column.class);
		
		/* 注释字段,对应关联对象字段 */
		Foreign foreign =  field.getAnnotation(Foreign.class);

		/* 注释字段,根据SQL语句查询值，仅返回基础数据类型 */
		Sql sql =  field.getAnnotation(Sql.class);
		
		if(column == null){
			return;
		}
		String name = field.getName();
		/* 数据表数据类型 */		
		Class<?> type = field.getType();	
		try {
			Object value = null;
			if(foreign!=null){
				// 外键对象之主键属性数据类型
				type = FieldUtil.getForeignKey(type).getType();
			}
			if(String.class.isAssignableFrom(type))
				value = rs.getString(column.name());				
			else if(Integer.class.isAssignableFrom(type) 
					|| Short.class.isAssignableFrom(type)
					|| short.class.isAssignableFrom(type)
					|| int.class.isAssignableFrom(type))
				value = rs.getInt(column.name());
			else if(Long.class.isAssignableFrom(type) 
					|| long.class.isAssignableFrom(type))
				value = rs.getLong(column.name());
			else if(Float.class.isAssignableFrom(type)
					|| float.class.isAssignableFrom(type))
				value = rs.getFloat(column.name());
			else if(Double.class.isAssignableFrom(type)
					|| double.class.isAssignableFrom(type))
				value = rs.getDouble(column.name());
			else if(Boolean.class.isAssignableFrom(type)
					|| boolean.class.isAssignableFrom(type))
				value = rs.getBoolean(column.name());
			else if(Time.class.isAssignableFrom(type))
				value = rs.getTime(column.name());
			else if(Date.class.isAssignableFrom(type)){
				value = rs.getDate(column.name());
			}else if(byte[].class.isAssignableFrom(type)){
				value = rs.getBytes(column.name());
			}else if(Collection.class.isAssignableFrom(type)
					|| String[].class.isAssignableFrom(type)){
				byte[] bytes = rs.getBytes(column.name());
				if(bytes!=null)
					value = StreamUtil.bytesToObject(bytes);
			}		
			if(foreign!=null){
				// 外键,字段为对象,获取类主键,并实例
				try {
					Class<?> fieldClass =  field.getType();
					DataSourceName dsn = fieldClass.getAnnotation(DataSourceName.class);	
					DaoSupport daoSupport = null;
					if(dsn!=null){
						daoSupport = new DaoSupport(fieldClass,dsn.value());
					}else if(dataSourceName!=null && !dataSourceName.equals("")){
						daoSupport = new DaoSupport(fieldClass,dataSourceName);
					}else {
						daoSupport = new DaoSupport(fieldClass);
					}
					value = daoSupport.get(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(sql!=null){
				// 根据SQL语句查询值
				try {
					DaoSupport daoSupport = new DaoSupport(field.getType());
					value = daoSupport.get(sql.value());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 执行写入操作
			PropertyDescriptor pd = new PropertyDescriptor(name, obj.getClass());
			Method wm = pd.getWriteMethod();
			if(value!=null)
				wm.invoke(obj, value);			
		} catch (IntrospectionException e) {
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
		
	}
}
