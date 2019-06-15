package com.j2mvc.framework.upload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.j2mvc.util.Error;
import com.j2mvc.framework.upload.entity.FileInfo;
import com.j2mvc.util.OSType;
import com.j2mvc.util.StringUtils;
import com.j2mvc.util.json.JSONFactory;

/**
 * 上传文件
 * @author 杨朔
 *	2014年1月14日
 */
public class Upload {
	Logger log = Logger.getLogger(getClass().getSimpleName());
	/** 多媒体目录 */
	public static final String DIR_MEDIA = "media";
	/** 文件目录 */
	public static final String DIR_FILE = "file";
	/** 图片目录 */
	public static final String DIR_IMAGE = "image";
	/** flash目录 */
	public static final String DIR_FLASH = "flash";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	// 上传实例
	FileInfo fileInfo = new FileInfo();
	// 最大文件大小1MB
	private long maxSize =  1*1024*1024;
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
	// 当前上传ID
	private String uploadId;
	// 所有上传信息
	private Map<String, Object> j2mvcUploads;
	// 当前上传信息
	private Map<String, Object> upload;
	// 项目路径
	private String contextPath;
	// 上传通知
	private UploadHandler handler;
	// 错误
	private Error error;
	// 保持原文件名
	private boolean keepOriginName = true;
	// 固定原文件名
	private String filename;
	 
	public Upload(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 *  上传最大文件大小，默认为1MB，单位为b
	 * @param maxSize
	 */
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	public void setKeepOriginName(boolean keepOriginName) {
		this.keepOriginName = keepOriginName;
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
	public void execute() throws IOException{
		session = request.getSession();
	  	// 上传ID
	  	initUploadId();
		// 获取Session所有上传信息
		j2mvcUploads =  session.getAttribute("j2mvcUploads")!=null?(HashMap<String, Object>)session.getAttribute("j2mvcUploads"):null;
		if(j2mvcUploads == null){
			j2mvcUploads = new HashMap<String, Object>();
		}
		// 当前上传信息MAP
		Object object =  j2mvcUploads.get(uploadId);
		if (object != null){
			j2mvcUploads.remove(uploadId);
			session.setAttribute("j2mvcUploads", j2mvcUploads);
		}
		upload = new HashMap<String,Object>();
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
	  	if(!initSaves()) {
	  		return;
	  	};

		log.info("上传文件:savePath="+savePath+
						"\r\nsaveUrl="+saveUrl+
						"\r\niconPath="+iconPath+
						"\r\ndirName="+dirName+
						"\r\nmaxSize="+maxSize+
						"\r\next="+ext);
		// 判断是否有文件
		if(!ServletFileUpload.isMultipartContent(request)){
			setError(Error.ERROR_NULL,"请选择文件。");
			return;
		}
		// 上传实例
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
		
		// 兼听器实例
		Lister lister = new Lister(fileInfo);
		// 注册兼听器
		servletFileUpload.setProgressListener(lister);
		
		// 设置Session
		upload.put("fileInfo", fileInfo);
		// 保存到所有文件信息MAP
		j2mvcUploads.put(uploadId, upload);
		// 保存Session
		session.setAttribute("j2mvcUploads", j2mvcUploads);

		// 写文件
		write(request,response,servletFileUpload);
	}
	/**
	 * 写文件
	 * @param request
	 * @param servletFileUpload
	 * @throws UnsupportedEncodingException 
	 */
	private void write(HttpServletRequest request,HttpServletResponse response,ServletFileUpload servletFileUpload) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		
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
		for(FileItem item:list){
			// 得到文件对象
			String filename = item.getName();
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
					fileInfo.setIcon(icon);
				}
				if(fileInfo.getTotalSize() > maxSize){
					//检查文件大小
					setError(Error.ERROR_IO,"大小超过限制。");
				}else if(extMap.get(dirName)!=null && !extMap.get(dirName).equalsIgnoreCase("all") && !Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
					//检查文件扩展名
					setError(Error.ERROR_AUTH,"不允许的扩展名!只允许" + extMap.get(dirName) + "格式。");
				}else{
					// 设置文件名:时间戳+"_"+4位随机数
					if(handler!=null){
						String newFilename = handler.getFilename();
						if(newFilename!=null && !newFilename.equals("")){
							filename = newFilename  + "." +  fileExt;
						}
					}
					if(!StringUtils.isEmpty(this.filename)) {
						// 保持上传固定的文件名称
						filename = this.filename;
					}
					if(!keepOriginName) {
						// 不保持原文件名
						filename = (new Date().getTime()) + "_" + new Random().nextInt(1000) + "." + fileExt;
					}
					// 保存路径和访问路径
					fileInfo.setSavePath(savePath+filename);
					fileInfo.setUrl(saveUrl+filename);
					fileInfo.setFilename(filename);
					try {
				    	// 写文件
						File uploadedFile = new File(savePath, filename);
						if(uploadedFile.exists()) {
							uploadedFile.delete();
						}
						item.write(uploadedFile);
						if(handler!=null){
							handler.success(fileInfo);
						}
						if(OSType.OSinfo.isLinux())
							Runtime.getRuntime().exec("chmod -R 777 " + savePath);
				    } catch (Exception e) {
				    	e.printStackTrace();
			    		setError(Error.ERROR_IO,"上传失败。");
			    		this.error = new Error(Error.ERROR_IO,"上传失败。");
				    }
				}
			}else {
				// 文本数据
				log.info(item.getFieldName()+"="+item.getString());
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
	private boolean initSaves(){
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
			setError(Error.ERROR_IO,"上传目录没有写权限。");
			return false;
		}	  	
		// 按文件类型保存
	  	if(!extMap.containsKey(dirName)){
			setError(Error.ERROR_IO,"目录名不正确，请设置文件后缀名ext和dirName。");
	  		return false;
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
		return true;
	}
	/**
	 * 设置上传ID
	 * @param request
	 */
	private void initUploadId(){
		// 当前上传文件ID
		uploadId = getParam("uploadId");
		// 默认上传文件ID为0
		uploadId = !uploadId.equals("")?uploadId:"0";
	}

	/**
	 * 设置错误信息
	 * @param code
	 * @param message
	 */
	private void setError(int code,String message){
		Error error = new Error(code,message);
		upload.put("error", error);
		j2mvcUploads.put(uploadId, upload);
		// 保存Session
		session.setAttribute("j2mvcUploads", j2mvcUploads);
		if(handler!=null){
			handler.error(error);
		}else {
			this.error = error;
		}
	}
	public FileInfo getFileInfo() {
		return fileInfo;
	}
	public Error getError() {
		return error;
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
}
