package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.RoleService;
import com.j2mvc.framework.mapping.DataSourceName;

/**
 * 用户基类
 * 
 * 2021-4-16 创建@杨朔
 */
public abstract class BaseUser extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 角色 */
	protected List<Role> roles;

	public BaseUser() {
		super();
		initServices();
	}

	public BaseUser(String id) {
		super();
		initServices();
	}

	public abstract String getId();


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
			roles =  roleService.query(sql, new String[]{getId()});
		}
		return roles;
	}
}
