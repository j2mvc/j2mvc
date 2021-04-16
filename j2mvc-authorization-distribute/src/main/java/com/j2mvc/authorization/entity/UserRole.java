package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.util.Utils;

/**
 * 用户角色映射
 * 
 * 2021-4-16 创建@杨朔
 */
@Table(EntityConstants.TABLE_USER_ROLE)
@PrimaryKey(autoIncrement = false)
public class UserRole extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 64,notnull = true)
	private String id = Utils.createId();			

	/**
	 * 权限,级联auth删除
	 */
	@Column(name = "user_id",length = 32,notnull = true)
	private String userId;			
	

	/**
	 * 角色,级联role删除
	 */
	@Column(name = "role_id",length = 32,notnull = true)
	private String roleId;


	public UserRole() {
		super();
	}


	public UserRole(String id, String userId, String roleId) {
		super();
		this.id = id;
		this.userId = userId;
		this.roleId = roleId;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}



}
