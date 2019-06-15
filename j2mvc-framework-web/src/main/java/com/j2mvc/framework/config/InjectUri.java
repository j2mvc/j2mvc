package com.j2mvc.framework.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.ActionUpload;
import com.j2mvc.framework.mapping.ActionPath;
import com.j2mvc.framework.mapping.UploadMeta;
import com.j2mvc.framework.mapping.ActionUri;
import com.j2mvc.framework.mapping.ContentType;
import com.j2mvc.framework.mapping.IncludePage;
import com.j2mvc.framework.mapping.RequestMethod;
import com.j2mvc.framework.util.InjectUtils;


/**
 * 注入系统方法权限
 * @version 1.0 2014-4-26创建@杨朔
 * @version 1.1 2014-8-21更改@杨朔
 */
public class InjectUri {
	static final Logger log = Logger.getLogger(InjectUri.class);

	/**
	 * 注入系统权限
	 */
	public void execute(){
		injectActions();
	}
	
	/**
	 * 注入方法权限
	 */
	private void injectActions(){
		InjectUtils injectUtils = new InjectUtils();
		String [] packages = Session.actionsPackages;
		if(packages!=null){
			for(String packageName:packages){
				List<Class<?>> classes = injectUtils.getClasses(packageName);
				if(classes!=null && classes.size()>0){
					for(Class<?> clazz : classes){
						injectActions(clazz);
					}
				}else {
					log.error("未找到Action控制器类,请检查是否正确配置类注解.");
				}
			}
		}else {
			log.error("未找到Action控制器包,请检查配置文件是否正确填写包名.");
		}
	}

	/**
	 * 注入Actions
	 * @param clazz
	 */
	private void injectActions(Class<?> clazz){
		ActionPath actionsPath = clazz.getAnnotation(ActionPath.class);
		// 文件目录
		String dir = actionsPath!=null?actionsPath.dir():"";
		// 访问路径
		String path = actionsPath!=null?actionsPath.path():"";
		// 访问路径描述
		String pathDescription =  actionsPath!=null?actionsPath.description():"";
		// 访问路径是否开启权限控制
		boolean pathAuth = actionsPath!=null?actionsPath.auth():false;
		
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method method:methods){
			IncludePage includePage = method.getAnnotation(IncludePage.class);
			ActionUri actionUri = method.getAnnotation(ActionUri.class);		
			UploadMeta uploadMeta = method.getAnnotation(UploadMeta.class);	
			RequestMethod requestMethod = method.getAnnotation(RequestMethod.class);	
			ContentType contentType = method.getAnnotation(ContentType.class);
			if(actionUri!=null){
				// URI
				String uri = actionUri.uri();
				// 标题
				String title = actionUri.title();
				// 关键字
				String keywords = actionUri.keywords();
				// 描述
				String description = actionUri.description();
				// 描述
				String tag = actionUri.tag();
				// 无权限代码
				String authNone = actionUri.authNone();
				// 是否开启权限控制
				boolean auth = actionUri.auth();
				
				String actionPath = path.endsWith("/") ? path : path + "/";
				dir = dir.endsWith("/") ? dir : dir + "/";

	            	String query = actionUri.query();
				// action组
				ActionBean bean = new ActionBean();
				bean.setPackageName(clazz.getPackage().getName());
				bean.setClassName(clazz.getName());				
				bean.setDir(dir);
				bean.setPath(uri.startsWith("/")?"":actionPath);
				bean.setPathDescription(pathDescription);
				bean.setIncude(includePage!=null);
				
				bean.setPathAuth(pathAuth);

				// action对象
				bean.setMethod(method);
				bean.setTitle(title);
				bean.setKeywords(keywords);
				bean.setDescription(description);
				bean.setUri(uri);
				bean.setQuerys(map(query));

				// action权限
				bean.setAuth(auth);
				bean.setTag(tag); 
				bean.setAuthNone(authNone);


				// 请求方式
				bean.setRequestMethod(requestMethod!=null?requestMethod.value():null);
				// 请求数据类型
				bean.setContentType(contentType!=null?contentType.value():null);
				// 上传
				if(uploadMeta != null) {
					ActionUpload actionUpload = new ActionUpload();
					actionUpload.setSavePath(uploadMeta.savePath());
					actionUpload.setSaveUrl(uploadMeta.saveUrl());
					actionUpload.setExt(uploadMeta.ext());
					actionUpload.setDirname(uploadMeta.dirname());
					actionUpload.setMaxSize(uploadMeta.maxSize());
					actionUpload.setFilename(uploadMeta.filename());
					actionUpload.setKeepOriginName(uploadMeta.keepOriginName());
					bean.setActionUpload(actionUpload);
				}
				// 封装到MAP
				String key = (uri.startsWith("/")?"":actionPath) + uri;
				key = key.replace("/([", "([");
				key = key.replace("//", "/");

				// 将URL放入session集合
				String url = !query.equals("")?uri + ":" + query:"";
				if(!url.equals("")){
					 Set<String> urls = Session.queryUris.get(key);
					 if(urls == null)
						 urls = new HashSet<String>();
					 urls.add(url);
					 Session.queryUris.put(key, urls);
					 Session.queryUriBeans.put(url, bean);
				}else {
					Session.beans.put(key, bean);
				}
				Session.uris.add(key);
				
				// 未包含路径，以及包含路径且当前权限开启状态为真
				if(!Session.paths.contains(actionPath) || 
						(Session.paths.contains(actionPath) && pathAuth)){
					Session.paths.add(actionPath);
					Session.pathMap.put(actionPath, bean);
				}
						
				if(Session.uriLog)
					log.info("init Config 映射URI "+(!title.trim().equals("")?" >> " +title:"")+" >> " + actionPath + uri);

			}
		}
		
	}
    /**
     * 解析字符串
     * @param qString
     * 
     */
	private Map<String,String> map(String query){
		Map<String, String> map = new HashMap<String, String>();
		String[] array = query.split("&");
		if(array!=null){
			for (int i = 0; i < array.length; i++) {
				String arr = array[i];
				String[] item = arr.split("=");
				if(item!=null && item.length > 1){
					map.put(item[0],item[1]);
				}
			}
		}
		return map;
	}
}
