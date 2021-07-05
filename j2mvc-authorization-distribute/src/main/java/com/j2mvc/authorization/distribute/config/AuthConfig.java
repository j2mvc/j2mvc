package com.j2mvc.authorization.distribute.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.j2mvc.authorization.distribute.entity.Auth;
/**
 * 配置变量
 * 2014-4-12创建@杨朔
 */
public class AuthConfig {
	
	
	Logger log = Logger.getLogger(AuthConfig.class.getName());
	/** 
	 * 项目ID
	 */
	public static  String projectId = "master";
	/** 
	 * SESSION用户参数名
	 */
	public static  String sessionUserParamName = "sessionUser";
	/** 
	 * 令牌参数名
	 */
	public static  String tokenParamName = "token";
	/** 
	 * 客户端ID
	 */
	public static  String clientIdParamName = "clientId";
	/** 
	 * 客户端类型
	 */
	public static  String clientTypeParamName = "clientType";
	/** 
	 * SESSION系统管理员参数名
	 */
	public static  String sessionAdminParamName = "sessionAdmin";
	/**
	 * 用户登陆界面
	 */
	public static  String loginUserUri = "login";

	/**
	 * 权限开启状态
	 */
	public static boolean enable = true;
	/**
	 * 权限自动更新
	 */
	public static boolean enableUpdate = true;
	/**
	 * 权限开启自动更新
	 */
	public static boolean enablestatusUpdate = true;
	/** 
	 * 数据源名称 
	 */
	public static  String dataSourceName = "";
	
	public static Set<Auth> sysAuths = new HashSet<Auth>();

    private ServletContext context;
    private String config;

	/** 开启日志*/
	public static boolean authLog = false;
    
    public AuthConfig(ServletContext context,String config){
		this.context = context;
    	this.config = config;
    	loadDom();
    	
    }

    /**
     * 加载XML
     */
    private void loadDom() {
		try {
			InputStream inputStream = new FileInputStream(new File(context.getRealPath("/")+config));
	        // 解析XML
	        DocumentBuilder parser;
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        try {
	            parser = factory.newDocumentBuilder();
	            log.info("解析配置文件:"+config+"."); 
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
        	String value = node.getTextContent()!=null?node.getTextContent().trim():"";
        	value = value.replaceAll(" ","").replaceAll("\r","").replaceAll("\n","").replaceAll("\t","");
            if (node.getNodeName().equalsIgnoreCase("session-user-param-name")) {
            	log.info("权限 >> Session用户参数名:"+value);
            	sessionUserParamName = value;
            }else if (node.getNodeName().equalsIgnoreCase("token-param-name")) {
            	log.info("权限 >> 令牌参数名:"+value);
            	tokenParamName = value;
            }else if (node.getNodeName().equalsIgnoreCase("client-id-param-name")) {
            	log.info("权限 >> 客户端ID参数名:"+value);
            	clientIdParamName = value;
            }else if (node.getNodeName().equalsIgnoreCase("client-type-param-name")) {
            	log.info("权限 >> 客户端类型参数名:"+value);
            	clientTypeParamName = value;
            }else if (node.getNodeName().equalsIgnoreCase("project-id")) {
            	log.info("权限  >> 项目ID >> "+value);
            	projectId = value;
            }else if (node.getNodeName().equalsIgnoreCase("login-user-uri")) {
            	log.info("权限  >> Session用户登陆URI >> "+value);
            	loginUserUri = value;
            }else if (node.getNodeName().equalsIgnoreCase("dataSourceName")) {
            	log.info("权限  >> dataSourceName>> "+value);
            	dataSourceName = value;
            }
        }
    }
}
