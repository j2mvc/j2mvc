package com.j2mvc.authorization.entity;

import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/***
 * 导航与菜单组关联
 * 
 * 2015-4-2 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_NAV_MENU_GROUP)
@Table(EntityConstants.TABLE_NAV_MENU_GROUP)
@PrimaryKey(autoIncrement = false)
public class NavMenuGroup extends BaseEntity{

	private static final long serialVersionUID = -731804880020126259L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id;
	
	/*** 导航ID */	
	@JSONField("nav_id")
	@Column(name = "nav_id",length = 32,notnull = true)
	private String navId;	

	/*** 菜单组ID */	
	@JSONField("group_id")
	@Column(name = "group_id",length = 32,notnull = true)
	private String groupId;


	public NavMenuGroup(String id, String navId, String groupId) {
		super();
		this.id = id;
		this.navId = navId;
		this.groupId = groupId;
	}

	public NavMenuGroup() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNavId() {
		return navId;
	}

	public void setNavId(String navId) {
		this.navId = navId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}	
}
