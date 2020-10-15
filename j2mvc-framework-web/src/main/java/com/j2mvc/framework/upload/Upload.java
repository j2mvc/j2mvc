package com.j2mvc.framework.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.j2mvc.util.Error;
import com.j2mvc.util.OSType;
import com.j2mvc.framework.config.Config;
import com.j2mvc.framework.upload.entity.FileInfo;
import com.j2mvc.util.json.JSONFactory;
import com.j2mvc.util.json.JSONParse;

/**
 * 上传文件
 * @author 杨朔
 *	2014年1月14日
 */
public class Upload {
	Logger log = Logger.getLogger(getClass().getSimpleName());
	private HttpServletRequest request;
	private HttpServletResponse response;

	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	// 最大文件大小1MB
	long maxSize =  5*1024*1024;
	// 保存类型：media,file,image,flash
  	private String dirName = "";
	// 定义允许上传的文件扩展名
	private Map<String, String> suffixMap = new HashMap<String,String>();
	private String exts = "";
    // 保存路径
	private String savePath = "";
	// 保存URL
	private String saveUrl = "";
	// 图标路径
	private String iconPath = "";
	// 图标文件真实路径
	private String iconFilePath = "";
	// 上传通知
	private UploadHandler handler;
	// 保持原文件名
	private boolean keepOriginName = true;
	// 只接收输入流，不创建文件
	private boolean inputStreamOnly = false;
	// 文本数据
	private Map<String,String> textData = new HashMap<String,String>();
	// 错误列表
	private List<Error> errors = new ArrayList<Error>();
	// 上传成功的文件列表
	List<FileInfo> fileList = new ArrayList<FileInfo>();
	// 上传成功的输入流列表
	List<InputStream> inputStreamList = new ArrayList<InputStream>();
	 
	public Upload(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
		init();
	}
	private void init() {
		Map<String,String> config = Config.props.get("upload");
		if(config!=null) {
			// 最大文件大小1MB
			ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript"); 
			if(config.get("maxSize")!=null) {
				try {
					maxSize = Long.parseLong(String.valueOf(scriptEngine.eval(config.get("maxSize"))));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
			// 保存类型：media,file,image,flash
		  	dirName = config.get("dirName");
			// 定义允许上传的文件扩展名
			exts =  config.get("exts");
		    // 保存路径
			savePath = config.get("savePath");
			// 保存URL
			saveUrl = config.get("saveUrl");
			// 图标路径
			iconPath = config.get("iconPath");
			// 图标文件真实路径
			iconFilePath = config.get("iconFilePath");
		}else {
			try {
				throw new Exception("未找到配置文件upload.properties");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<FileInfo> getFileList() {
		return fileList;
	}
	
	public List<InputStream> getInputStreamList() {
		return inputStreamList;
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
	public void setInputStreamOnly(boolean inputStreamOnly) {
		this.inputStreamOnly = inputStreamOnly;
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
	 * @param exts
	 */
	public void setExts(String exts) {
		this.exts = exts;
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
		// 初始化路径配置
		// 首先是加载配置文件upload.properties
		// 这里有可能会被注解方法改写
		if(exts!=null) {
		  	// 设置文件扩展名
			initSuffixMap(exts);
		}
		// 检查目录
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			uploadDir.mkdirs();
		}
		// 检查目录写权限
		if(!uploadDir.canWrite()){
			//setError(Error.ERROR_AUTH,"上传目录没有写权限。");
			
			if(!OSType.OSinfo.isWindows()) {
				Runtime rn = Runtime.getRuntime();
				try {
					rn.exec("chmod 655 -R "+uploadDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	  	
		// 按文件类型保存
	  	if(!suffixMap.containsKey(dirName)){
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
		/**** 以下开始读取文件 */
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
			setError(Error.ERROR_NULL,e.getMessage());
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
		// 删除空对象
		fileList.removeAll(Collections.singleton(null));
		if(handler!=null){
			handler.success(fileList,textData);
			if(errors.size()>0){
				handler.error(errors);
			}
		}else {
			Map<String,Object> m = new HashMap<String,Object>();
			if(fileList!=null && fileList.size()>0)
				m.put("fileList",fileList);
			if(textData!=null && textData.size()>0)
				m.put("textData",textData);
			if(errors.size()>0){
				m.put("errors", errors);
			}
//			printJson(m);
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
				// 将fileInfo重置为空对象
				fileList.set(i, null);
			}
			// 是表单才进行处理
			if(!item.isFormField()){
				// 文件扩展名
				String fileSuffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
				if( !iconPath.equals("")){
					String iconExt = fileSuffix+".png";
					File iconFile = new File(iconFilePath+iconExt);
					if(!iconFile.exists()){
						iconExt = "file.png";
					}		
					String icon = iconPath+iconExt;		
					info.setIcon(icon);
				}
				if(info.getTotalSize() > maxSize){
					//检查文件大小
					fileList.set(i, null);
					setError(Error.ERROR_IO,"["+filename+"]大小超过限制。");
				}else if(suffixMap!=null && suffixMap.get(dirName)!=null 
						&& !Arrays.<String>asList(suffixMap.get(dirName).split(",")).contains(fileSuffix)){
					//检查文件扩展名
					fileList.set(i, null);
					setError(Error.ERROR_AUTH,"["+filename+"]不允许的扩展名!只允许" + suffixMap.get(dirName) + "格式。");
				}else{
					if(inputStreamOnly) {
						// 仅接收输入流，不创建文件
						try {
							inputStreamList.add(item.getInputStream());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					    	setError(Error.ERROR_IO,"["+filename+"]上传失败。");
						}
					}else {
						String newFilename = filename;
						if(!keepOriginName) {
							// 不保持原文件名
							newFilename = Util.getRandomUUID(String.valueOf(new Date().getTime())) + "." + fileSuffix;
						}
						// 设置文件名:时间戳+"_"+4位随机数
						// 保存路径和访问路径
						info.setSavePath(savePath+newFilename);
						info.setUrl(saveUrl+newFilename);
						info.setFilename(newFilename);
						try {
					    	// 写文件
							File uploadedFile = new File(savePath, newFilename);
							item.write(uploadedFile);
							fileList.set(i, info);// 或许不需要
							chmod(savePath+newFilename);
					    } catch (Exception e) {
					    	fileList.set(i, null);
					    	setError(Error.ERROR_IO,"["+filename+"]上传失败。");
//					    	printJson(new Error(Error.ERROR_IO,"["+filename+"]上传失败。"));
					    }
					}
				}
			}else {
				// 文本数据
				if(item.getFieldName()!=null) {
					log.info("正在接收文本数据textData："+item.getFieldName());
					textData.put(item.getFieldName(),item.getString());
				}
			}		
		}
	}

	
	
	private void chmod(String fullpath) {
		try {
			Runtime.getRuntime().exec("chmod a+r "+fullpath);
		} catch (IOException e) {
		}
	}
	/**
	 * 设置文件扩展名
	 */
	private void initSuffixMap(String json){
		suffixMap = JSONParse.parseMap(json, String.class, String.class, suffixMap);
		suffixMap = suffixMap == null?new HashMap<String,String>():suffixMap;
		// 自定义扩展名
		String exts = getParam("exts");
	  	if(exts!=null && !exts.equals("") && dirName!=null){
	  		exts = exts.replaceAll("*.", "");
	  		exts = exts.replaceAll(";", ",");
	  		suffixMap.put(dirName, exts.toLowerCase());
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
//	private void printJson(Object object){
//		try {
//			response.getWriter().print( (new JSONFactory().toJsonObject(object,true)).toString());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//	}
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
