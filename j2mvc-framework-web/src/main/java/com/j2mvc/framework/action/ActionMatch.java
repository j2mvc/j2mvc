package com.j2mvc.framework.action;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import java.util.regex.Pattern;

import com.j2mvc.framework.Session;

/**
 * Action Match URL地址匹配
 * 
 * @version 1.0 2014-2-23创建@杨朔
 * @version 1.1 2014-8-21更改@杨朔
 */
public class ActionMatch {
	static final Logger log = Logger.getLogger(ActionMatch.class);
	// URI
	private String uri;
	// queryString
	private String queryString;
	// ActionBean
	private ActionBean bean;
	
	/**
	 * 构造器
	 * @param uri
	 */
	public ActionMatch(String uri){
		this.uri = uri!=null?uri.replace("//", "/").trim():"";
	}

	/**
	 * 构造器
	 * @param uri
	 */
	public ActionMatch(String uri,String queryString){
		this.uri = uri!=null?uri.replace("//", "/").trim():"";
		this.queryString = queryString;
	}
	/**
	 * 匹配
	 * 优先级：纯字符 =>正则表达式 
	 */
	public ActionBean getBean(){
		bean = Session.beans.get(uri);
		// 如果为空，匹配正则表达式
		if(bean == null){
			// 取出URL键集合
			Iterator<String> iterator = Session.uris.iterator();
			while (iterator.hasNext()) {
				String regex = iterator.next();
				Set<String> queryUris = Session.queryUris.get(regex);
				if(queryUris!=null && queryUris.size()>0 && queryString!=null && !queryString.equals("")){
					// 有URI参数串bean设置
					if(Pattern.matches(regex, uri)){
						return getBean(queryUris,queryString);
					}
				}else{
					if(Pattern.matches(regex, uri)){// && !regex.equals("/")
						if(Session.uriLog)
							log.info("成功匹配 >> configUri="+regex+" requestUri="+uri);
						return Session.beans.get(regex);
					}else{
//						if(Session.uriLog)
//							log.info("未匹配 >> configUri="+regex+" requestUri="+uri);
					}
				}
			}
		}
		return bean;
	}

	/**
	 * 判断请求参数串是否有设置,返回actionbean
	 * @param urls
	 * @param query
	 * 
	 */
	private ActionBean getBean(Set<String> queryUris,String query){
		for (String queryUri:queryUris) {
			if(queryUri.indexOf(":")!=-1 && queryUri.indexOf("=")!=-1){
				String regexQuery = queryUri.split(":")[1];
				if(matches(regexQuery, query)){
					return Session.queryUriBeans.get(queryUri);
				}
			}
		}
		return null;
	}
    /**
     * 解析字符串，并判断请求的参数是否包含在预设置参数串里面
     * @param qString
     * 
     */
	private boolean matches(String regexQuery,String query){
		String[] regexArray = regexQuery.split("&"); // 设置的参数串
		String[] array = query.split("&");
		
		boolean include = false;
		
		if(regexArray!=null){
			for (int i = 0; i < regexArray.length; i++) {
				String regexArr = regexArray[i];
				String[] regexParam = regexArr.split("=");
				include = false;
				if(regexParam!=null && regexParam.length > 1){
					for(int j=0;j<array.length;j++){
						String arr = array[j];
						String[] param = arr.split("=");
						if(param!=null&&param.length>1){
							if(param[0].equals(regexParam[0]) && param[1].equals(regexParam[1])){
								// 包含，继续下一循环判断
								include = true;
								continue;
							}
						}
					}
				}
			}
		}
		return include;
	}
}
