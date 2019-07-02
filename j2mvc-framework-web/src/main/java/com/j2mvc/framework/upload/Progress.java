package com.j2mvc.framework.upload;

import java.io.IOException;

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

		// 当前上传文件的ID
		String id = request.getParameter("id")!=null?request.getParameter("id").trim():"";
		if ("".equals(id)){
			callback.error(new Error("没有指定文件ID."));
			return;
		}
		//从Session获取上传Map
		Object object =  session.getAttribute(id);
		if (null == object){
			callback.error(new Error("没有上传任务."));
			return;
		}
		FileInfo fileInfo = (FileInfo)object;
		callback.success(fileInfo);
	}
}
