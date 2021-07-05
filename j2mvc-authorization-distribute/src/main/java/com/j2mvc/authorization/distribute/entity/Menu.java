package com.j2mvc.authorization.distribute.entity;

import java.util.List;

import com.j2mvc.authorization.distribute.global.EntityConstants;
import com.j2mvc.authorization.distribute.service.MenuService;
import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 菜单项
 * 
 * 2014-4-12 创建@杨朔
 */
@Table(EntityConstants.TABLE_MENU)
@PrimaryKey(autoIncrement = false)
public class Menu extends BaseEntity{
	private static final long serialVersionUID = 1521232234756813172L;
	
	@Column(name = "id")
	private String id;

	@Column(name = "project_id",length = 64,notnull = true)
	private String projectId;		
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "icon")
	private String icon = "";
	
	@Column(name = "path")
	private String path = "";

	@Column(name = "extra")
	private Boolean extra;

	@Column(name = "hide")
	private Boolean hide;
	
	/**
	 * 自定义字段 
	 */
	@Column(name = "custom")
	private String custom  = "";

    /** 样式*/
	@Column(name = "style_class",length = 255,notnull = true)
	private String styleClass  = "";
	
    /** 上级菜单*/
	@Column(name = "parent_id",length = 32,notnull = true)
	private String parentId;

	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 99;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Boolean getExtra() {
		return extra;
	}

	public void setExtra(Boolean extra) {
		this.extra = extra;
	}

	public Integer getSorter() {
		return sorter;
	}

	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Boolean getHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}


	/**下级菜单*/
	private List<Menu> children;

	public List<Menu> getChildren() {
		if(children == null) {
			MenuService service = new MenuService();
			children = service.queryChildren(id);
		}
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}
}
