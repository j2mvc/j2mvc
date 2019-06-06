package com.j2mvc.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import com.j2mvc.framework.Session;
import com.j2mvc.framework.dao.DataSourceBean;
import com.j2mvc.framework.i18n.I18n;
import com.j2mvc.util.OSType;
import com.j2mvc.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 配置XML解析器 @author 杨朔
 * 2013/1/2@创建
 * 2013/1/3@修改
 * 页面元素配置
 * <works> 	 
 *	<DataSources>
 *		<DataSource 
 *			name="test1" 
 *			driverClassName="com.mysql.jdbc.Driver" 
 *			username="test1"
 *			password="test1@123" 
 *			maxIdle="2"
 *			maxWait="5000" 
 *			maxActive="100"
 *			initialSize="1"
 *			url="jdbc:mysql://127.0.0.1/test1" />
 *		<DataSource 
 *			name="test2" 
 *			driverClassName="com.mysql.jdbc.Driver" 
 *			username="test2"
 *			password="test2@123" 
 *			maxIdle="2"
 *			maxWait="5000" 
 *			maxActive="100"
 *			initialSize="1"
 *			url="jdbc:mysql://127.0.0.1/test2" />	
 *	</DataSources>
 * </works>
 */
public class Config {
	static final Logger log = Logger.getLogger(Config.class.getName());
	
	static final String NODENAME_DATASOURCES = "DataSources";
	static final String NODENAME_DATASOURCE = "DataSource";
	static final String ATTR_NAME = "name";
	static final String ATTR_DRIVER_CLASS_NAME = "driverClassName";
	static final String ATTR_USERNAME = "username";
	static final String ATTR_PASSWORD = "password";
	static final String ATTR_MAX_WAIT = "maxWait";
	static final String ATTR_MAX_ACTIVE = "maxActive";
	static final String ATTR_INITIAL_SIZE = "initialSize";
	static final String ATTR_MAX_IDLE = "maxIdle";
	static final String ATTR_URL = "url";

	static final String NODENAME_INIT_PARAMS = "init-params";
	static final String NODENAME_INIT_PARAM = "init-param";
	static final String NODENAME_INIT_NAME = "name";
	static final String NODENAME_INIT_DESCRIPTION = "description";
	static final String NODENAME_INIT_VALUE = "value";
	
	static final String NODENAME_I18N = "i18n-default";
		
	static String configPath = "/conf/works.xml";
	static Element root;
	static Document doc;

	public static Map<String, String> props = new HashMap<String, String>();

	public static void init(String dir){
		if(StringUtils.isEmpty(dir)){
			
		}
		configPath = dir;
	}
	   /**
	    * 初始化
	    */
	public static void init(){
		InputStream is = null;
		String dir = "";
		if(OSType.OSinfo.isWindows())
			dir = System.getProperty("user.dir");
		else {
			URL url =  Config.class.getResource("/");
			if(url!=null){
				dir = url.getPath();
			}
		}
		String source = dir+"/.."+configPath;
		File file = new File(source);
		if(!file.exists()){
			// 获取Jar包路径
			URL url = Config.class.getProtectionDomain().getCodeSource().getLocation();
			if(url!=null){
				String jarPath  = url.getPath();
				File jarFile = new File(jarPath);
				source = jarFile.getParent()+"/.."+configPath;
				file = new File(source);
				if(!file.exists()){
					source = jarFile.getParent()+configPath;
					file = new File(source);
				}
			}
		}
		if(file.exists()){
			log.info(" init config >> "+source);
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				log.error(e.getMessage());
			}
		}
		if(is == null){
			is = Config.class.getResourceAsStream(configPath);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			root = doc.getDocumentElement();
			loadDataSource();
			loadDataSources();
			loadInitParams();
			loadI18n();
			PropsConfig.init();
//			DataSourceJndi.init();
//			DataSourceJndiMulti.init();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取数据源节点
	 * @param element
	 * 
	 */
	public static DataSourceBean getDataSource(Element element){
		if(element == null){
			return null;
		}
		String maxWaitStr = element.getAttribute(ATTR_MAX_WAIT);
		String maxIdleStr = element.getAttribute(ATTR_MAX_IDLE);
		String maxActiveStr = element.getAttribute(ATTR_MAX_ACTIVE);
		String initialStr = element.getAttribute(ATTR_INITIAL_SIZE);
		
		DataSourceBean dataSourceBean = new DataSourceBean();
		dataSourceBean.setDriverClassName(element.getAttribute(ATTR_DRIVER_CLASS_NAME));
		dataSourceBean.setMaxWait(maxWaitStr.matches("\\d+([L])?")?Long.valueOf(maxWaitStr):0);
		dataSourceBean.setMaxIdle(maxIdleStr.matches("\\d+")?Integer.valueOf(maxIdleStr):0);
		dataSourceBean.setMaxActive(maxActiveStr.matches("\\d+")?Integer.valueOf(maxActiveStr):0);
		dataSourceBean.setInitialSize(initialStr.matches("\\d+")?Integer.valueOf(initialStr):0);
		dataSourceBean.setName(element.getAttribute(ATTR_NAME));
		dataSourceBean.setPassword(element.getAttribute(ATTR_PASSWORD));
		dataSourceBean.setUrl(element.getAttribute(ATTR_URL));
		dataSourceBean.setUsername(element.getAttribute(ATTR_USERNAME));
		
		return dataSourceBean;
	}

	/**
	 * 加载数据源集合
	 * 
	 */
	static void loadDataSources(){
		NodeList nodes = root.getElementsByTagName(NODENAME_DATASOURCES);
		Element node = nodes.getLength()>0?(Element)nodes.item(0):null;
		if(node!=null){
			NodeList children = node.getElementsByTagName(NODENAME_DATASOURCE);
			for(int i=0;i<children.getLength();i++){
				Element element = (Element)children.item(i);
				DataSourceBean dataSourceBean = getDataSource(element);
				if(dataSourceBean!=null)
					Session.dataSourceBeanMap.put(dataSourceBean.getName(),dataSourceBean);
			}
		}
	}

	/**
	 * 加载第一个数据源
	 * 
	 */
	static void loadDataSource(){
		NodeList nodes = root.getElementsByTagName(NODENAME_DATASOURCE);
		Element node = nodes.getLength()>0?(Element)nodes.item(0):null;
		Session.dataSourceBean = getDataSource(node);
	}


	/**
	 * 加载语言设置
	 * 
	 */
	static void loadI18n(){
		// 获取城市下小区节点
		NodeList nodes = root.getElementsByTagName(NODENAME_I18N);
		Element node = nodes.getLength()>0?(Element)nodes.item(0):null;
		if(node==null){
			log.error("i18n未设置。");
		}else{
			String value = getNodeValue(node);
	        if(!value.equals(""))
	        	I18n.init("/conf/i18n/"+value+".properties");
	        else{
	        	log.error("i18n配置内容为空");
	        }
		}
	}
	/**
	 * 加载其他设置
	 * 
	 */
	static void loadInitParams(){
		// 获取城市下小区节点
		NodeList nodes = root.getElementsByTagName(NODENAME_INIT_PARAMS);
		Element node = nodes.getLength()>0?(Element)nodes.item(0):null;
		NodeList paramNodes = node.getElementsByTagName(NODENAME_INIT_PARAM);
		// 遍历页面
		for(int i=0;i<paramNodes.getLength();i++){
			Node node2 = paramNodes.item(i);
			NodeList chidren = node2.getChildNodes();
			InitParam initParam = new InitParam();
			for (int j = 0; j < chidren.getLength(); j++) {
				Node child = chidren.item(j);
				String name = child.getNodeName();
				String value = getNodeValue(child);
				if(name.equals(NODENAME_INIT_NAME)){
					initParam.setName(value);
				}else if(name.equals(NODENAME_INIT_DESCRIPTION)){
					initParam.setDescription(value);
				}else if(name.equals(NODENAME_INIT_VALUE)){
					initParam.setValue(value);					
				}
			}
			String initName = initParam.getName();
			if(initName!=null && !initName.equals("")){
				try {
					Session.sqlLog = Boolean.valueOf(initParam.getValue());
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	static String getNodeValue(Node node){
		String value = node.getTextContent();
		value = value!=null?value.replaceAll(" ","")
				.replaceAll("\r","").replaceAll("\n","").replaceAll("\t",""):"";
		value = StringUtils.getUtf8(value);
		return value!=null?value.trim():"";
	}
	
	static class InitParam{
		String name;
		String description;
		String value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}