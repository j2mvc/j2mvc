package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.authorization.global.EntityConstants;
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
	private String icon;
	
	@Column(name = "path")
	private String path;

	@Column(name = "extra")
	private String extra;
	/**
	 * 自定义字段 
	 */
	@Column(name = "custom")
	private Boolean custom;

    /** 样式*/
	@Column(name = "style_class",length = 255,notnull = true)
	private String styleClass;
	
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

	public Boolean getCustom() {
		return custom;
	}

	public void setCustom(Boolean custom) {
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

	/**下级菜单*/
	private List<Menu> children;

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}
}
