package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.Foreign;
import com.j2mvc.util.mapping.JSONField;
import com.j2mvc.util.mapping.JSONObjectStr;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Table;

/**
 * 角色菜单映射表
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_ROLE_MENU)
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
	@Foreign
	@JSONField("menu")
	@Column(name = "menu_id",length = 32,notnull = true)
	private Menu menu;			
	

	/**
	 * 角色,级联role删除
	 */
	@Foreign
	@JSONField("role")
	@Column(name = "role_id",length = 32,notnull = true)
	private Role role;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}



	public Menu getMenu() {
		return menu;
	}


	public void setMenu(Menu menu) {
		this.menu = menu;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}

}
