package com.j2mvc.searcher;

import java.text.SimpleDateFormat;

/**
 * 搜索配置<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔
 * 2015年1月21日
 */
public class SearchConfiguration {

	/** 
	 * 分词器配置文件路径
	 */	
	public static String ANALYZER_XML = "conf/analyzer.cfg.xml";
	/**
	 * 配置属性——扩展字典
	 */
	public static String EXT_DICT = "conf/ext_dict";
	/**
	 * 配置属性——扩展停止词典
	 */
	public static String EXT_STOP = "conf/ext_stopwords";
	
	/** 
	 * 默认日期格式
	 */
	public static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
}
