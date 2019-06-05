package com.j2mvc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 系统配置
 * 2013/12/31@author 杨朔
 *
 */
public class PropertiesConfiguration {
	private static Logger logger = Logger.getLogger(PropertiesConfiguration.class.getCanonicalName());

    private Properties props = new Properties();
    private String configPath = "";

	/**
	 * 构造器
	 */
	public PropertiesConfiguration(String configPath) {
		this.configPath = configPath.startsWith("/") ? configPath : "/"+ configPath;
		InputStream is = null;
		File file = new File(this.configPath);
		if(!file.exists()){
			// 获取Jar包路径
			URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
			if(url!=null){
				String jarPath  = url.getPath();
				File jarFile = new File(jarPath);
				String source  = jarFile.getParent()+"/.."+this.configPath;
				file = new File(source);
				if(!file.exists()){
					source = jarFile.getParent()+"/conf"+this.configPath;
					file = new File(source);
				}
			}
		}
		if(file.exists()){
			logger.info("读取properties配置文件："+ this.configPath);
			try {
				is = new FileInputStream(this.configPath);
			} catch (FileNotFoundException e) {
				logger.warning("读取properties配置信息错误：" + e);
			}
		}
		if(is == null){
			is = getClass().getResourceAsStream(this.configPath);
		}
		if(is == null){
			logger.warning("读取properties配置信息错误：输入流为空。" );
			return;
		}
		try {
			props.load(is);
		} catch (FileNotFoundException e) {
			logger.warning("读取properties配置信息错误：" + e);
		} catch (Exception e) {
			logger.warning("读取properties配置信息错误：" + e);
		}
	}

	/**
	 * 构造器
	 */
	public PropertiesConfiguration(File file) {
		try {
			this.configPath = file.getAbsolutePath();
			InputStream in = new FileInputStream(file);
			props.load(in);
		} catch (FileNotFoundException e) {
			logger.warning("读取WEB配置方法信息错误" + e);
		} catch (IOException e) {
			logger.warning("读取WEB配置方法信息错误" + e);
		}
	}
   /**
    * 获取
    * @param key
    */
    public String get(String key){
        if(props.containsKey(key)){
            String value = props.getProperty(key);//得到某一属性的值
            return StringUtils.getUtf8(value);
        }
        else
            return "";
    }

    /**
     * 获取所有内容，并存入Map
     */
     public Map<String, String> map(){
    	 Set<Entry<Object,Object>> set = props.entrySet();
    	 Iterator<Entry<Object,Object>> iterator = set.iterator();
    	 HashMap<String, String> map = new HashMap<String, String>();
    	 while (iterator.hasNext()) {
    		 Entry<Object, Object> entry = iterator.next();
    		 map.put(entry.getKey().toString(), entry.getValue().toString());	
		}
    	 return map;
     }
   /**
    * 清空properties文件
    *
    */
    public void clear(){
    	props.clear();
    }
  
   /**
    * 设置
    * @param key
    * @param value
    */
    public void set(String key, String value){
    	props.setProperty(key, value);
    }

    /**
     * 保存properties文件
     */
     public void save(String description){
         try {
        	 String filepath = getClass().getResource(configPath).getPath();
        	 PrintStream out = new PrintStream(filepath);
        	 props.store(out, new String(description.getBytes("utf-8"),"iso-8859-1"));
        	 out.flush();
             out.close();
         } catch (FileNotFoundException e) {
        	 logger.warning("保存WEB配置方法信息错误"+e);
         } catch (IOException e){
        	 logger.warning("保存WEB配置方法信息错误"+e);
         }
     }
  
    /**
     * 保存properties文件
     */
     public void save(){
         try {
        	 String filepath = getClass().getResource(configPath).getPath();
        	 PrintStream out = new PrintStream(filepath);
        	 props.store(out, new String("".getBytes("utf-8"),"iso-8859-1"));
        	 out.flush();
             out.close();
         } catch (FileNotFoundException e) {
        	 logger.warning("保存WEB配置方法信息错误"+e);
         } catch (IOException e){
        	 logger.warning("保存WEB配置方法信息错误"+e);
         }
     }
  
   /**
    * 另存为properties文件
    * @param fileName
    * @param description
    */
    public void saveAsFile(String fileName, String description){
        try {
        	PrintStream out = new PrintStream(fileName);
        	props.store(out, new String(description.getBytes("utf-8"),"iso-8859-1"));
            out.close();
        } catch (FileNotFoundException e) {
       	 	logger.warning("保存WEB配置方法信息错误"+e);
        } catch (IOException e){
       	 	logger.warning("保存WEB配置方法信息错误"+e);
        }
    }
}
