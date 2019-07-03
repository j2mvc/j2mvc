package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.RoleService;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.DataSourceName;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;
import com.j2mvc.util.Utils;

/**
 * 用户基类
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_USER)
@Table(EntityConstants.TABLE_USER)
@PrimaryKey(autoIncrement = false)
public class BaseUser extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */ 
	@JSONField("id")
	@Column(name = "id",length = 64,notnull = true)
	protected String id;			

	/** 角色 */
	@JSONField("roles")
	protected List<Role> roles;

	public BaseUser() {
		super();
		initServices();
	}

	public BaseUser(String id) {
		super();
		this.id = id;
		initServices();
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	String dataSourceName = null;
	private void initServices(){
		DataSourceName dsn = getClass().getAnnotation(DataSourceName.class);	
		if(dsn!=null){
			dataSourceName = dsn.value();
		}
		if(dataSourceName !=null && !dataSourceName.equals("")){
			roleService = new RoleService(dataSourceName);
		}else{
			roleService = new RoleService();
		}
	}

	private RoleService roleService;
	public List<Role> getRoles() {
		if(roles == null){
			String sql = "SELECT * FROM "+EntityConstants.TABLE_ROLE+" WHERE id in  "
			+ "(SELECT role_id FROM "+EntityConstants.TABLE_USER_ROLE+" WHERE user_id=?)";				
			roles =  roleService.query(sql, new String[]{id});
		}
		return roles;
	}
}
