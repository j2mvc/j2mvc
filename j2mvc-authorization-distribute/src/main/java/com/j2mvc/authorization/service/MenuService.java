package com.j2mvc.authorization.service;

import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.Menu;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;

/**
 * 
 * 菜单项服务
 * 
 * 2014-4-8 创建@杨朔
 */
public class MenuService{

	DaoSupport dao;
	String tableName = EntityConstants.TABLE_MENU;
	
	public MenuService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(Menu.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(Menu.class);
		}
	}
	public MenuService(String dataSourceName){
		dao = new DaoSupport(Menu.class,dataSourceName);
	}
	
	/**
	 * 插入
	 * @param Menu
	 * 
	 */
	private Menu insert(Menu Menu) {		
		return (Menu) dao.insert(Menu);
	}
	/**
	 * 更新
	 * @param Menu
	 * 
	 */
	private Menu update(Menu Menu) {
		return (Menu)dao.update(Menu);
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
	 * @param Menu
	 * 
	 */
	public Menu save(Menu Menu){
		if(Menu == null)
			return null;
		if(get(Menu.getId())!=null){
			// 更新
			Menu = update(Menu);
		}else{
			// 新增
			Menu = insert(Menu);
		}
		return Menu;
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public Menu get(String id) {
		Object object =  dao.get(id);
		return object!=null?(Menu)object:null;
	}

	/**
	 * 获取
	 * @param title
	 * @param projectId
	 * @return
	 */
	public Menu getByTitle(String title,String projectId) {
		String preSql = "SELECT * FROM " + tableName + " WHERE title=? and project_id=?";
		Object object =  dao.get(preSql, new String[]{title,projectId});
		return object!=null?(Menu)object:null;
	}

	/**
	 * 获取顶层菜单
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> queryTops(String projectId) {
		String sql = "SELECT * FROM " + tableName + " as m WHERE project_id=? and "
				+ "(SELECT count(*) FROM " + tableName + " as pm WHERE pm.id=m.parent_id )=0 "
				+ "order by sorter";
		List<?> list =  dao.query(sql, new String[]{projectId});
		return list!=null?(List<Menu>) list:null;
	}

	/**
	 * 获取顶层菜单
	 * @param projectId 菜单组
	 * @param userId 用户ID
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> queryTops(String projectId,String userId) {
		String sql = "SELECT * FROM " + tableName + " as m WHERE project_id=? and "
				+ "(SELECT count(*) FROM " + tableName + " as pm WHERE pm.id=m.parent_id )=0 " 
				// 菜单ID是否在角色菜单表内
				+ " and m.id in"
				+ "(SELECT menu_id FROM "+EntityConstants.TABLE_ROLE_MENU +" WHERE  role_id in  "
				// 用户的所有角色ID
				+ "(SELECT role_id FROM "+EntityConstants.TABLE_USER_ROLE+" WHERE user_id=?)"
				+ ")"
				+ " order by sorter";
		List<?> list =  dao.query(sql, new String[]{projectId,userId});
		return list!=null?(List<Menu>) list:null;
	}

	/**
	 * 获取组菜单总数
	 * @param gid 
	 * 
	 */
	public Integer total(String projectId) {
		String sql = "SELECT COUNT(*) FROM " + tableName + " as m WHERE project_id=? ";
		return dao.number(sql, new String[]{projectId});
	}
	/**
	 * 获取子菜单总数
	 * @param id 
	 * 
	 */
	public Integer totalChildren(String id) {
		String sql = "SELECT COUNT(*) FROM " + tableName + " as m WHERE parent_id=?";
		return dao.number(sql, new String[]{id});
	}

	/**
	 * 获取子菜单
	 * @param id
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> queryChildren(String id) {
		String sql = "SELECT * FROM " + tableName + " as m WHERE parent_id=? order by sorter";
		List<?> list =  dao.query(sql, new String[]{id});
		return list!=null?(List<Menu>) list:null;
	}

	/**
	 * 获取子菜单
	 * @param id
	 * @param userId 用户ID
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> queryChildren(String id,String userId) {
		String sql = "SELECT * FROM " + tableName + " as m WHERE parent_id=? " 
					// 菜单ID是否在角色菜单表内
					+ " and m.id in"
					+ "("
					+ "SELECT menu_id FROM "+EntityConstants.TABLE_ROLE_MENU +" WHERE role_id in  "
					// 用户的所有角色ID
					+ "(SELECT role_id FROM "+EntityConstants.TABLE_USER_ROLE +" WHERE user_id=?)"
					+ ")"
					+ " order by sorter";
		List<?> list =  dao.query(sql, new String[]{id,userId});
		return list!=null?(List<Menu>) list:null;
	}

	/**
	 * 获取指定菜单的顶级菜单
	 * @param id
	 * 
	 */
	public Menu getTop(String id) {
		Menu menu = get(id);
		if(menu.getParentId()!=null)
			menu = getTop(menu.getParentId());
		return menu;
	}
	/**
	 * 获取
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> query() {
		String sql = "SELECT * FROM " + tableName + " order by sorter";
		List<?> list =  dao.query(sql);
		return list!=null?(List<Menu>) list:null;
	}
	/**
	 * 获取
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<Menu>) list:null;
	}

	/**
	 * 查看菜单是否有下级菜单
	 * @param id
	 * 
	 */
	public boolean existsChildren(String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE parent_id=?";
		return dao.number(preSql, new String[]{id})>0;
	}

	/**
	 * 查看菜单项名称是否存在
	 * @param name
	 * 
	 */
	public boolean existsTitle(String title,String projectId) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE title=? and project_id=?";
		return dao.number(preSql, new String[]{title,projectId})>0;
	}
	/**
	 * 查看菜单项名称是否存在
	 * @param name
	 * @param id
	 * 
	 */
	public boolean existsTitle(String title,String id,String projectId) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE title=? and id<>? and project_id=?";
		return dao.number(preSql, new String[]{title,id,projectId})>0;
	}
	
	/**
	 * 查询指定的菜单列表
	 * @param projectId
	 * @param userId
	 * 
	 */
	public List<Menu> query(String projectId,String userId){
		List<Menu> items = getChildren(projectId,null,  userId);
		return items;
	}

	
	/**
	 * 获取子菜单
	 */
	public List<Menu> getChildren(String gid,String parentId,String userId){
		List<Menu> items = null;
		if(parentId!=null && !parentId.equals(""))
			items = queryChildren(parentId,userId);
		else
			items = queryTops(gid, userId);
		if(items!=null){
			for(Menu menu:items){
				menu.setChildren(getChildren(null, menu.getId(), userId));
			}
		}
		return items;
	}
	
}
