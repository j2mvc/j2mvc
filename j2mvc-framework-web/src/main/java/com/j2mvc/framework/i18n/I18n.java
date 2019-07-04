package com.j2mvc.framework.i18n;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;

import com.j2mvc.util.PropertiesConfiguration;

/**
 * 国际化配置
 * 
 * 2014-2-22 创建@杨朔
 */
public class I18n {
	static final Logger log = Logger.getLogger(I18n.class);

	/** i18n国际化 */
	public static Map<String,String> i18n = new HashMap<String,String>();
	
	/**
	 * 
	 * @param fileName
	 */
    public I18n(ServletContext context,String fileName) {
    	PropertiesConfiguration configuration = new PropertiesConfiguration(fileName);
    	Map<String, String> map = configuration.map();
    	i18n = map;
    	context.setAttribute("i18n", i18n);
	   	Set<Entry<String,String>> set = map.entrySet();
	   	Iterator<Entry<String,String>> iterator = set.iterator();
	   	while (iterator.hasNext()) {
	   		 Entry<String,String> entry = iterator.next();
	   		 context.setAttribute(entry.getKey(),entry.getValue());	
		}
    }
}
