package com.j2mvc.framework.upload;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j2mvc.util.Error;

/**
 * 上传文件后缀图标
 */
public class ExtIcon {

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
		public abstract void success(String icon);
		
	};

	/**
	 * 构造器
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public ExtIcon(HttpServletRequest request, HttpServletResponse response,Callback callback) throws IOException{
		String contextPath = request.getContextPath();
		// 获取传来的文件名
		String filename = getParam(request,"filename");
		String iconPath = getParam(request,"iconPath");
		iconPath = iconPath.endsWith("/")?iconPath:iconPath+"/";
		String iconFilePath = "";
		if(!contextPath.equals("")&& iconPath.length()>contextPath.length())
			iconFilePath = iconPath.substring(contextPath.length(),iconPath.length());
		iconFilePath = request.getRealPath(iconFilePath);
		iconFilePath = iconFilePath.endsWith("/")?iconFilePath:iconFilePath+"/";
		
		if( !iconPath.equals("")){
			String fileExt = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
			String iconExt = fileExt+".png";
			File iconFile = new File(iconFilePath+iconExt);
			if(!iconFile.exists()){
				iconExt = "file.png";
			}		
			String icon = iconPath+iconExt;	
			callback.success(icon);
		}else {
			callback.error(new Error("未获取到icon路径"));
		}
		
	}
	/**
	 * 返回指定页面参数值
	 * @param request
	 * @param name
	 * @return
	 */
	private String getParam(HttpServletRequest request,String name){
		String value =  request.getParameter(name)!=null?request.getParameter(name).trim():"";
		return getUtf8(value);
	}
	/**
	 * 获取UTF8格式
	 * @param value
	 * @return
	 */
	private String getUtf8(String value){
		if(value == null)
			return "";
		try {
			if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value)){
				value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
			}else if(java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(value)){
			}else if(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value)){
				value = new String(value.getBytes("GBK"),"UTF-8");
			}else  if(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(value)){
				value = new String(value.getBytes("GB2312"),"UTF-8");
			}
		} catch (Exception e) {
		}
		return value;
	}
}
