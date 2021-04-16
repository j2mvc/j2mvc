package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 角色菜单映射表
 * 
 * 2021-4-16 创建@杨朔
 */
@Table(EntityConstants.TABLE_ROLE_MENU)
@PrimaryKey(autoIncrement = false)
public class RoleMenu extends BaseEntity{
	
	private static final long serialVersionUID = -3939376218577702258L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 64,notnull = true)
	private String id;			

	/**
	 * 菜单,级联menu删除
	 */
	@Column(name = "menu_id",length = 32,notnull = true)
	private String menuId;			
	

	/**
	 * 角色,级联role删除
	 */
	@Column(name = "role_id",length = 32,notnull = true)
	private String roleId;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getMenuId() {
		return menuId;
	}


	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}


	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}



}
