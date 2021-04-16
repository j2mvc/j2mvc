package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 角色权限映射表
 * 
 * 2021-4-16 创建@杨朔
 */
@Table(EntityConstants.TABLE_ROLE_AUTH)
@PrimaryKey(autoIncrement = false)
public class RoleAuth extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */
	@Column(name = "id",length = 64,notnull = true)
	private String id;			

	/**
	 * 权限,级联auth删除
	 */
	@Column(name = "auth_id",length = 32,notnull = true)
	private String authId;			
	

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


	public String getAuthId() {
		return authId;
	}


	public void setAuthId(String authId) {
		this.authId = authId;
	}


	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}
