package com.j2mvc.framework.upload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;
import com.j2mvc.util.json.JSONFactory;

/**
 * 上传文件
 * @author 杨朔
 *	2014年1月14日
 */
public class UploadMutli {
	Logger log = Logger.getLogger(getClass().getSimpleName());
	private HttpServletRequest request;
	private HttpServletResponse response;

	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	// 最大文件大小1MB
	long maxSize =  5*1024*1024;
	// 保存类型：media,file,image,flash
  	private String dirName = "image";
	// 定义允许上传的文件扩展名
	private HashMap<String, String> extMap;
	private String ext = "";
    // 保存路径
	private String savePath = "";
	// 保存URL
	private String saveUrl = "";
	// 图标路径
	private String iconPath;
	// 图标文件真实路径
	private String iconFilePath = "";
	// 项目路径
	private String contextPath;
	// 上传通知
	private UploadHandler handler;
	// 保持原文件名
	private boolean keepOriginName = true;
	// 文本数据
	private Map<String,String> textData = new HashMap<String,String>();
	// 错误列表
	private List<Error> errors = new ArrayList<Error>();
	// 上传成功的文件列表
	List<FileInfo> fileList = new ArrayList<FileInfo>();
	 
	public UploadMutli(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
	}
	public List<FileInfo> getFileList() {
		return fileList;
	}
	public Map<String, String> getTextData() {
		return textData;
	}
	public List<Error> getErrors() {
		return errors;
	}
	public void setKeepOriginName(boolean keepOriginName) {
		this.keepOriginName = keepOriginName;
	}
	/**
	 *  上传最大文件大小，默认为1MB，单位为b
	 * @param maxSize
	 */
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	/**
	 * 上传文件保存目录，media,file,image,flash，默认为image 
	 * @param dirName
	 */
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	/**
	 * 文件保存路径，默认为/项目名/upload/
	 * @param savePath
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	/**
	 * 文件访问路径，默认为/项目名/upload/
	 * @param saveUrl
	 */
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
	/**
	 *  文件允许的后缀，多个用英文逗号分隔,如:".png,.jpg,.gif"
	 * @param ext
	 */
	public void setExt(String ext) {
		this.ext = ext;
	}

	/**
	 * 构造器
	 * @param request
	 * @param response
	 * @param handler 上传文件回调通知
	 * @return 
	 * @throws IOException
	 */
	public void execute(UploadHandler handler) throws IOException{
		this.handler = handler;
		execute();
	}
	/**
	 * 上传
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	synchronized public void execute() throws IOException{
		// 限制文件大小
		String maxSizeStr = !getParam("maxSize").equals("")?getParam("maxSize"):"";
		if(!maxSizeStr.equals("")){
			try{
				maxSize = Long.parseLong(maxSizeStr);
			}catch(Exception e){
			}
		}

		// 目录
	  	dirName = getParam("dir")!=null && !getParam("dir").equals("")?getParam("dir"):dirName;
	  	dirName = dirName.toLowerCase().trim();
	  	// 设置文件扩展名
	  	initExtMap();
	  	// 保存路径和访问路径
	  	initSaves();

		// 判断是否有文件
		if(!ServletFileUpload.isMultipartContent(request)){
			setError(Error.ERROR_NULL,"请选择文件。");
			return;
		}
		// 上传实例
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

		List<FileItem> list = null;
		try {
			list = servletFileUpload.parseRequest(request);
			
		} catch (FileUploadException e) {
			setError(Error.ERROR_NULL,"FileUploadException:"+e.getMessage());
		}
		if(list == null || list.size() == 0){
			setError(Error.ERROR_NULL,"上传列表为空。");
			return;
		}
		for(int i=0;i<list.size();i++) {
			FileInfo fileInfo = new FileInfo();
			// 保存到session，用于获取上传进度
			request.setAttribute(fileInfo.getId(), fileInfo);
			// 兼听器实例
			Lister lister = new Lister(fileInfo);
			// 注册兼听器
			servletFileUpload.setProgressListener(lister);
			fileList.add(fileInfo);
		}
		log.info("获取到上传列表,共"+list.size()+"个数据对象。");
		// 写文件
		write(list,request,response,servletFileUpload);

		if(handler!=null){
			handler.success(fileList,textData);
			if(errors.size()>0){
				handler.error(errors);
			}
		}else {
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("fileList",fileList);
			m.put("textData",textData);
			if(errors.size()>0){
				m.put("errors", errors);
			}
			printJson(m);
		}
	}
	/**
	 * 写文件
	 * @param request
	 * @param servletFileUpload
	 * @throws UnsupportedEncodingException 
	 */
	synchronized private void write(List<FileItem> list,HttpServletRequest request,HttpServletResponse response,ServletFileUpload servletFileUpload) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		
		for(int i=0;i<list.size();i++){
			// 上传实例
			FileInfo info = fileList.get(i);
			// 得到文件对象
			FileItem item = list.get(i);
			String filename = item.getName();
			if(!item.isFormField()) {
				log.info("正在接收上传任务："+i+";实体对象[fileInfo.id] >> "+info.getId()+";文件对象[fileItem] >> "+filename);
			}else {
				log.info("正在接收文本数据："+filename);
			}
			// 是表单才进行处理
			if(!item.isFormField()){
				// 文件扩展名
				String fileExt = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
				if( !iconPath.equals("")){
					String iconExt = fileExt+".png";
					File iconFile = new File(iconFilePath+iconExt);
					if(!iconFile.exists()){
						iconExt = "file.png";
					}		
					String icon = iconPath+iconExt;		
					info.setIcon(icon);
				}
				if(info.getTotalSize() > maxSize){
					//检查文件大小
					setError(Error.ERROR_IO,"大小超过限制。");
				}else if(extMap.get(dirName)!=null && !extMap.get(dirName).equalsIgnoreCase("all") && !Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
					//检查文件扩展名
					setError(Error.ERROR_AUTH,"不允许的扩展名!只允许" + extMap.get(dirName) + "格式。");
				}else{
					if(!keepOriginName) {
						// 不保持原文件名
						filename = Util.getRandomUUID(String.valueOf(new Date().getTime())) + "." + fileExt;
					}
					// 设置文件名:时间戳+"_"+4位随机数
					// 保存路径和访问路径
					info.setSavePath(savePath+filename);
					info.setUrl(saveUrl+filename);
					info.setFilename(filename);
					try {
				    	// 写文件
						File uploadedFile = new File(savePath, filename);
						item.write(uploadedFile);
						fileList.set(i, info);// 或许不需要
				    } catch (Exception e) {
				    	setError(Error.ERROR_IO,"上传失败。");
				    	printJson(new Error(Error.ERROR_IO,"上传失败。"));
				    }
				}
			}else {
				// 文本数据
				textData.put(item.getFieldName(),item.getString());
			}		
		}
	}
	
	/**
	 * 设置文件扩展名
	 */
	private void initExtMap(){
		extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "sql,js,css,jsp,html,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
		// 自定义扩展名
	  	ext = !getParam("ext").equals("")?getParam("ext"):ext;
	  	if(!ext.equals("") && dirName!=null){
	  		ext = ext.replace("*.", "");
	  		ext = ext.replace(";", ",");
	  		extMap.put(dirName, ext.toLowerCase());
	  	}
	}

	/**
	 * 设置保存路径和访问路径
	 * @param savePath
	 */
	@SuppressWarnings("deprecation")
	private void initSaves(){
		contextPath = request.getContextPath();
		// 获取传来的图标路径
		iconPath = getParam("iconPath");
		iconPath = iconPath.endsWith("/")?iconPath:iconPath+"/";
		if(!contextPath.equals("")&& iconPath.length()>contextPath.length())
			iconFilePath = iconPath.substring(contextPath.length(),iconPath.length());
		iconFilePath = request.getRealPath(iconFilePath);
		iconFilePath = iconFilePath.endsWith("/")?iconFilePath:iconFilePath+"/";
		// 获取传来的保存路径
		savePath = !getParam("savePath").equals("")?getParam("savePath"):savePath;
		// 获取传来的访问URL
		saveUrl = !getParam("saveUrl").equals("")?getParam("saveUrl"):saveUrl;
		
		// 保存路径
		savePath = !savePath.equals("")?savePath: request.getRealPath("/") + "upload/";
		// 文件访问URL
		saveUrl  = !saveUrl.equals("")?saveUrl: request.getContextPath() + "/upload/";		
		
		// 检查目录
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			uploadDir.mkdirs();
		}
		// 检查目录写权限
		if(!uploadDir.canWrite()){
			setError(Error.ERROR_AUTH,"上传目录没有写权限。");
			return;
		}	  	
		// 按文件类型保存
	  	if(!extMap.containsKey(dirName)){
			setError(Error.ERROR_IO,"目录名不正确。");
	  		return;
	  	}
	  	savePath += dirName + "/";
	  	saveUrl += dirName + "/";
		// 按日期保存
	  	String ymd = format.format(new Date());
	  	savePath += ymd + "/";
	  	saveUrl += ymd + "/";
	  	File dirFile = new File(savePath);
	  	if (!dirFile.exists()) {
	  		dirFile.mkdirs();
	  	}	
		// 最终保存路径
		savePath = savePath.replace("\\", "/");
		if(!savePath.endsWith("\\") && !savePath.endsWith("/")){
			savePath = savePath + "/";
		}	
		// 最终访问路径
		if( !saveUrl.endsWith("/")){
			saveUrl = saveUrl + "/";
		}	
	}

	/**
	 * 设置错误信息
	 * @param code
	 * @param message
	 */
	private void setError(int code,String message){
		Error error = new Error(code,message);
    	errors.add(error);
	}
	private void printJson(Object object){
		try {
			response.getWriter().print( (new JSONFactory().toJsonObject(object,true)).toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * 返回指定页面参数值
	 * @param request
	 * @param name
	 * @return
	 */
	private String getParam(String name){
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
			setError(Error.ERROR_NULL,"Exception:"+e.getMessage());
		}
		return value;
	}

	public static void main(String ...args){
		String url = "http://192.168.1.112:8081/zhaibao-api/resources/attaches/";
		URI uri = URI.create(url);
		System.out.println(uri.getHost());
		System.out.println(uri.getPath());
		FileInfo info1= new FileInfo();
		info1.setUrl(url);
		FileInfo info2 = new FileInfo();
		info2.setUrl("url");
		List<FileInfo> infos = new ArrayList<FileInfo>();
		infos.add(info1);
		infos.add(info2);
		System.out.println(new JSONFactory().toJsonArray(infos));
	}

}
