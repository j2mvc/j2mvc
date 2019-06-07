package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.framework.entity.BaseEntity;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.framework.mapping.Column;
import com.j2mvc.framework.mapping.JSONField;
import com.j2mvc.framework.mapping.JSONObjectStr;
import com.j2mvc.framework.mapping.PrimaryKey;
import com.j2mvc.framework.mapping.Table;

/**
 * 菜单组
 * 
 * 2014-4-12 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_MENU_GROUP)
@Table(EntityConstants.TABLE_MENU_GROUP)
@PrimaryKey(autoIncrement = false)
public class MenuGroup extends BaseEntity{
	private static final long serialVersionUID = 152123223216813172L;

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
	
    /** 菜单显示位置：根据不同系统设置不同值 本系统为: 0:用户左侧菜单*/
	@JSONField("position")
	@Column(name = "position",length = 2,notnull = true)
	private Integer position = 0;

	/**排序号*/
	@JSONField("sorter")
	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 99;

	/** 自定义定段 */
	@JSONField("custom")
	@Column(name = "custom",length = 225)
	private String custom;
	
	/** 菜单*/
	@JSONField("items")
	private List<Menu> menus;
	
	private boolean showTitle = false;

	public String getId() {
		return id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getSorter() {
		return sorter;
	}

	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
		if(custom!=null && !custom.equals(""))
		try {
			JSONObject json = JSONObject.parseObject(custom);
			String str = json.getString("showTitle");	
			if(str.equalsIgnoreCase("true") )
				showTitle = true;
		} catch (JSONException e) {
		}		
	}

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
	
	
}
