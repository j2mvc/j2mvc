package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.framework.mapping.JSONField;
import com.j2mvc.framework.mapping.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 角色权限映射表
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_ROLE_AUTH)
@Table(EntityConstants.TABLE_ROLE_AUTH)
@PrimaryKey(autoIncrement = false)
public class RoleAuth extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 64,notnull = true)
	private String id;			

	/**
	 * 权限,级联auth删除
	 */
	@Foreign
	@JSONField("auth")
	@Column(name = "auth_id",length = 32,notnull = true)
	private Auth auth;			
	

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


	public Auth getAuth() {
		return auth;
	}


	public void setAuth(Auth auth) {
		this.auth = auth;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}

}
