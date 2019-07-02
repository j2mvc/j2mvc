package com.j2mvc.framework.upload;

import java.util.List;
import java.util.Map;

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
	public abstract void error(List<Error> errors);

	/**
	 * 上传完成
	 * @param fileInfos
	 */
	public abstract void success(List<FileInfo> fileInfos,Map<String,String> textData);
	
}
