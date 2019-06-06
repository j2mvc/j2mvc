# j2mvc项目简介
基于JAVA开发的MVC框架，简单而强大，JAVA应用项目开发更轻松快捷。

# j2mvc-util
丰富的JAVA工具包，J2MVC依赖工具包

# j2mvc-framework-app
JAVA应用程序MVC框架，封装了数据库，及控制调用。

# j2mvc-framework-web
基于servlet和filter的web框架，封装数据库连接，逻辑控制。

# j2mvc-framework-authorization
依赖j2mvc-framework-web开发的权限控制模板，轻松创建强大的权限管理系统

# j2mvc-searcher
基于lucene开发的搜索功能


# 创建java application项目，请参考示例j2mvc-example-app
# 1:使用Eclipse创建Maven项目
	File => new => project =>  Maven => Maven Project
	在Select an Archetype选择maven-archetype-quickstart
# 2、添加Maven依赖
# 3、数据库｜表

# 4、应用配置
# 5、实体模型
# 6、应用逻辑
# 7、应用入口
# 8、发布

# 创建java web项目，请参考示例j2mvc-example-web
# 1、使用Eclipse创建Maven项目
	File => new => project =>  Maven => Maven Project
	在Select an Archetype选择maven-archetype-webapp
# 2、数据库｜表
create database j2mvc_example;

grant all privileges on j2mvc_example.* to exmaple@127.0.0.1 identified by 'exmaplepassword' WITH GRANT OPTION;
flush privileges;

use j2mvc_example;
 
-- 用户
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
	id					varchar(32)	NOT NULL,
	username			varchar(255)	NOT NULL COMMENT '用户名',			
	password			varchar(255)	NOT NULL COMMENT '密码',				
  	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 购物车
DROP TABLE IF EXISTS products;
CREATE TABLE IF NOT EXISTS products(
	id					varchar(32)		NOT NULL,
	title				varchar(255)		NOT NULL COMMENT '商品标题',			
	detail				varchar(255)	NOT NULL COMMENT '商品详情',				
	price				double(11,2)	NOT NULL COMMENT '价格',		
	stock				int(11)			NOT NULL COMMENT '库存',
  	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 购物车
DROP TABLE IF EXISTS carts;
CREATE TABLE IF NOT EXISTS carts(
	id					varchar(32)	NOT NULL,
	user_id				varchar(255)	NOT NULL COMMENT '用户ID',			
	product_id			varchar(255)	NOT NULL COMMENT '商品ID',				
	num					int(11)	NOT NULL COMMENT '数量',				
  	PRIMARY KEY (id),
  	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# 3、网站配置
# /WEB-INF/web.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="WebApp_ID" version="2.5">
  <display-name>minmeng-web</display-name>

	<listener>
		<listener-class>org.fixwork.framework.FixworkListener</listener-class>
	</listener>
	<context-param>
		<description>配置文件</description>
		<param-name>works</param-name>
		<param-value>/WEB-INF/works.xml</param-value>
	</context-param>	  
	<context-param>
		<description>输入SQL日志</description>
		<param-name>sqlLog</param-name>
		<param-value>false</param-value>
	</context-param>
	<context-param>
		<description>输入uriLog日志</description>
		<param-name>uriLog</param-name>
		<param-value>false</param-value>
	</context-param>
	
	<filter>
		<display-name>DispatcherFilter</display-name>
		<filter-name>DispatcherFilter</filter-name>
		<filter-class>org.fixwork.framework.dispatcher.DispatcherFilter</filter-class>
		<init-param>
			<description>拦截的URI后缀</description>
			<param-name>subfixes</param-name>
			<param-value>.jsp,.do</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>DispatcherFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping> 
	
	<error-page>
		<error-code>404</error-code>
		<location>/404.jsp</location>
	</error-page>
	<error-page>
		<error-code>500</error-code>
		<location>/500.jsp</location>
	</error-page>
		
</web-app>

# 4、实体模型
import com.j2mvc.util.mapping.DataSourceName;
import com.j2mvc.util.mapping.NotJSONField;
import com.j2mvc.util.mapping.PrimaryKey;
import com.j2mvc.util.mapping.Table;
import com.j2mvc.util.mapping.Column;
/**
 * 用户实体类
 * @author yangshuo
 * 如果只有一个数据源，可以不设置DataSourceName
 */
@DataSourceName("jdbc/j2mvcexample")
@Table("users")
@PrimaryKey(autoIncrement=false)
public class User {
	/** id */
	@Column(name = "id",length = 32)
	private String id;
	...
}

# 5、服务逻辑
import java.util.List;
import com.j2mvc.example.web.entity.Cart;
import com.j2mvc.framework.dao.DaoSupport;
public class CartService {
	DaoSupport dao = new DaoSupport(Cart.class);
	/**
	 * 插入
	 * @param cart
	 * @return Cart
	 */
	public Cart insert(Cart cart) {		
		return (Cart) dao.insert(cart);
	}
}
# 6、访问逻辑
import com.j2mvc.util.mapping.ActionPath;
import com.j2mvc.util.mapping.ActionUri;
@ActionPath(path="/",dir="/WEB-INF/jsp/")
public class BaseAction extends Action{

	@ActionUri(uri="items([/])?")
	public String index(Integer page){
	...
		return "index.jsp";
	}
}
# 7、jsp
	在/WEB-INF/jsp/或自己定义的其它目录下编写jsp文件。
# 8、运行
	直接运行到tomcat。
# 9、发布
	mvn clean install
	打包目录/target。将生成war文件或发布目录，上传至服务器。
	<font color=red>
	注意
	若打包后的action类，无法获取到参数，
	先clean工程项目，将target下classes目录上传至服务器覆盖原有classes目录
	</font>
	


