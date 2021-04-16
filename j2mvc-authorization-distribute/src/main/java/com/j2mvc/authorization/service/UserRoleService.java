package com.j2mvc.authorization.service;

import java.util.ArrayList;
import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.BaseUser;
import com.j2mvc.authorization.entity.Role;
import com.j2mvc.authorization.entity.UserRole;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.util.Utils;

/**
 * 
 * 角色服务
 * 
 * 2014-4-8 创建@杨朔
 */
public class UserRoleService{

	DaoSupport dao;
	String tableName = EntityConstants.TABLE_USER_ROLE;
	
	public UserRoleService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(UserRole.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(UserRole.class);
		}
	}

	public UserRoleService(String dataSourceName){
		 dao = new DaoSupport(UserRole.class,dataSourceName);
	}
	/**
	 * 插入
	 * @param userRole
	 * 
	 */
	private UserRole insert(UserRole userRole) {		
		return (UserRole) dao.insert(userRole);
	}
	/**
	 * 更新
	 * @param userRole
	 * 
	 */
	private UserRole update(UserRole userRole) {
		return (UserRole)dao.update(userRole);
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
	 * @param urIds
	 * 
	 */
	public int[] delete(String[]urIds) {
		List<String> sqls = new ArrayList<String>();
		for(int i=0;i<urIds.length;i++){
			String[] ids = urIds[i].split(":");
			if(ids.length>1){
				String userId = ids[0];
				String roleId = ids[1];
				String sql = "DELETE FROM "+tableName + " WHERE user_id='"+userId+"' and role_id='"+roleId+"'";
				sqls.add(sql);
			}
		}
		return dao.execute(sqls);
	}
	/**
	 * 保存
	 * @param userRole
	 * 
	 */
	public UserRole save(UserRole userRole){
		if(userRole == null)
			return null;
		if(get(userRole.getId())!=null){
			// 更新
			userRole = update(userRole);
		}else{
			// 新增
			userRole = insert(userRole);
		}
		return userRole;
	}


	/**
	 * 保存用户角色
	 * @param user 用户
	 * @param roles 角色列表
	 * 
	 */
	public boolean save(BaseUser user,List<Role> roles) {
		deleteByUser(user.getId());
		if(!(user != null && roles!=null && roles.size() > 0))
			return false;
		boolean bool = true;
		for(int i=0;i<roles.size();i++){
			Role role = roles.get(i);
			UserRole userRole = get(user.getId(), role.getId());
			if(userRole==null){
				userRole = new UserRole(Utils.createId(),user.getId(), role.getId());
				if(insert(userRole)==null)
					bool = false;
			}
		}
		return bool;
	}

	/**
	 * 删除一组
	 * @param userIds
	 * 
	 */
	public int[] deleteByUser(String...userIds) {
		List<String> sqls = new ArrayList<String>();
		for(int i=0;i<userIds.length;i++){
			String userId = userIds[i];
			String sql = "DELETE FROM "+tableName + " WHERE user_id='"+userId+"'";
			sqls.add(sql);
		}
		return dao.execute(sqls);
	}
	/**
	 * 保存用户角色
	 * @param rids
	 * 
	 */
	public boolean save(String userId,String[] rids) {
		deleteByUser(userId);
		if(userId == null || "".equals(userId) || rids == null )
			return false;
		boolean bool = true;
		for(int i=0;i<rids.length;i++){
			UserRole userRole = get(userId, rids[i]);
			if(userRole==null){
				userRole = new UserRole(Utils.createId(),userId, rids[i]);
				if(insert(userRole)==null)
					bool = false;
			}
		}
		return bool;
	}
	/**
	 * 获取
	 * @param id
	 * 
	 */
	public UserRole get(String id) {
		Object object = dao.get(id);
		return object!=null?(UserRole)object:null;
	}
	/**
	 * 获取
	 * @param roleId
	 * 
	 */
	public UserRole get(String userId,String roleId) {
		String sql = "SELECT * FROM "+ tableName +" WHERE user_id=? and role_id=?";
		Object object = dao.get(sql, new String []{userId,roleId});
		return object!=null?(UserRole)object:null;
	}

	/**
	 * 获取用户的角色列表
	 * @param userId
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<UserRole> queryByUser(String userId) {
		String sql = "SELECT * FROM " + tableName + " WHERE user_id=? order by sorter";
		List<?> list =  dao.query(sql,new String[]{userId});
		return list!=null?(List<UserRole>) list:null;
	}
	/**
	 * 获取列表
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Role> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<Role>) list:null;
	}
}
