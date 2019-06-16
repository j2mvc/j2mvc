package com.j2mvc.searcher;

/**
 * 搜索信息<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔
 * 2015年1月21日
 */
public class SearchItem {

	/** 主键 */
	private String id;
	/** 标题 */ 
	private String title;
	/** 外部链接 */ 
	private String href;
	/** 副标题 */
	private String subtitle;
	/** 关键词 */
	private String keywords;
	/** 来源 */
	private String source;
	/** 内容 */
	private String content;
	/** 分类ID */
	private String catId;
	/** 创建时间 */
	private Long createTime;
	/** 更新时间 */
	private Long updateTime;
	/** 索引时间 */
	private String indexedTime;
	/** 图片组 */
	private String[] images;
	/** 文件组 */
	private String[] files;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id!=null?id:"";
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title!=null?title:"";
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle!=null?subtitle:"";
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content!=null?content:"";
	}
	public String getCatId() {
		return catId;
	}
	public void setCatId(String catId) {
		this.catId = catId!=null?catId:"";
	}

	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public String getIndexedTime() {
		return indexedTime;
	}
	public void setIndexedTime(String indexedTime) {
		this.indexedTime = indexedTime!=null?indexedTime:"";
	}
	public String[] getImages() {
		return images;
	}
	public void setImages(String[] images) {
		this.images = images;
	}
	public String[] getFiles() {
		return files;
	}
	public void setFiles(String[] files) {
		this.files = files;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords!=null?keywords:"";
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source!=null?source:"";
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href!=null?href:"";
	}
	
}
