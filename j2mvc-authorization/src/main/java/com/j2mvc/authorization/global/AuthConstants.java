package com.j2mvc.authorization.global;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * 2014-4-12创建@杨朔
 */
public class AuthConstants {
	/** 默认权限控制配置文件 */
	public final static String authConfig = "/WEB-INF/authorization.xml";

	/** 
	 * 权限开启
	 */
	public static final String AUTH_STATUS_Y = "1";
	/** 
	 * 权限关闭
	 */
	public static final String AUTH_STATUS_N = "0";
	/** 
	 * 权限类型-路径
	 * 此权限将作用到所有此路径下的所有URI
	 */
	public static final int AUTH_TYPE_PATH = 0;
	
	/** 
	 * 权限类型 -URI
	 * URI属于读权限，通常为访问URI
	 * 若用户角色含有此URI，即有权限，允许访问。
	 */
	public static final int AUTH_TYPE_URI = 1;

	/** 
	 * 权限类型 - URL
	 * 提交权限为：RADU（查看增加删除更新），通常在表单提交时控制。
	 * 定义规则为“submit.实体名.方法名”，示例：submit.leter.a。
	 */
	public static final int AUTH_TYPE_URL = 2;

	/** 
	 * 权限类型 -菜单显示权限
	 * 此权限根据不同的角色获取相应的菜单权限，并显示
	 */
	public static final int AUTH_TYPE_MENU = 3;

	/** 
	 * 其它额外限制,不限制-1
	 */
	public static final int AUTH_EXTRA_LIMIT_NONE = -1;

	/** 
	 * 权限类型值和名称
	 */
	public static final Map<Integer, String> AUTH_TYPES = new HashMap<Integer, String>();
	static{
		AUTH_TYPES.put(AUTH_TYPE_PATH, "路径权限");
		AUTH_TYPES.put(AUTH_TYPE_URI, "uri权限");
		AUTH_TYPES.put(AUTH_TYPE_URL, "url权限(带参)");
//		AUTH_TYPES.put(AUTH_TYPE_MENU, "菜单显示");
	}
}
