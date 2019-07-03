package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.MenuService;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.Foreign;
import com.j2mvc.util.json.JSONField;
import com.j2mvc.util.json.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 菜单项
 * 
 * 2014-4-12 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_MENU)
@Table(EntityConstants.TABLE_MENU)
@PrimaryKey(autoIncrement = false)
public class Menu extends BaseEntity{
	private static final long serialVersionUID = 1521232234756813172L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id;			

	/** 编号 */
	@JSONField("no")
	@Column(name = "no",length = 32,notnull = true)
	private String no;	
	
	/** 名称 */
	@JSONField("name")
	@Column(name = "name",length = 64,notnull = true)
	private String name	;		

	/** 英文名称 */
	@JSONField("enname")
	@Column(name = "enname",length = 64,notnull = true)
	private String enname;		
	
    /** 访问地址*/
	@JSONField("url")
	@Column(name = "url",length = 255,notnull = true)
	private String url = "";

    /** 图标*/
	@JSONField("icon")
	@Column(name = "icon",length = 255,notnull = true)
	private String icon;

    /** 样式*/
	@JSONField("styleClass")
	@Column(name = "style_class",length = 255,notnull = true)
	private String styleClass;
	
    /** 上级菜单*/
	@Foreign
	@JSONField("parent")
	@Column(name = "pid",length = 32,notnull = true)
	private Menu parent;

	/**排序号*/
	@Foreign
	@JSONField("menuGroup")
	@Column(name = "gid",length = 32,notnull = true)
	private MenuGroup menuGroup;

	/**排序号*/
	@JSONField("sorter")
	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 99;

	/**下级菜单*/
	@JSONField("items")
	private List<Menu> children;
	
	/** 自定义定段 */
	@JSONField("custom")
	@Column(name = "custom",length = 255,notnull = true)
	private String custom;

	@JSONField("exists_children")
	private boolean existsChildren;
	
	MenuService service = new MenuService();

	private String description;
	
	public Menu(String id) {
		super();
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Menu() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		setExistsChildren(service.totalChildren(id)>0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MenuGroup getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(MenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}

	public Integer getSorter() {
		return sorter;
	}

	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean isExistsChildren() {
		return existsChildren;
	}

	public void setExistsChildren(boolean existsChildren) {
		this.existsChildren = existsChildren;
	}

	public String getDescription() {
		if(description == null || "".equals(description)){
			if(no!=null && !no.trim().equals("")){
				description ="编号："+no;
			}
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
