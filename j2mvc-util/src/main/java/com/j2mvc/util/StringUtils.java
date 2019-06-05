package com.j2mvc.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
/**
 * 字符串工具类
 * 2014-4-4@杨大江
 */
public class StringUtils {

	/**
	 * 转换日期
	 * 
	 * @param s
	 * @return
	 */
	public static String dateToString(String s,SimpleDateFormat format) {
		s = s != null ? s.trim() : "";
		try{  
			return format.format(format.parse(s));
		}catch(Exception e){
			return "";
		}
	}

	/**
	 * 获取UTF8格式值
	 * @param value  
	 * @return 
	 */
	public static String getUtf8(String value){
		if(value == null)
			return "";
		try {
			if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value))
				value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
			if(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value))
				value = new String(value.getBytes("GBK"),"UTF-8");
			if(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(value))
				value = new String(value.getBytes("GB2312"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return value;
	}
	
	/**
	 * 删除重复
	 */
	public static String deleRepeat(String source,String regex){
		return source.replaceAll("["+regex+"]+", regex);
	}
	/**
	 * 截取HTML
	 * @param html
	 * @param length
	 * @return
	 */
	public static String subHtml(String html,int length){
        Document document = Jsoup.parse(html);
        String text = document.text();
        String dest = text.replaceAll("\\s*|\t|\r|\n","");
        if(dest.length()>length){
        	return dest.substring(0,length);
        }else{
        	return dest;
        }
	}
	/**
	 * 移出HTML标签
	 * @param html
	 * @return
	 */
	public static String removeHtmlTag(String html){
        Document document = Jsoup.parse(html);
        String text = document.text();
        String dest = text.replaceAll("\\s*|\t|\r|\n","");
        return dest;
	}
	public static boolean isEmpty(String source){
		if(source == null)
			return true;
		if(source.trim().equals(""))
			return true;
		return false;
	}
	
}
