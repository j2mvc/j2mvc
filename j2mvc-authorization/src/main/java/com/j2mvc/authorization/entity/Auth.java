package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;

/**
 * 权限
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_AUTH)
@Table(EntityConstants.TABLE_AUTH) 
@PrimaryKey(autoIncrement = false) 
public class Auth extends BaseEntity{
	private static final long serialVersionUID = 1521232234756813172L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 64,notnull = true)
	private String id;			

	/** 权限标签，相当于分类 */
	@JSONField("tag")
	@Column(name = "tag",length = 255,notnull = true)
	private String tag = "";

	/** 名称 */
	@JSONField("name")
	@Column(name = "name",length = 64,notnull = true)
	private String name	;

	/** 无权限处理代码，自定义代码。 */
	@JSONField("auth_none")
	@Column(name = "auth_none",length = 255,notnull = true)
	private String authNone = "";

	/**
	 * 权限值
	 * 	authType为0:直接输入
	 * 	authType为1:直接输入
	 * 	authType为2:直接输入
	 * 	authType为3:读取菜单获取ID
	 */
	@JSONField("value")
	@Column(name = "auth_value",length = 255,notnull = true)
	private String value;

	/**
	 * 权限类型
	 * 	0:权限类型-路径
	 * 	1:权限类型-URI
	 * 	2:权限类型-URL
	 */
	@JSONField("type")
	@Column(name = "auth_type",length = 2,notnull = true)
	private int type = AuthConstants.AUTH_TYPE_URI;

	/**
	 * 权限开启状态,默认为1,需要权限控制
	 * 1:权限控制开启
	 * 0:权限控制关闭
	 */
	@JSONField("status")
	@Column(name = "status",length = 1,notnull = true)
	private String status = AuthConstants.AUTH_STATUS_Y;
	
	/**
	 * 额外限制，其它额外限制,默认为0,无限制
	 */
	@JSONField("limit")
	@Column(name = "extra_limit",length = 2,notnull = true)
	private int limit = AuthConstants.AUTH_EXTRA_LIMIT_NONE;

	/**
	 * 默认为允许编辑，不允许编辑的权限为系统初始化时创建。
	 */
	@JSONField("enable_edit")
	@Column(name = "enable_edit",notnull = true)
	private boolean enableEdit = true;

	
	
	public Auth() {
		super();
	}

	public Auth(String id, String name, String value, int type) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public Auth(String id, String name, String value, int type, String status,
			int limit, boolean enableEdit) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = type;
		this.status = status;
		this.limit = limit;
		this.enableEdit = enableEdit;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isEnableEdit() {
		return enableEdit;
	}

	public void setEnableEdit(boolean enableEdit) {
		this.enableEdit = enableEdit;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAuthNone() {
		return authNone;
	}

	public void setAuthNone(String authNone) {
		this.authNone = authNone;
	}
}
