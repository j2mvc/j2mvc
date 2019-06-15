package com.j2mvc.framework.upload;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;

public class Progress {
	
	public static abstract class Callback {
		/**
		 * 上传错误
		 * @param error
		 */
		public abstract void error(Error error);

		/**
		 * 上传完成
		 * @param fileInfos
		 */
		public abstract void success(FileInfo fileInfo);
		
	};


	/**
	 * 进度
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public Progress(HttpServletRequest request, HttpServletResponse response,Callback callback) throws IOException{
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();

		// 当前上传文件ID
		String uploadId = request.getParameter("uploadId")!=null?request.getParameter("uploadId").trim():"";
		// 默认上传文件ID为0
		uploadId = !uploadId.equals("")?uploadId:"0";
		//从Session获取上传Map
		Object object =  session.getAttribute("j2mvcUploads");
		if (null == object){
			callback.error(new Error("没有上传任务."));
			return;
		}
		
		Map<String,Object> uploads = (Map<String,Object>)object;
		// 获取上传信息
		Map<String,Object> upload = uploads.get(uploadId)!=null?(Map<String,Object>)uploads.get(uploadId):null;
		if(upload == null){
			Error error = new Error(Error.ERROR_NULL,"没有找到上传信息!");
			callback.error(error);
			return;
		}
		// 获取错误信息
		Error error = upload.get("error")!=null?(Error)upload.get("error"):null;
		// 获取上传的文件信息
		FileInfo fileInfo = upload.get("fileInfo")!=null?(FileInfo)upload.get("fileInfo"):null;
		
		// 输出错误JSON
		if(error!=null){
			callback.error(error);
		}else
		// 输出文件信息JSON
		if(fileInfo!=null){
			callback.success(fileInfo);
		}else{
			callback.error(new Error("没有上传任务."));
		}
	}
}
