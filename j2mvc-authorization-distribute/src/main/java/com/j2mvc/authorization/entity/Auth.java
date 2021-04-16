package com.j2mvc.authorization.entity;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 权限
 * 
 * 2021-4-16 创建@杨朔
 */
@Table(EntityConstants.TABLE_AUTH) 
@PrimaryKey(autoIncrement = false) 
public class Auth extends BaseEntity{
	private static final long serialVersionUID = 1521232234756813172L;
	

	@Column(name = "project_id",length = 64,notnull = true)
	private String projectId;			
	
	/** 主键 */
	@Column(name = "id",length = 64,notnull = true)
	private String id;			

	/** 权限标签，相当于分类 */
	@Column(name = "tag",length = 255,notnull = true)
	private String tag = "";

	/** 名称 */
	@Column(name = "name",length = 64,notnull = true)
	private String name	;

	/** 无权限处理代码，自定义代码。 */
	@Column(name = "auth_none",length = 255,notnull = true)
	private String authNone = "";

	/**
	 * 权限值
	 * 	authType为0:直接输入
	 * 	authType为1:直接输入
	 * 	authType为2:直接输入
	 */
	@Column(name = "auth_value",length = 255,notnull = true)
	private String value;

	/**
	 * 权限类型
	 * 	0:权限类型-路径
	 * 	1:权限类型-URI
	 * 	2:权限类型-URL
	 */
	@Column(name = "auth_type",length = 2,notnull = true)
	private int type = AuthConstants.AUTH_TYPE_URI;

	/**
	 * 权限开启状态,默认为1,需要权限控制
	 * 1:权限控制开启
	 * 0:权限控制关闭
	 */
	@Column(name = "status",length = 1,notnull = true)
	private String status = AuthConstants.AUTH_STATUS_Y;
	
	/**
	 * 额外限制，其它额外限制,默认为0,无限制
	 */
	@Column(name = "extra_limit",length = 2,notnull = true)
	private int limit = AuthConstants.AUTH_EXTRA_LIMIT_NONE;

	/**
	 * 默认为允许编辑，不允许编辑的权限为系统初始化时创建。
	 */
	@Column(name = "enable_edit",notnull = true)
	private boolean enableEdit = true;

	
	
	public Auth() {
		super();
	}

	public Auth(String id,String projectId, String name, String value, int type) {
		super();
		this.id = id;
		this.projectId = projectId;
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public Auth(String id,String projectId, String name, String value, int type, String status,
			int limit, boolean enableEdit) {
		super();
		this.id = id;
		this.projectId = projectId;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
