1、j2mvc项目简介
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
			<version>2.1.01</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-framework-web</artifactId>
			<version>2.1.01</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-framework-app</artifactId>
			<version>2.1.01</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-authorization</artifactId>
			<version>2.1.01</version>
		</dependency>
		<dependency>
			<groupId>com.j2mvc</groupId>
			<artifactId>j2mvc-searcher</artifactId>
			<version>2.1.01</version>
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
	


