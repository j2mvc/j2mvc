package com.j2mvc.framework.upload.entity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import com.j2mvc.framework.upload.Lister;
import com.j2mvc.framework.upload.Util;
import com.j2mvc.util.json.NotJSONField;

/**
 * 上传类
 * @author 杨朔
 *	2014年1月13日
 */
public class FileInfo implements Serializable{
	private static final long serialVersionUID = -8300150424507048521L;
	/** 保存根路径 */
	@NotJSONField
    private String savePath;
	/** 访问URL */
    private String url;
    /** 文件图标 */
    private String icon;
	/** 文件名 */
    private String filename;
	/** 文件大小 */
    private long totalSize = 0;
	/** 已读取字节 */
    private long bytesRead = 0;
	/** 开始时间 */
    private long startTime = System.currentTimeMillis();
	/** 上传状态，参考类UploadLister状态定义，默认为等待 */
    private int status = Lister.STATUS_WAIT;
	/** 上传序号，通常在多文件上传使用，单文件为0 */
    private int index = 0;
	// 文件流
    private InputStream inputStream;
	/** 
	 * 以下参数计算获取 
	 * 完成的百分比 
	 */
    private float percent = 0;
	/** 耗时 */
    private long elapsedTime = 0;
	/** 估计剩余时间 */
    private long restTime = 0;
	/** 速度 */
    private long spead = 0;

    private String id = Util.getRandomUUID(String.valueOf(new Date().getTime())).toString();
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getBytesRead() {
		return bytesRead;
	}
	public void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setPercent(float percent) {
		this.percent = percent;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public void setRestTime(long restTime) {
		this.restTime = restTime;
	}
	public void setSpead(long spead) {
		this.spead = spead;
	}
	public float getPercent() {
		return percent;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public long getRestTime() {
		return restTime;
	}
	public long getSpead() {
		return spead;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
}
