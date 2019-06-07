package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.framework.mapping.JSONField;
import com.j2mvc.framework.mapping.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.util.Utils;

/**
 * 用户角色映射
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_USER_ROLE)
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
	@Foreign
	@JSONField("user")
	@Column(name = "user_id",length = 32,notnull = true)
	private BaseUser user;			
	

	/**
	 * 角色,级联role删除
	 */
	@Foreign
	@JSONField("role")
	@Column(name = "role_id",length = 32,notnull = true)
	private Role role;


	public UserRole() {
		super();
	}


	public UserRole(String id, BaseUser user, Role role) {
		super();
		this.id = id;
		this.user = user;
		this.role = role;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public BaseUser getUser() {
		return user;
	}


	public void setUser(BaseUser user) {
		this.user = user;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}

}
