package com.j2mvc.authorization.entity;

import java.util.List;

import com.j2mvc.authorization.global.EntityConstants;
import com.j2mvc.authorization.service.MenuGroupService;
import com.j2mvc.framework.entity.BaseEntity;
import com.j2mvc.util.mapping.Column;
import com.j2mvc.util.mapping.DataSourceName;
import com.j2mvc.util.mapping.JSONField;
import com.j2mvc.util.mapping.JSONObjectStr;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Table;

/***
 * 导航
 * 
 * 2014-5-7 创建@杨朔
 */
@JSONObjectStr(EntityConstants.JSON_NAV)
@Table(EntityConstants.TABLE_NAV)
@PrimaryKey(autoIncrement = false)
public class BaseNav extends BaseEntity{

	private static final long serialVersionUID = -731804884323686259L;

	/** 主键 */
	@JSONField("id")
	@Column(name = "id",length = 32,notnull = true)
	private String id;

	/** 编号 */
	@JSONField("no")
	@Column(name = "no",length = 32,notnull = true)
	private String no;	
	
	/*** 显示值 */	
	@JSONField("label")
	@Column(name = "label",length = 32,notnull = true)
	private String label;	

	/*** 英文显示值 */	
	@JSONField("enlabel")
	@Column(name = "enlabel",length = 32,notnull = true)
	private String enlabel;	
	
	/** 根据不同系统设置不同值，本系统为:0:顶部导航*/
	@Column(name = "position",length = 2,notnull = true)
	private Integer position = 0;			

	/**链接地址*/
	@JSONField("link")
	@Column(name = "link",length = 255,notnull = true)
	private String link;			

	/**排序*/
	@Column(name = "sorter",length = 11,notnull = true)
	private Integer sorter = 0;

	/**状态:1:启用，0:未启用*/
	@Column(name = "status",length = 11,notnull = true)
	private String status = "1";
	
	private List<MenuGroup> menuGroups;
	MenuGroupService menuGroupService;
	
	public BaseNav() {
		super();
		initServices();
	}

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	private void initServices(){
		String dataSourceName = null;
		DataSourceName dsn = getClass().getAnnotation(DataSourceName.class);	
		if(dsn!=null){
			dataSourceName = dsn.value();
		}
		if(dataSourceName !=null && !dataSourceName.equals("")){
			menuGroupService = new MenuGroupService(dataSourceName);
		}else{
			menuGroupService = new MenuGroupService();
		}
	}
	public List<MenuGroup> getMenuGroups() {
		if(menuGroups == null){
			menuGroups = menuGroupService.queryByNav(id);
		}
		return menuGroups;
	}

	public void setMenuGroups(List<MenuGroup> menuGroups) {
		this.menuGroups = menuGroups;
	}

	public String getEnlabel() {
		return enlabel;
	}

	public void setEnlabel(String enlabel) {
		this.enlabel = enlabel;
	}		
}
