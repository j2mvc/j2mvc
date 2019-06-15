package com.j2mvc.framework.upload;

import java.util.List;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;

/**
 * 上传文件回调
 * @author 杨朔
 *	2014年1月14日
 */
public abstract class UploadHandler {

	/**
	 * 上传错误
	 * @param error
	 */
	public abstract void error(Error error);

	/**
	 * 上传完成
	 * @param fileInfos
	 */
	public abstract void success(List<FileInfo> fileInfos);
	
	/**
	 * 上传完成
	 * @param fileInfo
	 */
	public abstract void success(FileInfo fileInfo);
	
	/**
	 * 重命名，可以为空，
	 * 文件名称不含后缀名
	 */
	public abstract String getFilename();
}
