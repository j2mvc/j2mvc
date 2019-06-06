package com.j2mvc.framework.i18n;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.j2mvc.util.PropertiesConfiguration;

/**
 * 国际化配置
 * 
 * 2014-2-22 创建@杨朔
 */
public class I18n {
	final static Logger log = Logger.getLogger(I18n.class);

	/** i18n国际化 */
	public static Map<String, String> i18n = new HashMap<String, String>();

	/**
	 * 
	 * @param fileName
	 */
	public static void init(String fileName) {
		PropertiesConfiguration configuration = new PropertiesConfiguration(fileName);
		i18n = configuration.map();
	}
}
