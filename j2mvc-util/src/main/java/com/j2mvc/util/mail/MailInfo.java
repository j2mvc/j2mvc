package com.j2mvc.util.mail;

/**
 * 邮件信息
 * @version 1.1@date2014-9-2
 */
public class MailInfo {

	/** 主题 */
	private String subject;
	/** 内容 */
	private String body;
	/** 多个接收地址 */
	private String [] receivers;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String[] getReceivers() {
		return receivers;
	}
	public void setReceivers(String[] receivers) {
		this.receivers = receivers;
	}
}
