package com.j2mvc.authorization.entity;

import java.util.Date;

import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.framework.mapping.Column;
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
@JSONObjectStr(EntityConstants.JSON_USER_TOKEN)
@Table(EntityConstants.TABLE_USER_TOKEN)
@PrimaryKey(autoIncrement = false)
public class UserToken extends BaseEntity{
	private static final long serialVersionUID = 1521232238886871802L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id = Utils.createId();			

	/**
	 * 用户ID
	 */
	@JSONField("user_id")
	@Column(name = "user_id",length = 32,notnull = true)
	private String userId;			
	
	/**
	 * 客户端ID
	 */
	@JSONField("client_id")
	@Column(name = "client_id",length = 255,notnull = true)
	private String clientId;
	/**
	 * 客户端类型
	 */
	@JSONField("client_type")
	@Column(name = "client_type",length = 255,notnull = true)
	private String clientType;

	/**
	 * 会话令牌
	 */
	@JSONField("token")
	@Column(name = "token",length = 255,notnull = true)
	private String token;

	/** 创建时间 */
	@JSONField("create_time")
	@Column(name = "create_time")
	private Long createTime = new Date().getTime();		

	
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


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

}
