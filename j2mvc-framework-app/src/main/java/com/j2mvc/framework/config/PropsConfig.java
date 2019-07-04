package com.j2mvc.framework.config;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.j2mvc.util.OSType;
import com.j2mvc.util.PropertiesConfiguration;

public class PropsConfig {

	final static Logger log = Logger.getLogger(PropsConfig.class);
	static String path = "/conf";

	public static void init(){

		String dir = "";
		if(OSType.OSinfo.isWindows())
			dir = System.getProperty("user.dir");
		else {
			URL url =  PropsConfig.class.getResource("/");
			if(url!=null){
				dir = url.getPath();
			}
		}
		String source = dir+"/.."+path;
		File file = new File(source);
		if(!file.exists()){
			// 获取Jar包路径
			URL url = PropsConfig.class.getProtectionDomain().getCodeSource().getLocation();
			if(url!=null){
				String jarPath  = url.getPath();
				File jarFile = new File(jarPath);
				source = jarFile.getParent()+"/.."+path;
				file = new File(source);
				if(!file.exists()){
					source = jarFile.getParent()+path;
					file = new File(source);
				}
			}
		}
		if(!file.exists()){
			// 文件不存在
			file = new File(PropsConfig.class.getResource(path).getFile());
			source = file.getAbsolutePath();
		}
		log.info(" init config >> "+source);

		File[] files = file.listFiles();
		for(File f:files){
			String fileName = f.getName();
			String ext = f.getName().substring(fileName.lastIndexOf(".")+1,fileName.length());
			if("properties".equalsIgnoreCase(ext)){
				init(source+"/"+fileName);
			}
		}
	}
	/**
	 * 
	 * @param fileName
	 */
	public static void init(String fileName) {
		PropertiesConfiguration configuration = new PropertiesConfiguration(fileName);
		Config.props.putAll(configuration.map());
	}
}
