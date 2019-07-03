package com.j2mvc.authorization.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.AuthService;
import com.j2mvc.authorization.service.MenuService;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 角色
 * 
 * 2014-3-27 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_ROLE)
@Table(EntityConstants.TABLE_ROLE)
@PrimaryKey(autoIncrement = false)
public class Role extends BaseEntity{
	private static final long serialVersionUID = 1521232234756871802L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id;			

	/** 域名名称 */
	@JSONField("name")
	@Column(name = "name",length = 64,notnull = true)
	private String name	;		

	/** 英文名称 */
	@JSONField("enname")
	@Column(name = "enname",length = 64,notnull = true)
	private String enname;		

	/** 描述 */
	@JSONField("sorter")
	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 99;

	/** 默认角色：0:否,1:是 */
	@JSONField("default_set")
	@Column(name = "default_set",length = 1,notnull = true)
	private Integer defaultSet = 0;

	/** 权限列表*/
	@JSONField("auths")
	private List<Auth> auths;

	/** 菜单列表*/
	@JSONField("menus")
	private List<Menu> menus;

	/** 权限分组列表*/
	@JSONField("group_auths")
	private Map<Integer,List<Auth>> groupAuths = new HashMap<Integer, List<Auth>>();

	public Role() {
		super();
	}


	public Role(String id) {
		super();
		this.id = id;
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


	public Integer getSorter() {
		return sorter;
	}


	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}

	private MenuService menuService = new MenuService();
	
	/**
	 * 菜单列表
	 * 
	 */
	public List<Menu> getMenus(){
		String sql = "SELECT m.* FROM "
			+ EntityConstants.TABLE_MENU+" as m,"+EntityConstants.TABLE_ROLE_MENU+" as rm "
					+ "WHERE m.id=rm.menu_id and rm.role_id=? order by rm.id";		
		return menus!=null?menus:menuService.query(sql, new String[]{id});
	}
	
	/**
	 * 以下为角色权限
	 */
	private AuthService authService = new AuthService();
	public List<Auth> getAuths() {
		String sql = "SELECT a.* FROM "
				+ EntityConstants.TABLE_AUTH+" as a,"+EntityConstants.TABLE_ROLE_AUTH+" as ra "
						+ "WHERE a.id=ra.auth_id and ra.role_id=? order by ra.id";		
		return auths!=null?auths:authService.query(sql, new String[]{id});
	}

	public Map<Integer,List<Auth>> getGroupAuths() {
		String sql = "SELECT a.* FROM "
				+EntityConstants.TABLE_AUTH+" as a,"+EntityConstants.TABLE_ROLE_AUTH+" as ra "
				+"WHERE a.id=ra.auth_id and ra.role_id=? and a.auth_type=? order by ra.id";	
		List<Auth> pathAuths = new ArrayList<Auth>();			
		pathAuths = auths!=null?auths:authService.query(sql, new Object[]{id,AuthConstants.AUTH_TYPE_PATH});
		List<Auth> uriAuths = new ArrayList<Auth>();			
		uriAuths = auths!=null?auths:authService.query(sql, new Object[]{id,AuthConstants.AUTH_TYPE_URI});
		List<Auth> methodAuths = new ArrayList<Auth>();			
		methodAuths = auths!=null?auths:authService.query(sql, new Object[]{id,AuthConstants.AUTH_TYPE_URL});
		List<Auth> menuAuths = new ArrayList<Auth>();			
		menuAuths = auths!=null?auths:authService.query(sql, new Object[]{id,AuthConstants.AUTH_TYPE_MENU});
		groupAuths.put(AuthConstants.AUTH_TYPE_PATH, pathAuths);
		groupAuths.put(AuthConstants.AUTH_TYPE_URI, uriAuths);
		groupAuths.put(AuthConstants.AUTH_TYPE_URL, methodAuths);
		groupAuths.put(AuthConstants.AUTH_TYPE_MENU, menuAuths);
		return groupAuths;
	}


	public void setAuths(List<Auth> auths) {
		this.auths = auths;
	}


	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}


	public String getEnname() {
		return enname;
	}


	public void setEnname(String enname) {
		this.enname = enname;
	}


	public Integer getDefaultSet() {
		return defaultSet;
	}


	public void setDefaultSet(Integer defaultSet) {
		this.defaultSet = defaultSet;
	}
	
}
