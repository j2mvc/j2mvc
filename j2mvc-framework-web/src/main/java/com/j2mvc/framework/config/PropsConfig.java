package com.j2mvc.framework.config;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.j2mvc.util.PropertiesConfiguration;
/**
 * Properties 配置
 * @author 杨朔
 *
 */
public class PropsConfig {

	final static Logger log = Logger.getLogger(PropsConfig.class);
	static String confPath = "/conf/";
	static String configPath = "/config/";
	
	/**
	 * 
	 * @param fileName
	 */
	public static void init(ServletContext context){
		String classesPath = PropsConfig.class.getClassLoader().getResource("/").getPath();
		File file = new File(classesPath);
		init(context,file,null);
		
		File conf = new File(classesPath+confPath);
		if(conf.exists()) {
			init(context,conf,confPath);
		}
		File config = new File(classesPath+configPath);
		if(config.exists()) {
			init(context,config,configPath);
		}
	}
	public static void init(ServletContext context,File file,String path) {
		File[] files = file.listFiles();
		if(files!=null)
		for(File f:files){
			String fileName = f.getName();
			String suffix = f.getName().substring(fileName.lastIndexOf(".")+1,fileName.length());
			if("properties".equalsIgnoreCase(suffix)){
				init(context,(path!=null?path:"/")+fileName);
			}
		}
	}
	/**
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void init(ServletContext context,String fileName) {
		String name = fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf("."));
		if("log4j".contentEquals(name)) {
			return;
		}
		PropertiesConfiguration configuration = new PropertiesConfiguration(fileName);
    	Map<String, String> map = configuration.map();
    	// 放入缓存
    	Config.props.put(name, map);
	   	Set<Entry<String,String>> set = map.entrySet();
	   	Iterator<Entry<String,String>> iterator = set.iterator();
	   	while (iterator.hasNext()) {
	   		 Entry<String,String> entry = iterator.next();
	   		 String key = entry.getKey().trim();
	   		 String value = entry.getValue().trim();
	   		 key = key.substring(0, 1).toUpperCase()+key.substring(1, key.length());
	   		 log.info("已导入配置变量："+name+key+"="+value);
	   		 context.setAttribute(name+key,value);	
		}
	}
}
