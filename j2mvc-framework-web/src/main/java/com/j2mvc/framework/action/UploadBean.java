package com.j2mvc.framework.action;

import java.util.List;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;

public class UploadBean {

	private Error error;
	private List<FileInfo> fileInfos;
	private FileInfo fileInfo;
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public List<FileInfo> getFileInfos() {
		return fileInfos;
	}
	public void setFileInfos(List<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}
	public FileInfo getFileInfo() {
		return fileInfo;
	}
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
}
