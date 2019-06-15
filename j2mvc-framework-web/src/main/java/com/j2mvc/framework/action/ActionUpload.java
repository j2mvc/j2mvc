package com.j2mvc.framework.action;

public class ActionUpload {
	private String saveUrl;
	private String savePath;
	private String dirname;
	private String ext;
	private long maxSize;
	private String filename;
	private boolean keepOriginName;

	public String getSaveUrl() {
		return saveUrl;
	}
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getDirname() {
		return dirname;
	}
	public void setDirname(String dirname) {
		this.dirname = dirname;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public long getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public boolean isKeepOriginName() {
		return keepOriginName;
	}
	public void setKeepOriginName(boolean keepOriginName) {
		this.keepOriginName = keepOriginName;
	}
}
