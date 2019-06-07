package com.j2mvc.framework.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;
import com.j2mvc.framework.dao.callback.CallbackArrayList;
import com.j2mvc.framework.dao.callback.CallbackInteger;
import com.j2mvc.framework.dao.callback.CallbackList;
import com.j2mvc.framework.dao.callback.CallbackObject;
import com.j2mvc.framework.dao.callback.CallbackString;
import com.j2mvc.framework.dao.callback.Creator;
import com.j2mvc.framework.dao.callback.MutilCreator;
import com.j2mvc.framework.dao.callback.ObjectFieldsValue;
import com.j2mvc.framework.dao.callback.PreparedStatementCallBack;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.DataSourceName;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.framework.mapping.Transient;
import com.j2mvc.framework.util.FieldUtil;
/**
 * 数据层访问
 * 
 * 传入实体类名，实现实体类关联数据表增、删、改、查。
 * 
 * 2014-3-29 创建@杨朔
 */
public class DaoSupport extends JdbcDaoSupport implements Serializable{
	private static final long serialVersionUID = -6480408267186862188L;

	static final Logger log = Logger.getLogger(DaoSupport.class);

	private Class<?> clazz;	
	private Table table; 
	private PrimaryKey primaryKey;
	private Column keyColumn;
	 
	public DaoSupport() {
		super();
	}
	public DaoSupport(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public DaoSupport(Class<?> clazz){
		DataSourceName dsn = clazz.getAnnotation(DataSourceName.class);	
		if(dsn!=null){
			this.dataSourceName = dsn.value();
		}
		this.clazz = clazz;
		initialize();
	}
	public DaoSupport(Class<?> clazz,String dataSourceName) {
		this.clazz = clazz;
		this.dataSourceName = dataSourceName;
		initialize();
	}
	private void initialize(){
		// 表名
		table = clazz.getAnnotation(Table.class);
		// 主键
		primaryKey = clazz.getAnnotation(PrimaryKey.class);
		if(primaryKey == null)
			log.error("主键未设置");
		keyColumn = FieldUtil.getColumn(primaryKey.name(), clazz);
	}
	/** 
	 * 生成插入语句	
	 * @return String
	 */
	private String getInsertSql(){
		String insertSql = "";
		String fields_sql	 = "";
		String values_sql	 = "";

		List<Field> fields = FieldUtil.getFields(null,clazz);
		for(int i=0;i<fields.size();i++){
			Field field = fields.get(i);
			/* 注释字段,对应数据表字段 */
			Column column = field.getAnnotation(Column.class);
			Transient transient1 = field.getAnnotation(Transient.class);
			// 字段不为空， 且当前字段为主键，且主键自动增长，忽略
			if(transient1 == null && column!=null && !(primaryKey.autoIncrement()
									&& field.getName().equals(primaryKey.name()))){
				fields_sql = fields_sql + (!fields_sql.equals("") ? "," : "") + "`" + column.name() + "`";
				values_sql = values_sql +  (!values_sql.equals("") ?",":"") + "?";
			}
		}
		insertSql = "INSERT INTO "+ table.value() + "(" + fields_sql + ")VALUES(" + values_sql + ")";
				
		return insertSql;
	}
	/**
	 * 生成更新语句	 
	 * 
	 */
	private String getUpdateSql(){
		String updateSql = "";
		String fields_sql	 = "";

		List<Field> fields = FieldUtil.getFields(null,clazz);
		for(int i=0;i<fields.size();i++){
			Field field = fields.get(i);
			/* 注释字段,对应数据表字段 */
			Column column = field.getAnnotation(Column.class);
			Transient transient1 = field.getAnnotation(Transient.class);
			// 字段不为空， 且当前字段为主键，且主键自动增长，忽略
			if(transient1 == null && column!=null &&  !(primaryKey.autoIncrement()
									&& field.getName().equals(primaryKey.name()))){
				fields_sql = fields_sql + (!fields_sql.equals("") ? "," : "") + "`" + column.name() + "`" + "=?";
			}
		}
		updateSql = "UPDATE " + table.value() + " SET  " + fields_sql + " WHERE " + "`" + keyColumn.name() + "`" + "=?";
				
		return updateSql;
	}

	/**
	 * 生成删除语句	
	 * 
	 * 
	 */
	private String getDeleteSql(){
		return "DELETE FROM " + table.value() + " WHERE " + "`" + keyColumn.name() + "`" + "=?";
	}
	
	/**
	 * 检查
	 * 
	 */
	private boolean check(){
		if(clazz == null){
			log.error("class为空，需要设置class，如,dao.setClazz(User.class).");
			return false;
		}
		if(table == null){
			log.error("未在"+clazz+"内注解Table，如：@Table(\"users\")");
			return false;
		}
		if(primaryKey == null){
			log.error("未在"+clazz+"类声明位置注解primaryKey，"
					+ "如：@PrimaryKey(name = \"id\",  autoIncrement = false).");
			return false;
		}
		return true;
	}
	/**
	 * 新增
	 * @param object
	 */
	public  Object insert(Object object){
		if(!check())
			return null;
		Object[] params = new ObjectFieldsValue(object).getValues(false);
		Creator creator = new Creator(getInsertSql(),params);
		CallbackObject callback = new CallbackObject(PreparedStatementCallBack.EXECUTE_UPDATE,object,dataSourceName);
		return execute(creator,callback);
	}
	/**
	 * 更新
	 * @param object
	 */
	public Object update(Object object){
		if(!check())
			return null;
		Object[] params = new ObjectFieldsValue(object).getValues(true);
		Creator creator = new Creator(getUpdateSql(),params);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_UPDATE);		
		if(execute(creator,callback)>0){
			return object;
		}else{
			return null;
		}
	}

	/**
	 * 预编译语句更新
	 * @param sql 预编译语句
	 * @param params 相对应的值
	 */
	public Integer update(String sql,Object[] params){
		Creator creator = new Creator(sql,params);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_UPDATE);
		return execute(creator,callback);
	}

	/**
	 * 预编译语句执行
	 * @param sql 预编译语句
	 * @param params 相对应的值
	 */
	public Integer execute(String sql,Object[] params){
		Creator creator = new Creator(sql,params);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_UPDATE);
		return execute(creator,callback);
	}
	/** 
	 * 执行多条语句
	 * @param sqls
	 * 
	 */
	public int[] execute(List<String> sqls){
		if(sqls!=null && sqls.size()>0)
			return new MutilCreator(sqls,dataSourceName).execute();
		else {
			return null;
		}
	}

	/**
	 * 根据主键删除
	 * @param key
	 */
	public Integer delete(Object key){
		if(!check())
			return 0;
		Creator creator = new Creator(getDeleteSql(),new Object[]{key});
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_UPDATE);
		return execute(creator,callback);
	}

	/**
	 * 根据主键删除
	 * @param key
	 */
	public Integer delete(Object...key){
		if(!check())
			return 0;
		if(!(key!=null && key.length>0))
			return 0;
		String sql = "DELETE FROM " + table.value() + " WHERE " + "`" + keyColumn.name() + "`" + " in(";
		for(int i=0;i<key.length;i++){
			sql += i>0?",?":"?";
		}
		sql += ")";
		Creator creator = new Creator(sql,key);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_UPDATE);
		return execute(creator,callback);
	}
	/**
	 * 根据主键获取指定条目
	 * @param key
	 * @return Object
	 */
	public Object get(Object key){
		if(!check())
			return null;
		if(keyColumn == null)
			log.error("未在"+clazz+"类声明位置注解primaryKey，"
					+ "如：@PrimaryKey(name = \"id\",  autoIncrement = false).");
		if(table == null)
			log.error("未在"+clazz+"内注解Table，如：@Table(\"users\")");
		String sql = "SELECT * FROM " + table.value() + " WHERE " + "`" + keyColumn.name() + "`" + "=?";
		Object[] params ={key};			
		Creator creator = new Creator(sql,params);
		CallbackObject callback = new CallbackObject(PreparedStatementCallBack.EXECUTE_QUERY,clazz,dataSourceName);
		return execute(creator,callback);
	}

	/**
	 * 预编译语句查询列表
	 * @param preSql 预编译语句
	 * @param params 相对应的值
	 * @return List<?>
	 */
	public List<?> query(String preSql,Object[] params){
		if(!check())
			return null;
		Creator creator = new Creator(preSql,params);
		CallbackList callback = new CallbackList(clazz,dataSourceName);
		return execute(creator,callback);
	}

	/**
	 * 预编译语句查询对象
	 * @param preSql 预编译语句
	 * @param params 相对应的值
	 * @return Object
	 */
	public Object get(String preSql,Object[] params){
		if(!check())
			return null;
		List<?> list = query(preSql, params);
		return list!=null && list.size()>0?list.get(0):null;
	}
	/**
	 * 预编译语句查询数量
	 * @param sql 预编译语句
	 * @param params 相对应的值
	 * @return Integer
	 */
	public Integer number(String sql,Object[] params){
		Creator creator = new Creator(sql,params);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_QUERY);
		return execute(creator,callback);
	}

	/**
	 * 根据SQL查询，返回LIST集合
	 * @param sql
	 * @return List<?>
	 */
	public List<?> query(String sql){
		if(!check())
			return null;
		Creator creator = new Creator(sql,null);
		CallbackList callback = new CallbackList(clazz,dataSourceName);
		return execute(creator,callback);
	}

	/**
	 * 根据SQL查询查询对象
	 * @param sql 
	 * @return Object
	 */
	public Object queryForObject(String sql){
		if(!check())
			return null;
		List<?> list = query(sql);
		return list!=null && list.size()>0?list.get(0):null;
	}
	/**
	 * 根据SQL查询查询对象
	 * @param sql 
	 * @param params 
	 * @return Object
	 */
	public Object queryForObject(String sql,Object[] params){
		if(!check())
			return null;
		List<?> list = query(sql,params);
		return list!=null && list.size()>0?list.get(0):null;
	}
	/**
	 * 返回所有
	 * @param sorterField 排序字段
	 * @param esc 是否顺序，否则倒序
	 * @return List<?>
	 */
	public List<?> all(String sorterField,boolean esc){
		if(!check())
			return null;
		String sql = "SELECT * FROM " + table.value() + " order by " + "`" + sorterField + "`" + (!esc ? " desc" : "");
		Creator creator = new Creator(sql,null);
		CallbackList callback = new CallbackList(clazz,dataSourceName);
		return execute(creator,callback);
	}

	/**
	 * 返回所有
	 * @return List<?>
	 */
	public List<?> all(){
		if(!check())
			return null;
		String sql = "SELECT * FROM "+table.value();
		Creator creator = new Creator(sql,null);
		CallbackList callback = new CallbackList(clazz,dataSourceName);
		return execute(creator,callback);
	}
	/**
	 * 根据SQL查询，返回LIST集合
	 * @param sql
	 * @return List<Object>
	 */
	public List<Object> queryOnlyInteger(String sql){
		if(!check())
			return null;
		Creator creator = new Creator(sql,null);
		CallbackList callback = new CallbackList(Integer.class,dataSourceName);
		return execute(creator,callback);
	}

	/**
	 * 获取数值
	 * @param sql
	 * @return Integer
	 */
	public Integer number(String sql){
		Creator creator = new Creator(sql,null);
		CallbackInteger callback = new CallbackInteger(PreparedStatementCallBack.EXECUTE_QUERY);
		return execute(creator,callback);
	}

	/**
	 * 获取单字段
	 * @param sql
	 * @return Integer
	 */
	public String string(String sql){
		Creator creator = new Creator(sql,null);
		CallbackString callback = new CallbackString(PreparedStatementCallBack.EXECUTE_QUERY);
		return execute(creator,callback);
	}

	/**
	 * 预编译语句查询字符
	 * @param sql 预编译语句
	 * @param params 相对应的值
	 * @return Integer
	 */
	public String string(String sql,Object[] params){
		Creator creator = new Creator(sql,params);
		CallbackString callback = new CallbackString(PreparedStatementCallBack.EXECUTE_QUERY);
		return execute(creator,callback);
	}

	/**
	 * 预编译语句查询字符列表
	 * @param sql 预编译语句
	 * @param params 相对应的值
	 * @return Integer
	 */
	public List<String> queryArray(String sql,Object[] params){
		Creator creator = new Creator(sql,params);
		CallbackArrayList callback = new CallbackArrayList();
		return execute(creator,callback);
	}
}
