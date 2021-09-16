package com.j2mvc.framework.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.j2mvc.util.PropertiesConfiguration;

public class PropsConfig {

	final static Logger log = Logger.getLogger(PropsConfig.class);
	static String configPath = "/config/";

	public static void init() {

		File file = new File(System.getProperty("user.dir") + configPath);
		if (!file.exists()) {
			URL url = Config.class.getResource("/");
			if (url != null) {
				file = new File(url.getPath() + "/.." + configPath);
			}
		}
		if (!file.exists()) {
			// 获取Jar包路径
			URL url = PropsConfig.class.getProtectionDomain().getCodeSource().getLocation();
			if (url != null) {
				String jarPath = url.getPath();
				File jarFile = new File(jarPath);
				file = new File(jarFile.getParent() + "/.." + configPath);
			}
		}
		if (!file.exists()) {
			// 文件不存在
			file = new File(PropsConfig.class.getResource(configPath).getFile());
		}
		if (file.exists()) {
			init(file, configPath);
		}
	}

	public static void init(File file, String path) {
		File[] files = file.listFiles();
		if (files != null)
			for (File f : files) {
				String fileName = f.getName();
				String suffix = f.getName().substring(fileName.lastIndexOf(".") + 1, fileName.length());
				if ("properties".equalsIgnoreCase(suffix)) {
					init(f.getAbsolutePath());
				}
			}
	}

	/**
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void init(String fileName) {
		String name = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
		if ("log4j".equals(name)) {
			return;
		}
		if ("logging".equals(name)) {
			return;
		}


		PropertiesConfiguration configuration = new PropertiesConfiguration(fileName);

		Map<String, String> map = configuration.map();
		// 放入缓存
		Config.props.put(name, map);
		Set<Entry<String, String>> set = map.entrySet();
		Iterator<Entry<String, String>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			String key = entry.getKey().trim();
			String value = entry.getValue().trim();
			key = name + key.substring(0, 1).toUpperCase() + key.substring(1, key.length());
			log.info("已导入配置变量：" + key + "=" + value);
			Config.attributes.put(key, value);
		}
	}
}
