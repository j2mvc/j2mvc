package com.j2mvc.framework.dao;

/**
 * 数据源信息
 * 
 * 2014-2-24 创建@杨朔
 */
public class DataSourceBean {
	private String name; 
	private String driverClassName; 
	private Integer maxIdle; 
	private Long maxWait; 
	private String username; 
	private String password; 
	private String url; 
	private Integer maxActive;
	private Integer initialSize;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public Integer getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}
	public Long getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(Long maxWait) {
		this.maxWait = maxWait;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	public Integer getInitialSize() {
		return initialSize;
	}
	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}
}
