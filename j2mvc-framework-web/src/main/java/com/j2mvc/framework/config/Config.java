package com.j2mvc.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.j2mvc.framework.Session;
import com.j2mvc.framework.dao.DataSourceBean;
import com.j2mvc.framework.dao.DataSourceJndi;
import com.j2mvc.framework.i18n.I18n;
import com.j2mvc.framework.interceptor.DispatcherInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

/**
 * 配置
 * 
 * 2014-2-22 创建@杨朔
 */
public class Config {
	static final Logger log = Logger.getLogger(Config.class);


    private ServletContext context;
    /** 主配置文件名 */
    private String fileName;
    /**  properties 配置文件键值集合 */
	public static Map<String, Map<String, String>> props = new HashMap<String, Map<String, String>>();
    /**  properties 配置文件键值集合 */
	public static Map<String, String> attributes = new HashMap<String, String>();

    /**
     * 加载XML配置文件构造器
     * @param context
     * @param fileName
     */
    public Config(ServletContext context,String fileName) {
    	this.context = context;
    	this.fileName = fileName;
        // Properties配置导入
    	PropsConfig.init(context);
    	loadDom();
    	// 绑定数据源
    	DataSourceJndi.init();
    }

    /**
     * 加载XML
     * @param inputStream
     */
    private void loadDom() {
		try {
			InputStream inputStream = new FileInputStream(new File(context.getRealPath("/")+fileName));
	        // 解析XML
	        DocumentBuilder parser;
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        try {
	            parser = factory.newDocumentBuilder();
	            log.info("解析配置文件:"+fileName+"."); 
	            Document doc = parser.parse(inputStream);
	            log.info("解析完成，读取文档Document."); 
	            processConfDoc(doc); 
	        } catch (ParserConfigurationException e) {
	            e.printStackTrace();
	        }catch (SAXParseException e) { 
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }

    /**
     * 解析XML
     */
    private void processConfDoc(Document doc) {
        Element rootElement = doc.getDocumentElement();

        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
        	Node node = nodeList.item(i);
        	// 初始化Actions包节点
            if (node.getNodeName().equals("actions-packages")) {
                log.info("读取Action包节点:"+node.getNodeName()+"."); 
            	initActionsPackages(node);
            }
        	// 初始化拦截器
            if (node.getNodeName().equals("interceptors")) {
                log.info("读取拦截器interceptors节点:"+node.getNodeName()+"."); 
            	initInterceptors((Element)node);
            }
        	// 初始化数据源
            if (node.getNodeName().equals("DataSource")) {
                log.info("读取数据源DataSource节点:"+node.getNodeName()+"."); 
            	initDataSource((Element)node);
            }
        	// 默认语种
            if (node.getNodeName().equals("i18n-default")) {
                log.info("读取默认语种i18n-default节点:"+node.getNodeName()+"."); 
            	String value = node.getTextContent()!=null?node.getTextContent().trim():"";
            	value = value.replaceAll(" ","").replaceAll("\r","").replaceAll("\n","").replaceAll("\t","");
            	value = getCharset(value);
            	if(!value.equals(""))
            		new I18n(context,"/conf/i18n/"+value+".properties");
            }
        }
    }
    /**  
     * 初始化拦截器
     * @param element
     */
    private void initInterceptors(Element parentElement) {
        NodeList nodeList = parentElement.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
        	Node node = nodeList.item(i);
            if (node.getNodeName().equals("interceptor")) {
            	Element element = (Element)node;
            	DispatcherInterceptor interceptor = new DispatcherInterceptor();
            	interceptor.setContext(context);
            	String ref = get(element,"ref");
            	interceptor.setUrlPattern(get(element,"url-pattern"));
            	Class<?> refClass;
				try {
					refClass = Class.forName(ref);
	            	interceptor.setRefClass(refClass);
	                Session.interceptors.add(interceptor);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
            }
        }
	}
    
    /**
     * 初始化数据源
     * @param element
     */
    private void initDataSource(Element element){
    	String maxActive = get(element,"maxActive").trim();
    	String maxIdle = get(element,"maxIdle").trim();
    	String maxWait = get(element,"maxWait").trim();
    	String initialSize = get(element,"initialSize").trim();
    	String name = get(element,"name");
    	String validationQuery = get(element,"validationQuery");
    	DataSourceBean dataSourceBean = new DataSourceBean();
    	dataSourceBean.setDriverClassName(get(element,"driverClassName"));
    	dataSourceBean.setMaxActive(maxActive.matches("\\d+")?Integer.valueOf(maxActive):0);
    	dataSourceBean.setMaxIdle(maxIdle.matches("\\d+")?Integer.valueOf(maxIdle):0);
    	dataSourceBean.setMaxWait(maxWait.matches("\\d+")?Long.valueOf(maxWait):0);
    	dataSourceBean.setInitialSize(initialSize.matches("\\d+")?Integer.valueOf(initialSize):0);
    	dataSourceBean.setName(name);
    	dataSourceBean.setPassword(get(element,"password"));
    	dataSourceBean.setUrl(get(element,"url"));
    	dataSourceBean.setUsername(get(element,"username"));
    	if(validationQuery!=null)
    		dataSourceBean.setValidationQuery(validationQuery);
    	Session.dataSourceBean = dataSourceBean;
    	Session.dataSourceBeanMap.put(name,dataSourceBean);
    }
    
    /**
     * 初始化ActionsPackages
     * @param element
     */
    private void initActionsPackages(Node node){
    	String mValue = node.getTextContent()!=null?node.getTextContent().trim():"";
    	mValue = mValue.replaceAll(" ","").replaceAll("\r","").replaceAll("\n","").replaceAll("\t","");
    	String[] args = mValue.split(","); 
    	Set<String> set = new HashSet<String>();
    	for(int i=0;i<args.length;i++){
    		set.add(args[i]);
    	}
		if(Session.uriLog && set.size()<1)
			log.error("init Config actions 包名未设置.");
		Session.actionsPackages = set.toArray(new String[]{});
    }
    
    private String get(Element element,String name){
    	String text = element.getAttribute(name);
    	return text!=null?text.trim():"";
    }


	/**
	 * 获取默认编码格式值
	 * @param value  
	 */
	public static String getCharset(String value){
		if(value == null)  
			return "";
		try {
			if(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(value))
				value = new String(value.getBytes("ISO-8859-1"),Session.encoding);
			if(java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(value))
				value = new String(value.getBytes("UTF-8"),Session.encoding);
			if(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value))
				value = new String(value.getBytes("GBK"),Session.encoding);
			if(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(value))
				value = new String(value.getBytes("GB2312"),Session.encoding);
		} catch (UnsupportedEncodingException e) {
		}
		return value;
	}
}
