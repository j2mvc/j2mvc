package com.j2mvc.searcher;

import java.util.List;

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
	/** 主图 */ 
	private String image;
	/** 视频 */ 
	private String video;
	/** 说明 */ 
	private String descri;
	/** 任意格式的值 */ 
	private String extra;
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
	private List<String> images;
	/** 文件组 */
	private List<String> files;
	
	private Integer browser;
	private String catNames;
	private String catAddress;
	
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

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image!=null?image:"";
	}
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri!=null?descri:"";
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra!=null?extra:"";
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
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
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
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video!=null?video:"";
	}
	public Integer getBrowser() {
		return browser;
	}
	public void setBrowser(Integer browser) {
		this.browser = browser;
	}
	public String getCatNames() {
		return catNames;
	}
	public void setCatNames(String catNames) {
		this.catNames = catNames;
	}
	public String getCatAddress() {
		return catAddress;
	}
	public void setCatAddress(String catAddress) {
		this.catAddress = catAddress;
	}
	
}
