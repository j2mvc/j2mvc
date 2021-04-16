package com.j2mvc.authorization.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.Auth;
import com.j2mvc.authorization.entity.Menu;
import com.j2mvc.authorization.entity.Role;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;

/**
 * 
 * 角色服务
 * 
 * 2014-4-8 创建@杨朔
 */
public class RoleService implements Serializable{
	private static final long serialVersionUID = -969868251460398049L;
	DaoSupport dao ;
	String tableName = EntityConstants.TABLE_ROLE;
	
	public RoleService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			  dao = new DaoSupport(Role.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(Role.class);
		}
	}

	public RoleService(String dataSourceName){
		 dao = new DaoSupport(Role.class,dataSourceName);
	}
	/**
	 * 插入
	 * @param role
	 * 
	 */
	private Role insert(Role role) {		
		return (Role) dao.insert(role);
	}
	/**
	 * 更新
	 * @param role
	 * 
	 */
	private Role update(Role role) {
		return (Role)dao.update(role);
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
	 * @param role
	 * 
	 */
	public Role save(Role role){
		if(role == null)
			return null;
		if(get(role.getId())!=null){
			// 更新
			role = update(role);
		}else{
			// 新增
			role = insert(role);
		}
		return role;
	}
	/**
	 * 创建用户默认设置的角色
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Role> queryDefaultSets(){
		String sql = "SELECT * FROM "+tableName + " WHERE default_set=1";
		List<?> list =  dao.query(sql);
		return list!=null?(List<Role>) list:null;
	}

	/**
	 * 创建用户默认设置的角色ID
	 * 
	 */
	public List<String> queryDefaultSetsId(){
		String sql = "SELECT id FROM "+tableName + " WHERE default_set=1";
		return  dao.queryArray(sql,null);
	}
	/**
	 * 清空指定角色的权限
	 * @param roleId
	 * 
	 */
	public int clearAuths(String roleId){
		String sql = "DELETE FROM "+EntityConstants.TABLE_ROLE_AUTH+" WHERE role_id=?";
		return dao.execute(sql, new String[]{roleId});
	}


	/**
	 * 保存角色权限
	 * @param role
	 * @param auths
	 */
	public int[] saveAuths(Role role,List<Auth> auths) {
		if(!(role != null && auths!=null && auths.size() > 0))
			return null;
		List<String> sqls = new ArrayList<String>();
		sqls.add("DELETE FROM "+EntityConstants.TABLE_ROLE_AUTH + " WHERE role_id='"+role.getId()+"'");
		for(int i=0;i<auths.size();i++){
			Auth auth = auths.get(i);
			sqls.add("INSERT INTO "+EntityConstants.TABLE_ROLE_AUTH 
					+ "(id,auth_id,role_id)"
					+ "values"
					+ "('"+createId()+"','"+auth.getId()+"','"+role.getId()+"')");
		}
		return dao.execute(sqls);
	}


	/**
	 * 清空指定角色的菜单
	 * @param roleId
	 * 
	 */
	public int clearMenus(String roleId){
		String sql = "DELETE FROM "+EntityConstants.TABLE_ROLE_MENU+" WHERE role_id=?";
		return dao.execute(sql, new String[]{roleId});
	}


	/**
	 * 保存角色菜单
	 * @param role
	 * @param menus
	 * 
	 */
	public int[] saveMenus(Role role,List<Menu> menus) {
		if(!(role != null && menus!=null && menus.size() > 0))
			return null;
		List<String> sqls = new ArrayList<String>();
		sqls.add("DELETE FROM "+EntityConstants.TABLE_ROLE_MENU + " WHERE role_id='"+role.getId()+"'");
		for(int i=0;i<menus.size();i++){
			Menu menu = menus.get(i);
			sqls.add("INSERT INTO "+EntityConstants.TABLE_ROLE_MENU 
					+ "(id,menu_id,role_id)"
					+ "values"
					+ "('"+createId()+"','"+menu.getId()+"','"+role.getId()+"')");
		}
		return dao.execute(sqls);
	}
	/**
	 * 获取
	 * @param id
	 * 
	 */
	public Role get(String id) {
		Object object =  dao.get(id);
		return object!=null?(Role)object:null;
	}

	/**
	 * 获取
	 * @param name
	 * 
	 */
	public Role getByName(String name) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name=?";
		Object object =  dao.get(preSql, new String[]{name});
		return object!=null?(Role)object:null;
	}

	/**
	 * 获取角色总数
	 * 
	 */
	public Integer total() {
		String sql = "SELECT COUNT(*) FROM " + tableName;
		return dao.number(sql);
	}

	/**
	 * 分页获取角色
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Role> query(Integer start,Integer limit) {
		String sql = "SELECT * FROM " + tableName + " order by sorter limit ?,?";
		List<?> list =  dao.query(sql,new Integer[]{start,limit});
		return list!=null?(List<Role>) list:null;
	}
	/**
	 * 获取所有角色
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Role> query() {
		String sql = "SELECT * FROM " + tableName + " order by sorter";
		List<?> list =  dao.query(sql);
		return list!=null?(List<Role>) list:null;
	}
	/**
	 * 获取角色
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Role> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<Role>) list:null;
	}
	/**
	 * 查看角色名称是否存在
	 * @param name
	 * 
	 */
	public boolean existsName(String name) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=?";
		return dao.number(preSql, new String[]{name})>0;
	}

	/**
	 * 查看角色名称是否存在
	 * @param name
	 * @param id
	 * 
	 */
	public boolean existsName(String name,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=? and id<>?";
		return dao.number(preSql, new String[]{name,id})>0;
	}

	static int i=0;
	private String createId(){
		long timestamp = System.currentTimeMillis();
		if(i<10000)
			i++;
		else {
			i = 0;
		}
		return timestamp + "_"+i;
	}
}
