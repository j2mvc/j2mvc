package com.j2mvc.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.dao.DataSourceBean;
import com.j2mvc.framework.interceptor.DispatcherInterceptor;
/**
 * 会话变量
 * 
 * @version 1.0 2014-2-23创建@杨朔
 * @version 1.1 2014-8-21更改@杨朔
 */
public class Session {

	/** 默认后缀 */
	public static String subfix = ".do";

	public static String encoding = "utf-8";

	/** pathMap集合 */
	public static Map<String,ActionBean> pathMap = new HashMap<String,ActionBean>();
	
	/** beanMap集合 */
	public static Map<String,ActionBean> beans = new HashMap<String,ActionBean>();

	/** 含有query参数串的uri集合 */
	public static Map<String,Set<String>> queryUris = new HashMap<String,Set<String>>();
	
	/** 含有query参数串的uriBean集合 */
	public static Map<String,ActionBean> queryUriBeans = new HashMap<String,ActionBean>();

	/** path集合 */
	public static Set<String> paths = new HashSet<String>();
	/** 权限集合 */
	public static Map<String,Object> auths = new HashMap<String,Object>();
	
	/** actions-packages集合 */
	public static String[] actionsPackages = new String[]{};
	
	/** URI集合 */
	public static Set<String> uris = new HashSet<String>();

	/** 拦截器集合 */
	public static List<DispatcherInterceptor> interceptors = new ArrayList<DispatcherInterceptor>();
	
	/** 数据源 */
	public static DataSourceBean dataSourceBean = new DataSourceBean();

	/** 多个数据源 */
	public static Map<String,DataSourceBean> dataSourceBeanMap = new HashMap<String,DataSourceBean>();
	
	/** 是否输出SQL日志 */
	public static boolean sqlLog = false;
	
	/** 开启日志*/
	public static boolean uriLog = false;
}
