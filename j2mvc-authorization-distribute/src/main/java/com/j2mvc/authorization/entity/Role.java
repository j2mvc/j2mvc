package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.AuthService;
import com.j2mvc.authorization.service.MenuService;
import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.util.json.JSONField;

/**
 * 角色
 * 
 * 2021-4-16 创建@杨朔
 */
@Table(EntityConstants.TABLE_ROLE)
@PrimaryKey(autoIncrement = false)
public class Role extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id;			

	/** 名称 */
	@JSONField("name")
	@Column(name = "name",length = 64,notnull = true)
	private String name	;		

	/** 描述 */
	@JSONField("sorter")
	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 99;

	/** 默认角色：0:否,1:是 */
	@JSONField("default_set")
	@Column(name = "default_set",length = 1,notnull = true)
	private Integer defaultSet = 0;

	/** 权限列表*/
	@JSONField("auths")
	private List<Auth> auths;

	/** 菜单列表*/
	@JSONField("menus")
	private List<Menu> menus;

	public Role() {
		super();
	}


	public Role(String id) {
		super();
		this.id = id;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getSorter() {
		return sorter;
	}


	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}

	private MenuService menuService = new MenuService();
	
	/**
	 * 菜单列表
	 * 
	 */
	public List<Menu> getMenus(){
		String sql = "SELECT m.* FROM "
			+ EntityConstants.TABLE_MENU+" as m,"+EntityConstants.TABLE_ROLE_MENU+" as rm "
					+ "WHERE m.id=rm.menu_id and rm.role_id=? order by rm.id";		
		return menus!=null?menus:menuService.query(sql, new String[]{id});
	}
	
	/**
	 * 以下为角色权限
	 */
	private AuthService authService = new AuthService();
	public List<Auth> getAuths() {
		String sql = "SELECT a.* FROM "
				+ EntityConstants.TABLE_AUTH+" as a,"+EntityConstants.TABLE_ROLE_AUTH+" as ra "
						+ "WHERE a.id=ra.auth_id and ra.role_id=? order by ra.id";		
		return auths!=null?auths:authService.query(sql, new String[]{id});
	}



	public void setAuths(List<Auth> auths) {
		this.auths = auths;
	}


	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}



	public Integer getDefaultSet() {
		return defaultSet;
	}


	public void setDefaultSet(Integer defaultSet) {
		this.defaultSet = defaultSet;
	}
	
}
