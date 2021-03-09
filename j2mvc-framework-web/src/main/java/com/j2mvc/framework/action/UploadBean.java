package com.j2mvc.framework.action;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;

public class UploadBean {

	private List<FileInfo> fileList;
	private Map<String,String> textData;
	private List<Error> errors;
	
	public List<Error> getErrors() {
		return errors;
	}
	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}
	public List<FileInfo> getFileList() {
		return fileList;
	}
	public void setFileList(List<FileInfo> fileList) {
		this.fileList = fileList;
	}
	public Map<String, String> getTextData() {
		return textData;
	}
	public void setTextData(Map<String, String> textData) {
		this.textData = textData;
	}
}
