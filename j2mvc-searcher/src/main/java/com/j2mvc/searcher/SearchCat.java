package com.j2mvc.searcher;

import java.util.List;

/**
 * 搜索信息<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔
 * 2015年1月21日
 */
public class SearchCat {

	/** 主键 */
	private String id;
	/** 分类名称 */
	private String name;
	/** 分类名称组 */
	private String names; 
	/** 分类别名 */
	private String alias;
	/** 分类图标 */
	private String image;
	/** 分类URL */
	private String url;
	/** 子分类 */
	private List<SearchCat> children;
	/** 子分类总数 */
	private Integer childrenTotal = 0;
	
	
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
		this.name = name !=null?name:"";
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias !=null?alias:"";
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image !=null?image:"";
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url !=null?url:"";
	}
	public List<SearchCat> getChildren() {
		return children;
	}
	public void setChildren(List<SearchCat> children) {
		this.children = children;
	}
	public Integer getChildrenTotal() {
		return childrenTotal;
	}
	public void setChildrenTotal(Integer childrenTotal) {
		this.childrenTotal = childrenTotal !=null?childrenTotal:0;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names !=null?names:"";
	}

}
