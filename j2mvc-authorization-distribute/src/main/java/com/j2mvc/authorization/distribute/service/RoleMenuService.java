package com.j2mvc.authorization.distribute.service;

import java.util.ArrayList;
import java.util.List;

import com.j2mvc.authorization.distribute.config.AuthConfig;
import com.j2mvc.authorization.distribute.entity.Menu;
import com.j2mvc.authorization.distribute.entity.Role;
import com.j2mvc.authorization.distribute.entity.RoleMenu;
import com.j2mvc.authorization.distribute.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.util.MD5;
import com.j2mvc.util.Utils;

/**
 * 
 * 角色菜单
 * 
 * 2014-4-8 创建@杨朔
 */
public class RoleMenuService{

	DaoSupport dao;
	String tableName = EntityConstants.TABLE_ROLE_MENU;
	String userRoleTable = EntityConstants.TABLE_USER_ROLE;
	
	public RoleMenuService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(RoleMenu.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(RoleMenu.class);
		}
	}

	public RoleMenuService(String dataSourceName){
		 dao = new DaoSupport(RoleMenu.class,dataSourceName);
	}
	/**
	 * 插入
	 * @param roleMenu
	 * 
	 */
	private RoleMenu insert(RoleMenu roleMenu) {		
		return (RoleMenu) dao.insert(roleMenu);
	}
	/**
	 * 更新
	 * @param roleMenu
	 * 
	 */
	private RoleMenu update(RoleMenu roleMenu) {
		return (RoleMenu)dao.update(roleMenu);
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
	 * 保存
	 * @param RoleMenu
	 * 
	 */
	public RoleMenu save(RoleMenu roleMenu){
		if(roleMenu == null)
			return null;
		if(get(roleMenu.getId())!=null){
			// 更新
			roleMenu = update(roleMenu);
		}else{
			// 新增
			roleMenu = insert(roleMenu);
		}
		return roleMenu;
	}


	/**
	 * 保存角色菜单
	 * @param role 角色
	 * @param menus 菜单列表
	 * 
	 */
	public int save(Role role,Menu[] menus) {
		int i=0;
		deleteByRole(role.getId());
		if(menus!=null) {
			for(Menu menu:menus){
				RoleMenu roleMenu = new RoleMenu(MD5.md5(menu.getId()+role.getId()),menu.getId(), role.getId());
				if(save(roleMenu)!=null)
					i++;
			}
		}
		return i;
	}

	/**
	 * 删除一组
	 * @param roleIds
	 * 
	 */
	public int[] deleteByRole(String...roleIds) {
		List<String> sqls = new ArrayList<String>();
		for(int i=0;i<roleIds.length;i++){
			String roleId = roleIds[i];
			String sql = "DELETE FROM "+tableName + " WHERE role_id='"+roleId+"'";
			sqls.add(sql);
		}
		return dao.execute(sqls);
	}
	/**
	 * 保存用户角色
	 * @param rids
	 * 
	 */
	public boolean save(String roleId,String[] mids) {
		deleteByRole(roleId);
		if(roleId == null || "".equals(roleId) || mids == null )
			return false;
		boolean bool = true;
		for(int i=0;i<mids.length;i++){
			RoleMenu RoleMenu = get(roleId, mids[i]);
			if(RoleMenu==null){
				RoleMenu = new RoleMenu(Utils.createId(),roleId, mids[i]);
				if(insert(RoleMenu)==null)
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
	public RoleMenu get(String id) {
		Object object = dao.get(id);
		return object!=null?(RoleMenu)object:null;
	}
	/**
	 * 获取
	 * @param roleId
	 * 
	 */
	public RoleMenu get(String roleId,String menuId) {
		String sql = "SELECT * FROM "+ tableName +" WHERE role_id=? and menu_id=?";
		Object object = dao.get(sql, new String []{roleId,menuId});
		return object!=null?(RoleMenu)object:null;
	}

	/**
	 * 获取角色的菜单列表
	 * @param userId
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<RoleMenu> queryByRole(String roleId) {
		String sql = "SELECT * FROM " + tableName + " WHERE role_id=? order by sorter";
		List<?> list =  dao.query(sql,new String[]{roleId});
		return list!=null?(List<RoleMenu>) list:null;
	}

	/**
	 * 获取角色的菜单ID列表
	 * @param roleId
	 * 
	 */
	public List<String> queryMenuIdByRole(String roleId) {
		String sql = "SELECT menu_id FROM " + tableName + " WHERE role_id=?";
		return  dao.queryArray(sql,new String[]{roleId});
	}


	/**
	 * 获取用户的菜单ID列表
	 * @param roleId
	 * 
	 */
	public List<String> queryMenuIdByUser(String useId) {
		String sql = "SELECT menu_id FROM " + tableName + " WHERE role_id in("
				+ "SELECT role_id FROM "+userRoleTable+" WHERE user_id=?)";
		return  dao.queryArray(sql,new String[]{useId});
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
