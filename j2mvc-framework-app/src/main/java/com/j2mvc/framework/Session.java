package com.j2mvc.framework;

import java.util.HashMap;
import java.util.Map;

import com.j2mvc.framework.dao.DataSourceBean;

public class Session {
	/** 数据源 */
	public static DataSourceBean dataSourceBean = new DataSourceBean();

	/** 多个数据源 */
	public static Map<String,DataSourceBean> dataSourceBeanMap = new HashMap<String,DataSourceBean>();
	
	/** 是否输出SQL日志 */
	public static boolean sqlLog = false;
	

}
