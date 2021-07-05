2.1.282.1.281、j2mvc项目简介
<pre>
基于JAVA开发的MVC框架，简单而强大，JAVA应用项目开发更轻松快捷。
</pre>

2、j2mvc-util
<pre>
丰富的JAVA工具包，J2MVC依赖工具包
</pre>

3、j2mvc-framework-app
<pre>
JAVA应用程序MVC框架，封装了数据库，及控制调用。
</pre>

4、j2mvc-framework-web
<pre>
基于servlet和filter的web框架，封装数据库连接，逻辑控制。
</pre>

5、j2mvc-framework-authorization
<pre>
依赖j2mvc-framework-web开发的权限控制模板，轻松创建强大的权限管理系统
</pre>

6、j2mvc-searcher
<pre>
基于lucene开发的搜索功能
</pre>

7、 maven依赖

		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-util</artifactId>
			<version>2.1.09</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-framework-web</artifactId>
			<version>2.1.28</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-authorization-distribute</artifactId>
			<version>2.1.06</version>
		</dependency> 

8、 创建java application项目

8.1、请参考示例j2mvc-example-app
<pre>
https://github.com/j2mvc/j2mvc-example/
https://github.com/j2mvc/j2mvc-example/tree/master/j2mvc-example-app
</pre>

8.2、 流程
<pre>
1、使用Eclipse创建Maven项目
2、添加Maven依赖
3、数据库｜表
4、应用配置
5、实体模型
6、应用逻辑
7、应用入口
8、发布
</pre>
9、创建java web项目

9.1、请参考示例j2mvc-example-web
<pre>
https://github.com/j2mvc/j2mvc-example/
https://github.com/j2mvc/j2mvc-example/tree/master/j2mvc-example-web
</pre>

9.2、流程
<pre>
1、使用Eclipse创建Maven项目
2、数据库｜表
3、网站配置
4、实体模型
5、服务逻辑
6、访问逻辑
7、jsp
8、运行
9、发布
10、注意
</pre>
	

10、 java web项目完整maven依赖
 
		<dependency>   
			<groupId>junit</groupId>         
			<artifactId>junit</artifactId>
			<version>4.11</version>   
			<scope>test</scope>    
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp.jstl</groupId>
			<artifactId>jstl-api</artifactId>
			<version>1.2</version>
			<exclusions>
				<exclusion>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
				</exclusion>
				<exclusion>
					<groupId>javax.servlet.jsp</groupId>
					<artifactId>jsp-api</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency>
			<groupId>org.glassfish.web</groupId>
			<artifactId>jstl-impl</artifactId>
			<version>1.2</version>
			<exclusions>
				<exclusion>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
				</exclusion>
				<exclusion>
					<groupId>javax.servlet.jsp</groupId>
					<artifactId>jsp-api</artifactId>
				</exclusion>
				<exclusion>
					<groupId>javax.servlet.jsp.jstl</groupId>
					<artifactId>jstl-api</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<!-- https://mvnrepository.com/artifact/eu.bitwalker/UserAgentUtils -->
		<dependency>
		    <groupId>eu.bitwalker</groupId>
		    <artifactId>UserAgentUtils</artifactId>
		    <version>1.21</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.58</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/javax.mail/mail -->
		<dependency>
			<groupId>javax.mail</groupId>
			<artifactId>mail</artifactId>
			<version>1.4.7</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/log4j/log4j -->
		<dependency>
		    <groupId>log4j</groupId>
		    <artifactId>log4j</artifactId>
		    <version>1.2.17</version>
		</dependency>

		<dependency>
			<groupId>com.belerweb</groupId>
			<artifactId>pinyin4j</artifactId>
			<version>2.5.0</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
		<dependency>
			<groupId>commons-io</groupId>
			<artifactId>commons-io</artifactId>
			<version>2.6</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload -->
		<dependency>
			<groupId>commons-fileupload</groupId>
			<artifactId>commons-fileupload</artifactId>
			<version>1.4</version>
		</dependency>
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.4</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.javassist/javassist -->
		<dependency>
			<groupId>org.javassist</groupId>
			<artifactId>javassist</artifactId>
			<version>3.25.0-GA</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.29</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
		<dependency>
			<groupId>org.jsoup</groupId>
			<artifactId>jsoup</artifactId>
			<version>1.12.1</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.antlr/antlr-runtime -->
		<dependency>
			<groupId>org.antlr</groupId>
			<artifactId>antlr-runtime</artifactId>
			<version>3.5.1</version>
		</dependency>

		<dependency>
			<groupId>org.netbeans.external</groupId>
			<artifactId>org-apache-commons-httpclient</artifactId>
			<version>RELEASE110</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.netbeans.external/org-apache-commons-logging -->
		<dependency>
			<groupId>org.netbeans.external</groupId>
			<artifactId>org-apache-commons-logging</artifactId>
			<version>RELEASE112</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/commons-codec/commons-codec -->
		<dependency>
			<groupId>commons-codec</groupId>
			<artifactId>commons-codec</artifactId>
			<version>1.13</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.0.1</version>
		</dependency>
		
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.10.7</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.10.7</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.10.7</version>
			<scope>runtime</scope>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
		<dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpclient</artifactId>
		    <version>4.5.12</version>
		</dependency>

		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-util</artifactId>
			<version>2.1.09</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-framework-web</artifactId>
			<version>2.1.28</version>
		</dependency> 
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-authorization-distribute</artifactId>
			<version>2.1.06</version>
		</dependency> 