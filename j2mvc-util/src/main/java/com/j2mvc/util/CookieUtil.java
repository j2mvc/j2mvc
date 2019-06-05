package com.j2mvc.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Cookie类
 * 
 * 2014-3-29 创建@杨朔 */
public class CookieUtil {

	/**
	 * 设置Cookie
	 * @param response 响应
	 * @param cookieName cookie名称
	 * @param value cookie值
	 * @param maxAge 最大时间
	 * @param path  路径
	 * @param pattern 路径匹配
	 */
	public static void setCookie(HttpServletResponse response,
								String cookieName, 
								String value, 
								Integer maxAge,
								String path,
								String pattern) {
		if (response != null) {
			response.addCookie(addCookie(cookieName, value, maxAge, path,pattern));
		}
	}

	/**
	 * 添加
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param path
	 * @param pattern
	 */
	public static Cookie addCookie(
			String name, 
			String value, 
			Integer maxAge,
			String path, 
			String pattern) {
		try {
			Cookie cookie = new Cookie(name, value);
			if (maxAge != null)
				cookie.setMaxAge(maxAge.intValue());
			cookie.setPath(path!=null && !path.equals("")?path:"/");
			if (pattern!=null && !pattern.equals("")) {
				cookie.setDomain(pattern);
				// cookie.setDomain(".worda.cn"); 
			}
			return cookie;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 移除cookie
	 * @param request
	 * @param response
	 * @param name
	 * @param pattern
	 */
	public static void reMoveCookie(HttpServletRequest request,
									HttpServletResponse response, 
									String name,
									String path,
									String pattern) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(name)){
					cookie = new Cookie(name, null);
					if (pattern!=null && !pattern.equals(""))
						cookie.setDomain(pattern);
					cookie.setPath(path!=null && !path.equals("")?path:"/");
					cookie.setMaxAge(0);
					cookie.setValue(null);
					response.addCookie(cookie);
				}
			}
		}
	}

	/**
	 * 获取指定名称的cookie
	 * @param request
	 * @param cookieName
	 */
	public static Cookie getCookie(HttpServletRequest request,String cookieName){		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)){
					return cookie;
				}
			}
		}
		return null;
	}
	/**
	 * 查找指定名称的cookie值
	 * @param request
	 * @param cookieName
	 */
	public static String getCookieValue(HttpServletRequest request,String cookieName){
		Cookie cookie = getCookie(request, cookieName);
		if(cookie!=null){
			return cookie.getValue();
		}
		return null;
	}
}
