package com.j2mvc.authorization.service;

import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.MenuGroup;
import com.j2mvc.authorization.entity.Role;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;

/**
 * 
 * 菜单组服务
 * 
 * 2014-4-8 创建@杨朔
 */
public class MenuGroupService{

	DaoSupport dao ;
	String tableName = EntityConstants.TABLE_MENU_GROUP;

	public MenuGroupService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(MenuGroup.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(MenuGroup.class);
		}
	}
	public MenuGroupService(String dataSourceName){
		 dao = new DaoSupport(MenuGroup.class,dataSourceName);
	}
	
	/**
	 * 插入
	 * @param menuGroup
	 * 
	 */
	private MenuGroup insert(MenuGroup menuGroup) {		
		return (MenuGroup) dao.insert(menuGroup);
	}
	/**
	 * 更新
	 * @param menuGroup
	 * 
	 */
	private MenuGroup update(MenuGroup menuGroup) {
		return (MenuGroup)dao.update(menuGroup);
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
	 * @param menuGroup
	 * 
	 */
	public MenuGroup save(MenuGroup menuGroup){
		if(menuGroup == null)
			return null;
		if(get(menuGroup.getId())!=null){
			// 更新
			menuGroup = update(menuGroup);
		}else{
			// 新增
			menuGroup = insert(menuGroup);
		}
		return menuGroup;
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public MenuGroup get(String id) {
		Object object =  dao.get(id);
		return object!=null?(MenuGroup)object:null;
	}

	/**
	 * 获取
	 * @param name
	 * @param position
	 * 
	 */
	public MenuGroup get(String name,Integer position) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name=? ";
		if(position!=null)
			preSql += " and position=?";
		Object object =  dao.get(preSql, 
				position!=null?new Object[]{name,position}:new Object[]{name});
		return object!=null?(MenuGroup)object:null;
	}
	/**
	 * 获取
	 * @param name
	 * @param position
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> query(String name,Integer position) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name like ? ";
		if(position!=null)
			preSql += " and position=?";
		Object object = dao.query(preSql, position!=null?new Object[]{"%"+name+"%",position}:new Object[]{"%"+name+"%"});
		return object!=null?(List<MenuGroup>)object:null;
	}

	/**
	 * 获取
	 * @param position
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> query(Integer position) {
		String preSql = "SELECT * FROM " + tableName + " WHERE position=?";
		Object object = dao.query(preSql, new Object[]{position});
		return object!=null?(List<MenuGroup>)object:null;
	}
	/**
	 * 获取
	 * @param name
	 * @param position
	 * @param id
	 * 
	 */
	public MenuGroup get(String name,Integer position,String id) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name=? and position=? and id<>?";
		Object object =  dao.get(preSql, new Object[]{name,position,id});
		return object!=null?(MenuGroup)object:null;
	}
	
	/**
	 * 获取
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> query() {
		String sql = "SELECT * FROM " + tableName + " order by sorter";
		List<?> list =  dao.query(sql);
		return list!=null?(List<MenuGroup>) list:null;
	}
	/**
	 * 获取
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<MenuGroup>) list:null;
	}
	/**
	 * 查看菜单组名称是否存在
	 * @param name
	 * 
	 */
	public boolean existsName(String name) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=?";
		return dao.number(preSql, new String[]{name})>0;
	}

	/**
	 * 查看菜单组名称是否存在
	 * @param name
	 * @param id
	 * 
	 */
	public boolean existsName(String name,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE name=? and id<>?";
		return dao.number(preSql, new String[]{name,id})>0;
	}

	/**
	 * 查看菜单组编号是否存在
	 * @param no
	 * @param id
	 * 
	 */
	public boolean existsNo(String no,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE no=? and id<>?";
		return dao.number(preSql, new String[]{no,id})>0;
	}
	/**
	 * 查看菜单组是否存在菜单
	 * @param id
	 * 
	 */
	public boolean existsMenus(String id) {
		String preSql = "SELECT count(*) FROM " + EntityConstants.TABLE_MENU + " WHERE gid=?";
		return dao.number(preSql, new String[]{id})>0;
	}
	/**
	 * 查询指定导航菜单组
	 * @param navId
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> queryByNav(String navId){
		String sql = "SELECT * FROM "+tableName + " WHERE id in" +
				"(SELECT group_id FROM "+EntityConstants.TABLE_NAV_MENU_GROUP+" WHERE nav_id=? or nav_id in " +
						"(SELECT id FROM "+EntityConstants.TABLE_NAV+" WHERE no=?)" +
						")" +
						"ORDER BY sorter";
		List<?> list =  dao.query(sql,new String[]{navId,navId});
		return list!=null?(List<MenuGroup>) list:null;
		
	}

	/**
	 * 查询指定角色导航菜单组
	 * 1：根据角色与菜单关联
	 * 2：菜单关联菜单组
	 * @param navId
	 * @param roles
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<MenuGroup> query(String navId,List<Role> roles){
		if(roles == null)
			return null;
		if(roles.size() == 0)
			return null;
		String rs = "";
		for(Role r : roles){
			rs += (!rs.equals("")?",":"") + "'"+ r.getId()+"'";
		}
		String s1 = "SELECT menu_id FROM "+EntityConstants.TABLE_ROLE_MENU+" WHERE role_id in("+rs+")";
		String s2 = "SELECT gid FROM "+EntityConstants.TABLE_MENU+" WHERE id in( "+s1+")";
		String sql = "SELECT * FROM "+tableName + " WHERE id in" +
				"(SELECT group_id FROM "+  EntityConstants.TABLE_NAV_MENU_GROUP +" WHERE nav_id=? or nav_id in " +
						"(SELECT id FROM "+EntityConstants.TABLE_NAV+" WHERE no=?)" +
						") " +// 指定导航ID
				"and id in ("+s2+")"+// 指定角色
						"ORDER BY sorter";
		List<?> list =  dao.query(sql,new String[]{navId,navId});
		return list!=null?(List<MenuGroup>) list:null;
		
	}
}
