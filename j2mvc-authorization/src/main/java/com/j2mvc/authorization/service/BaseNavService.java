package com.j2mvc.authorization.service;

import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.BaseNav;
import com.j2mvc.authorization.entity.Role;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;

/**
 * 
 * 导航
 * 
 * 2016-8-31 创建@杨朔
 */
public class BaseNavService{

	DaoSupport dao;
	String tableName = EntityConstants.TABLE_NAV;
	String menuTable = EntityConstants.TABLE_MENU;
	String navMenuGroupTable = EntityConstants.TABLE_NAV_MENU_GROUP;
	String roleMenuTable = EntityConstants.TABLE_ROLE_MENU;
	
	public BaseNavService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(BaseNav.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(BaseNav.class);
		}
	}
	public BaseNavService(String dataSourceName){
		dao = new DaoSupport(BaseNav.class,dataSourceName);
	}
	
	/**
	 * 插入
	 * @param nav
	 * 
	 */
	private BaseNav insert(BaseNav nav) {		
		return (BaseNav) dao.insert(nav);
	}
	/**
	 * 更新
	 * @param nav
	 * 
	 */
	private BaseNav update(BaseNav nav) {
		return (BaseNav)dao.update(nav);
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
	 * @param nav
	 * 
	 */
	public BaseNav save(BaseNav nav){
		if(nav == null)
			return null;
		if(get(nav.getId())!=null){
			// 更新
			nav = update(nav);
		}else{
			// 新增
			nav = insert(nav);
		}
		return nav;
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public BaseNav get(String id) {
		Object object =  dao.get(id);
		return object!=null?(BaseNav)object:null;
	}

	/**
	 * 获取
	 * @param name
	 * 
	 */
	public BaseNav getByName(String name) {
		String preSql = "SELECT * FROM " + tableName + " WHERE name=?";
		Object object =  dao.get(preSql, new String[]{name});
		return object!=null?(BaseNav)object:null;
	}

	/**
	 * 获取顶层菜单
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<BaseNav> query() {
		String sql = "SELECT * FROM " + tableName
				+ " order by sorter";
		List<?> list =  dao.query(sql, new String[]{});
		return list!=null?(List<BaseNav>) list:null;
	}

	/**
	 * 获取顶层菜单
	 * @param roles
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<BaseNav> queryByRole(List<Role> roles) {
		if (roles == null)
			return null;
		if (roles.size() == 0)
			return null;
		String rs = "";
		for (Role r : roles) {
			rs = rs + (!rs.equals("") ? "," : "") + "'" + r.getId() + "'";
		}
		String s1 = "SELECT menu_id FROM "+roleMenuTable+" WHERE role_id in(" + rs+ ")";
		String s2 = "SELECT gid FROM "+menuTable+" WHERE id in( " + s1 + ")";
		String s3 = "SELECT nav_id FROM "+navMenuGroupTable+" WHERE group_id in( "+ s2 + ")";
		String s4 = "SELECT * FROM " + tableName + " WHERE id in(" + s3+ ") ORDER BY sorter";
		List<?> list = dao.query(s4);
		return list != null ? (List<BaseNav>) list : null;
	  }

	/**
	 * 查看导航编号是否存在
	 * @param no
	 * @param id
	 * 
	 */
	public boolean existsNo(String no,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE no=? and id<>?";
		return dao.number(preSql, new String[]{no,id})>0;
	}


	/**
	 * 查看导航Label是否存在
	 * @param label
	 * @param id
	 * 
	 */
	public boolean existsLabel(String label,String id) {
		String preSql = "SELECT count(*) FROM " + tableName + " WHERE label=? and id<>?";
		return dao.number(preSql, new String[]{label,id})>0;
	}

}
