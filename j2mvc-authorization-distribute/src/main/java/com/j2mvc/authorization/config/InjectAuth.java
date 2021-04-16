package com.j2mvc.authorization.config;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.authorization.entity.Auth;
import com.j2mvc.authorization.global.AuthConstants;
import com.j2mvc.util.StringUtils;


/**
 * 注入系统权限
 * 2014-4-26创建@杨朔
 */
public class InjectAuth {
	Logger log = Logger.getLogger(getClass().getName());

	/**
	 * 注入系统权限
	 */
	public void execute(){
		injectPaths();
		injectUris();
		injectQueryUris();
	}
	
	/**
	 * 注入Path权限
	 */
	public void injectPaths(){
		Set<String> paths = Session.paths;
		Iterator<String> iterator = paths.iterator();
		while (iterator.hasNext()){
			String path = iterator.next();
			ActionBean bean = Session.pathMap.get(path);
			String name = bean!=null?!bean.getPathDescription().trim().equals("") ? bean.getPathDescription():"未命名"+timestamp():"未命名"+timestamp();
			Auth auth = new Auth();
			auth.setId(id());
			auth.setProjectId(AuthConfig.projectId);
			auth.setName(name);
			auth.setValue(path.length() > 1 && !path.endsWith("/")?path+"/":path);
			auth.setType(AuthConstants.AUTH_TYPE_PATH);
			auth.setStatus(bean.isPathAuth()?AuthConstants.AUTH_STATUS_Y:AuthConstants.AUTH_STATUS_N);
			auth.setTag(bean.getTag());
			auth.setAuthNone(bean.getAuthNone());
			AuthConfig.sysAuths.add(auth);
		}			
	}

	/**
	 * 注入URI权限
	 */
	public void injectUris(){
		Set<String> uris = Session.uris;
		Iterator<String> iterator = uris.iterator();
		while (iterator.hasNext()){
			String uri = iterator.next();
			ActionBean bean = Session.beans.get(uri);
			if(bean!=null){
				String description = bean.getDescription();
				String name = description!=null&&!description.trim().equals("") ?description:"未命名"+timestamp();
				Auth auth = new Auth();
				auth.setId(id());
				auth.setProjectId(AuthConfig.projectId);
				auth.setName(name);
				auth.setValue(parseUri(bean));
				auth.setType(AuthConstants.AUTH_TYPE_URI);
				auth.setStatus(bean.isAuth()?AuthConstants.AUTH_STATUS_Y:AuthConstants.AUTH_STATUS_N);
				auth.setTag(bean.getTag());
				auth.setAuthNone(bean.getAuthNone());
				AuthConfig.sysAuths.add(auth);
			}
		}
	}

	/**
	 * 注入带参URI权限
	 */
	public void injectQueryUris(){
		Set<Entry<String,ActionBean>> set = Session.queryUriBeans.entrySet();
		Iterator<Entry<String,ActionBean>> iterator = set.iterator();
		while (iterator.hasNext()){
			Entry<String,ActionBean> entry = iterator.next();
			ActionBean bean = entry.getValue();
			if(bean!=null){
				String queryString = buildQuery(bean.getQuerys());
				String value = parseUri(bean)+"([?])"+queryString;
				String description = bean.getDescription();
				String name = description!=null&&!description.trim().equals("") ?description:"未命名"+timestamp();
				Auth auth = new Auth();
				auth.setId(id());
				auth.setProjectId(AuthConfig.projectId);
				auth.setName(name);
				auth.setValue(value);
				auth.setType(AuthConstants.AUTH_TYPE_URL);
				auth.setStatus(bean.isAuth()?AuthConstants.AUTH_STATUS_Y:AuthConstants.AUTH_STATUS_N);
				auth.setTag(bean.getTag());
				auth.setAuthNone(bean.getAuthNone());
				AuthConfig.sysAuths.add(auth);
			}
		}
	}
	/**
	 * 生成uri
	 * @param bean
	 * 
	 */
	private String parseUri(ActionBean bean){
		String path = bean.getPath(); 
		path = path.endsWith("/")?path:path+"/";
		path = StringUtils.deleRepeat(path, "/");
		String uri = bean.getUri();
		if(uri.startsWith("([/])?")){
			path = path.substring(0,path.length() - 1);
		}
		uri = path + uri; 
		uri = StringUtils.deleRepeat(uri, "/");
		return uri;
	}
    /**
     * 生成query字符串
     * @param qString
     * 
     */
	private String  buildQuery(Map<String,String> map){
		String query = "";
		if(map!=null && map.size() > 0){
			Set<Entry<String,String>> set = map.entrySet();
			Iterator<Entry<String,String>> iter = set.iterator();
			 
			while(iter.hasNext()){
				Entry<String,String> entry = iter.next();
				query += (!query.equals("")?"&":"")+entry.getKey()+"="+entry.getValue();
			}
		}
		return query;
	}

	/**
	 * 创建时间戳
	 * 
	 */
	public String timestamp(){
		long timestamp = new Date().getTime();
		return timestamp + ""+new Random().nextInt(10000);
	}
	/**
	 * 创建时间戳
	 * 
	 */
	public String id(){
		long timestamp = new Date().getTime();
		return timestamp + "_"+new Random().nextInt(10000);
	}
}
