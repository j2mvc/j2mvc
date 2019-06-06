package com.j2mvc.framework.action;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ActionBean
 * 
 * @version 1.0 2014-2-23创建@杨朔
 * @version 1.1 2014-8-21更改@杨朔
 */
public class ActionBean {
	/** 是否为包含页面 */
	private boolean incude = false;
	/** 包名 */
	private String packageName; 
	/** URL上层路径 */
	private String path;
	/** 路径描述 */
	private String pathDescription;
	/** 路径开启权限控制 */
	private boolean pathAuth;
	/** 文件目录 */
	private String dir;
	/** URI */
	private String uri;
	/** URL请求参数串 */
	private Map<String, String> querys;
	/** 类名 */
	private String className;
	/** 方法 */
	private Method method;
	/** 标题 */
	private String title;
	/** 关键字 */
	private String keywords;
	/** 描述 */
	private String description;
	/** 标签,相当于分组或分类 */
	private String tag;
	/** 是否开启权限控制 */
	private boolean auth;
	/** 无权限代码 */
	private String authNone;
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAuth() {
		return auth;
	}
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	public String getPathDescription() {
		return pathDescription;
	}
	public void setPathDescription(String pathDescription) {
		this.pathDescription = pathDescription;
	}
	public boolean isPathAuth() {
		return pathAuth;
	}
	public void setPathAuth(boolean pathAuth) {
		this.pathAuth = pathAuth;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public Map<String, String> getQuerys() {
		return querys;
	}
	public void setQuerys(Map<String, String> querys) {
		this.querys = querys;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAuthNone() {
		return authNone;
	}
	public void setAuthNone(String authNone) {
		this.authNone = authNone;
	}
	public boolean isIncude() {
		return incude;
	}
	public void setIncude(boolean incude) {
		this.incude = incude;
	}

	
}
