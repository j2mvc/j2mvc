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

# 快速开始创建一个WEB项目
# 1；使用Eclipse创建Maven项目
	File => new => project =>  Maven => Maven Project
	在Select an Archetype选择
    
# 2：引入框架运行依赖包
	<dependencies>
		<dependency>
			<groupId>org.json</groupId>
			<artifactId>json</artifactId>
			<version>20180813</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.jsoup</groupId>
			<artifactId>jsoup</artifactId>
			<version>1.12.1</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.mail</groupId>
			<artifactId>mail</artifactId>
			<version>1.4.7</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.16</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.0.1</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>com.belerweb</groupId>
			<artifactId>pinyin4j</artifactId>
			<version>2.5.0</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>javassist</groupId>
			<artifactId>javassist</artifactId>
			<version>3.16.1</version>
			<classifier>ga</classifier>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool</artifactId>
			<version>1.3</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-codec</artifactId>
			<version>1.3</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.3</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>com.mysql.jdbc</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.30</version>
			<classifier>bin</classifier>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>antlr</groupId>
			<artifactId>antlr</artifactId>
			<version>3.0.1</version>
			<classifier>antlr</classifier>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>antlr.runtime</groupId>
			<artifactId>antlr-runtime</artifactId>
			<version>3.0.1</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>xssprotect</groupId>
			<artifactId>xssprotect</artifactId>
			<version>0.1</version>
			<scope>provided</scope>
		</dependency>

	</dependencies>



