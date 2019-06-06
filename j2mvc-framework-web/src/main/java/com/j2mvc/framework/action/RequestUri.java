package com.j2mvc.framework.action;

/**
 * Action 请求地址
 * @author 杨朔
 * @version 1.0 2014-2-23
 * @version 1.1.6 2014-8-17
 * @version 1.2.2 2014-12-21
 */
public class RequestUri {

	private String [] values;

	public RequestUri() {
		super();
	}

	public RequestUri(String[] values) {
		super();
		this.values = values;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
	
}
