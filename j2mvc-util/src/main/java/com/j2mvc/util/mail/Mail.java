package com.j2mvc.util.mail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 邮件管理
 * @version 1.1@date2014-9-2
 */
public class Mail {
	
	String from;
	boolean auth;
	String name;
	Session session;
	String contentType= "text/html; charset=utf-8";
	
	

	public Mail(String from, String hostname, int port, boolean auth,
			String name, String username, String password) {
		super();
		this.from = from;
		this.auth = auth;
		this.name = name;

		Properties props = new Properties(); 
		props.put("mail.smtp.host", hostname);   
		props.put("mail.smtp.port", port);   
		props.put("mail.smtp.auth", auth);   
		session = Session.getInstance(props, new MailAuthenticator(username,password));
	}



	/**
	 * 发送邮件
	 * @param info
	 * @throws UnsupportedEncodingException 
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean sendMail(MailInfo info) throws UnsupportedEncodingException{
		try {
			// 根据session创建一个邮件消息
			Message message = new MimeMessage(session);
			// 创建邮件发送者地址
			InternetAddress address = new InternetAddress(from);
			address.setPersonal(MimeUtility.encodeText(name + "<" + from + ">"));
			// 设置邮件消息的发送者
			message.setFrom(address);
			// 创建邮件的接收者地址，并设置到邮件消息中
			String[] receivers = info.getReceivers();
			Address[] recipients = new InternetAddress[receivers.length];
			for (int i = 0; i < receivers.length; i++) {
				recipients[i] = new InternetAddress(receivers[i]);
			}
			// Message.RecipientType.TO属性表示接收者的类型为TO
			message.setRecipients(Message.RecipientType.TO, recipients);
			// 设置邮件消息的主题
			message.setSubject(info.getSubject());
			// 设置邮件消息发送的时间
			message.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart body = new MimeBodyPart();
			// 设置HTML内容
			body.setContent(info.getBody(),contentType);
			mainPart.addBodyPart(body);
			// 将MiniMultipart对象设置为邮件内容
			message.setContent(mainPart);
			// 发送邮件
			Transport.send(message);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
			return false;
		}
//	      
//		Message message = new MimeMessage(session);
//		InternetAddress from = new InternetAddress(senderMail);
//		from.setPersonal(MimeUtility.encodeText(senderName + "<" + senderMail + ">"));
//		message.setFrom(from);
//		InternetAddress to = new InternetAddress(info.getAddresses()[0]);
//		message.setRecipient(Message.RecipientType.TO, to);
//		message.setSubject(MimeUtility.encodeText(info.getSubject()));
//		message.setText(info.getBody());
//		message.setSentDate(new Date());
//		
//		// 邮件对象
//		File file = new File("/works/tmp/textmail.eml");
//		// 获得输出流
//		OutputStream ips = new FileOutputStream(file);
//		// 把邮件内容写入到文件
//		message.writeTo(ips);
//		// 关闭流
//		ips.close();
	}
}
