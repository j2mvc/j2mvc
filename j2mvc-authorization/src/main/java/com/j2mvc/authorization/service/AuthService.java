package com.j2mvc.authorization.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.Auth;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;

/**
 * 
 * 权限服务
 * 
 * 2014-4-8 创建@杨朔
 * @version 1.1.7 2014-9-5
 */
public class AuthService{
	String procName = "saveauth";
	Logger log = Logger.getLogger(getClass());

	DaoSupport dao ;
	String tableName = EntityConstants.TABLE_AUTH;
	public AuthService() {
		 if(!AuthConfig.dataSourceName.equals("")){
			 //procName = AuthConfig.dataSourceName+"_"+procName;
			 dao = new DaoSupport(Auth.class,AuthConfig.dataSourceName);
		 }else {
			 dao = new DaoSupport(Auth.class);
		}
	}
	public AuthService(String dataSourceName) {
		//procName = dataSourceName+"_"+procName;
		dao = new DaoSupport(Auth.class,dataSourceName);
	}
	/**
	 * 插入
	 * @param auth
	 * 
	 */
	private Auth insert(Auth auth) {		
		return (Auth) dao.insert(auth);
	}
	/**
	 * 更新
	 * @param auth
	 * 
	 */
	private Auth update(Auth auth) {
		return (Auth)dao.update(auth);
	}

	/**
	 * 删除
	 * @param id
	 * 
	 */
	public Integer delete(String id) {
		return dao.delete(id);
	}
	/**
	 * 删除一组
	 * @param ids
	 * 
	 */
	public Integer delete(String...ids) {
		Object[] object = ids;
		return dao.delete(object);
	}
	/**
	 * 保存
	 * @param auth
	 * 
	 */
	public Auth save(Auth auth){
		if(auth == null)
			return null;
		if(get(auth.getId())!=null){
			// 更新
			auth = update(auth);
		}else{
			// 新增
			auth = insert(auth);
		}
		return auth;
	}

	/**
	 * 存储过程保存权限	
	 * @param auths
	 * 
	 */
	public int callSave(List<Auth> auths){
		if(!(auths!=null && auths.size() > 0))
			return 0;
		StringBuffer procedureSql = new StringBuffer();
		procedureSql.append("CREATE PROCEDURE "+procName+"(IN a_id VARCHAR(255) character set utf8,\n"
									+ "IN a_name VARCHAR(255) character set utf8,\n"
									+ "IN a_value VARCHAR(255) character set utf8,\n"
									+ "IN a_type INT,\n"
									+ "IN status CHAR(1),\n"
									+ "IN extra_limit INT,\n"
									+ "IN a_enable BOOLEAN,\n"
									+ "IN a_tag VARCHAR(255) character set utf8,\n"
									+ "IN a_auth_none VARCHAR(255) character set utf8)\n");
		procedureSql.append("BEGIN\n"+
								"DECLARE count INT DEFAULT 0;\n"+
								"SELECT COUNT(*) INTO count FROM "+tableName+" WHERE auth_type=a_type and auth_value=a_value;\n"+
								"IF count > 0 THEN\n"+
									(AuthConfig.enableUpdate? // 可自动更新
									"\tUPDATE "+tableName+" SET name=a_name,extra_limit=extra_limit,enable_edit=a_enable,tag=a_tag,tag=a_auth_none\n"+
									"\tWHERE auth_type=a_type and auth_value=a_value;\n"
									:"") +
									(AuthConfig.enablestatusUpdate? // 可自动更新开启状态
									"\tUPDATE "+tableName+" SET status=status\n"+
									"\tWHERE auth_type=a_type and auth_value=a_value;\n"
									:"") +
								"ELSE\n"+
									"\tINSERT INTO "+tableName+" (id,name,auth_value,auth_type,status,extra_limit,enable_edit,tag,auth_none) VALUES\n"+
									"\t(a_id,a_name,a_value,a_type,status,extra_limit,a_enable,a_tag,a_auth_none);\n"+
								"END IF;\n" +
							"END;");
		List<String> sqls = new ArrayList<String>();
		sqls.add("DROP PROCEDURE "+ procName+";\n");
		sqls.add(procedureSql.toString());
		for(int i=0;i<auths.size();i++){
			Auth auth = auths.get(i);
			String value = auth.getValue();
			value = value.replace("\\", "\\\\");
			String sql = "{call  "+procName+"('"+auth.getId()+"',"+
										"'"+auth.getName()+"',"+
										"'"+value+"',"+
										auth.getType()+","+
										"'"+auth.getStatus()+"',"+
										auth.getLimit()+","+
										auth.isEnableEdit()+","+
										"'"+auth.getTag()+"',"+
										"'"+auth.getAuthNone()+"')}";
			sqls.add(sql);
		}
		int[] nums = dao.execute(sqls);
		log.debug("执行权限更新语句"+(nums!=null?nums.length:0)+"组。");
		return nums!=null?nums.length:0;
		
	}
	/**
	 * 获取
	 * @param id
	 * 
	 */
	public Auth get(String id) {
		Object object =  dao.get(id);
		return object!=null?(Auth)object:null;
	}

	/**
	 * 获取
	 * @param name
	 * 
	 */
	public Auth getByName(String name) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name=?";
		Object object =  dao.get(preSql, new String[]{name});
		return object!=null?(Auth)object:null;
	}

	/**
	 * 获取
	 * @param tag
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Auth> getByTag(String tag) {
		String preSql = "SELECT * FROM " + tableName + " WHERE tag=?";
		List<?> list =  dao.query(preSql, new Object[]{tag});
		return list!=null?(List<Auth>) list:null;
	}
	/**
	 * 获取
	 * @param tag
	 * @param type
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Auth> getByTagAndType(String tag,Integer type) {
		String preSql = "SELECT * FROM " + tableName + " WHERE tag=? and auth_value=?";
		List<?> list =  dao.query(preSql, new Object[]{tag,type});
		return list!=null?(List<Auth>) list:null;
	}
	/**
	 * 查找指定值和指定权限类型和开关状态的权限
	 * @param value
	 * @param type
	 * @param status
	 * 
	 */
	public Auth get(String value,int type,String status) {
		String preSql = "SELECT * FROM " + tableName + " WHERE "
				+ "? regexp CONCAT('^',auth_value,'$') "
				+ "and auth_type=? and status=?";
		if(status.equals(AuthConstants.AUTH_TYPE_PATH) || status.equals(AuthConstants.AUTH_TYPE_URI)){
			// 查询字段值是否包含在URI中
			preSql = "SELECT * FROM " + tableName + " WHERE instr(?,auth_value) and auth_type=? and status=?";
		}
		Object object =  dao.get(preSql, new Object[]{value,type,status});
		return object!=null?(Auth)object:null;
	}
	/**
	 * 查找开启路径权限的权限
	 * @param value
	 * 
	 */
	public Auth getAuthPath(String value) {
		String preSql = "SELECT * FROM " + tableName + " WHERE ( auth_value like ? or auth_value like ?) ";
		preSql +=  " and auth_type=? and status=?";
		Object object =  dao.get(preSql, new Object[]{
				value.endsWith("/")?value.substring(0,value.lastIndexOf("/")):value,
				!value.endsWith("/")?value+"/":value,
				AuthConstants.AUTH_TYPE_PATH,AuthConstants.AUTH_STATUS_Y});
		return object!=null?(Auth)object:null;
	}
	/**
	 * 查找当前用户是否有当前权限
	 * @param id
	 * @param userId
	 * 
	 */
	public boolean exists(String id,String userId) {
		String preSql = "SELECT COUNT(*) FROM " + tableName + " WHERE id=? and id in"
				+ "(SELECT auth_id FROM "+EntityConstants.TABLE_ROLE_AUTH+" WHERE role_id in"
				+ "(SELECT role_id FROM "+EntityConstants.TABLE_USER_ROLE+" WHERE user_id=?))";
		return dao.number(preSql, new Object[]{id,userId}) > 0;
	}
	
	/**
	 * 查找指定用户指定类型指定值和开关状态的权限
	 * @param value
	 * @param type
	 * @param status
	 * @param userId
	 * 
	 */
	public boolean exists(String value,int type,String status,String userId) {
		String preSql = "SELECT COUNT(*) FROM " + tableName + " WHERE  ? regexp CONCAT('^',auth_value,'$')";
//		if(type == AuthConstants.AUTH_TYPE_PATH){
//			// 查询字段值是否包含在URI中
//			preSql = "SELECT * FROM " + tableName + " WHERE instr(value,?)";
//		}
		preSql +=  " and auth_type=? and status=? and id in"
				+ "(SELECT auth_id FROM "+EntityConstants.TABLE_ROLE_AUTH+" WHERE role_id in"
				+ "(SELECT role_id FROM "+EntityConstants.TABLE_USER_ROLE+" WHERE user_id=?))";
		return dao.number(preSql, new Object[]{value,type,status,userId}) > 0;
	}
	/**
	 * 获取
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Auth> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<Auth>) list:null;
	}
		
	/**
	 * 查看权限名称是否存在
	 * @param name
	 * 
	 */
	public boolean existsName(String name) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=?";
		return dao.number(preSql, new String[]{name})>0;
	}

	/**
	 * 查看权限名称是否存在
	 * @param name
	 * @param id
	 * 
	 */
	public boolean existsName(String name,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=? and id<>?";
		return dao.number(preSql, new String[]{name,id})>0;
	}

	/**
	 * 查看权限值是否存在
	 * @param id
	 * @param value
	 * @param type
	 * 
	 */
	public boolean existsValue(String id,String value,int type) {
		String preSql = "SELECT count(*) FROM " + tableName + " "
				+ "WHERE  id<>? "
				+ "and ? regexp  CONCAT('^',auth_value,'$')"
				+ " and auth_type=? ";
		return dao.number(preSql, new Object[]{id,value,type})>0;
	}
}
