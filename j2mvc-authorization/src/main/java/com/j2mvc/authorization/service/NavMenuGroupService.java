package com.j2mvc.authorization.service;

import java.util.List;

import com.j2mvc.authorization.config.AuthConfig;
import com.j2mvc.authorization.entity.NavMenuGroup;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.dao.DaoSupport;
import com.j2mvc.util.Utils;

/**
 * 
 * 导航菜单组关联
 * 
 * 2014-5-7 创建@杨朔
 */
public class NavMenuGroupService{

	DaoSupport dao;
	String tableName = EntityConstants.TABLE_NAV_MENU_GROUP;

	public NavMenuGroupService(){
		 if(!AuthConfig.dataSourceName.equals(""))
			 dao = new DaoSupport(NavMenuGroup.class,AuthConfig.dataSourceName);
		 else {
			 dao = new DaoSupport(NavMenuGroup.class);
		}
	}
	public NavMenuGroupService(String dataSourceName){
		 dao = new DaoSupport(NavMenuGroup.class,dataSourceName);
	}
	/**
	 * 插入
	 * @param nmg
	 * 
	 */
	private NavMenuGroup insert(NavMenuGroup nmg) {		
		return (NavMenuGroup) dao.insert(nmg);
	}
	/**
	 * 更新
	 * @param nmg
	 * 
	 */
	private NavMenuGroup update(NavMenuGroup nmg) {
		return (NavMenuGroup)dao.update(nmg);
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
	 * @param nmg
	 * 
	 */
	public NavMenuGroup save(NavMenuGroup nmg){
		if(nmg == null)
			return null;
		if(get(nmg.getId())!=null){
			// 更新
			nmg = update(nmg);
		}else{
			// 新增
			nmg = insert(nmg);
		}
		return nmg;
	}


	/**
	 * 保存
	 * @param navId 导航ID
	 * @param groupIdArr 组ID
	 * 
	 */
	public boolean save(String navId,String[] groupIdArr) {
		if(navId == null || "".equals(navId) || groupIdArr == null )
			return false;
		boolean bool = true;
		for(int i=0;i<groupIdArr.length;i++){
			NavMenuGroup nmg = get(navId, groupIdArr[i]);
			if(nmg == null){
				nmg = new NavMenuGroup(Utils.createId(),navId, groupIdArr[i]);
				if(insert(nmg)==null)
					bool = false;
			}
		}
		return bool;
	}

	/**
	 * 清除导航所有关联
	 * @param navId
	 * 
	 */
	public Integer clear(String navId) {
		String sql = "DELETE FROM " + tableName + " WHERE nav_id=?";
		return dao.execute(sql, new String[]{navId});
	}

	/**
	 * 获取
	 * @param id
	 * 
	 */
	public NavMenuGroup get(String id) {
		Object object =  dao.get(id);
		return object!=null?(NavMenuGroup)object:null;
	}

	/**
	 * 获取
	 * @param navId 导航ID
	 * @param nmgId 菜单组ID
	 * 
	 */
	public NavMenuGroup get(String navId,String nmgId) {
		String sql = "SELECT  * FROM "+tableName + " WHERE nav_id=? and group_id=?";
		Object object =  dao.queryForObject(sql, new String[]{navId,nmgId});
		return object!=null?(NavMenuGroup)object:null;
	}

	/**
	 * 获取
	 * @param preSql 预编译查询语句
	 * @param params 参数数组
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<NavMenuGroup> query(String preSql,Object [] params) {
		List<?> list =  dao.query(preSql, params);
		return list!=null?(List<NavMenuGroup>) list:null;
	}
}
