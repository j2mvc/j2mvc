package com.j2mvc.framework.dispatcher;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.j2mvc.framework.action.ActionBean;
import com.j2mvc.framework.action.UploadBean;
import com.j2mvc.framework.dispatcher.reader.DefaultReader;
import com.j2mvc.framework.dispatcher.reader.FileReader;
import com.j2mvc.framework.dispatcher.reader.FormDataReader;
import com.j2mvc.framework.dispatcher.reader.JSONReader;
import com.j2mvc.framework.dispatcher.reader.XMLReader;
import com.j2mvc.framework.mapping.ContentType;
import com.j2mvc.framework.mapping.RequestMethod;
import com.j2mvc.framework.util.InvokeUtils;

/**  
 * 页面调配
 * @author 杨朔
 * @version 1.0 2014-2-23
 * @version 1.1.6 2014-8-17
 */
public class DispatcherForward {
	static final Logger  log = Logger.getLogger(DispatcherForward.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ActionBean bean;
	/**
	 * 构造器
	 * @param request
	 * @param response
	 * @param bean
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public DispatcherForward(HttpServletRequest request,
			HttpServletResponse response, 
			ActionBean bean) throws ServletException, IOException {
		super();
		this.request = request;
		this.response = response;
		this.bean = bean;
		init();
	}
	/**
	 * 初始化Action
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void init() throws ServletException, IOException{
		execute();
	}
	/**
	 * 执行方法
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void execute() throws ServletException, IOException{
		
		String className = bean.getClassName().indexOf(".")!=-1?bean.getClassName():
								bean.getPackageName() + "."+bean.getClassName();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.error("执行方法>>"+e.getMessage());
		}
		if(clazz!=null){
			try {
				Object obj = clazz.newInstance();
				InvokeUtils.invoke(clazz, "setRequest", obj, new Object[]{request},HttpServletRequest.class);
				InvokeUtils.invoke(clazz, "setResponse", obj,  new Object[]{response},HttpServletResponse.class);
				InvokeUtils.invoke(clazz, "setBean", obj,  new Object[]{bean},ActionBean.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"path",request.getContextPath()},String.class,Object.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"WEB_ROOT",request.getContextPath()},String.class,Object.class);
				InvokeUtils.invoke(clazz, "setAttribute", obj,  new Object[]{"PATH",request.getContextPath()},String.class,Object.class);
				// 执行当前Action方法
				Object result = InvokeUtils.invoke(clazz, "onStart", obj, null);
				if(result == null) {
					// 读取请求参数的数据
					// 按请求方式和数据类型分配读取方式
					Method method = bean.getMethod();
					String contentType = bean.getContentType();
					String requestMethod = request.getMethod();
					log.info("地址："+bean.getPath()+bean.getUri()+";请求数据格式:"+contentType+";请求方法："+requestMethod+".");
					
					if(requestMethod!=null && requestMethod.equalsIgnoreCase(RequestMethod.POST)) {
						contentType = contentType!=null && !contentType.trim().equals("")?contentType:ContentType.XWwwFormUrlencoded;
						if(contentType!=null && ContentType.FormData.equalsIgnoreCase(contentType)) {
							// multipart/form-data
							log.info(" read FormData.");
							result = new FormDataReader(request,method, obj).result();
						}else if(contentType!=null && ContentType.FILE.equalsIgnoreCase(contentType)) {
							// multipart/file
							log.info(" read File data.");
							FileReader reader  = new FileReader(request,method,obj,response,bean.getActionUpload());
							log.info("接收上传完毕.");
							InvokeUtils.invoke(clazz, "setUploadBean", obj,  new Object[]{reader.getUploadBean()},UploadBean.class);
							result = reader.result();
						}else if(contentType!=null && ContentType.XWwwFormUrlencoded.equalsIgnoreCase(contentType)) {
							// application/x-www-form-urlencoded
							log.info(" read XWwwFormUrlencoded, use DefaultReader.");
							result =  new DefaultReader(request,method, obj).result();
						}else if(contentType!=null && ContentType.JSON.equalsIgnoreCase(contentType)) {
							// application/json
							log.info(" read JSON.");
							JSONReader reader =  new JSONReader(request,method, obj);
							result = reader.result();
						}else if(contentType!=null && (
								ContentType.XML.equalsIgnoreCase(contentType)
								|| ContentType.XML_TEXT.equalsIgnoreCase(contentType))) {
							// text/xml
							// application/xml
							log.info(" read XML.");
							XMLReader reader = new XMLReader(request,method, obj);
							result = reader.result();
						}else {
							log.error("请求数据格式“"+contentType+"”不正确");
						}
					}else {
						// GET方法读取数据
						log.info(" read default.");
						result = new DefaultReader(request,method, obj).result();
					}
				}
				String file = result instanceof String?(String) result:null;
				forward(file);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 跳转页面
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void forward(String file) throws ServletException, IOException{
		if(file==null)
			return;
		file = file.startsWith("/")?file:bean.getDir()+file;
		if(file!=null && !response.isCommitted()){
			if(bean.isIncude()){
				log.info(file+" is includePage");
				request.getRequestDispatcher(file).include(request, response);
			}else{
				request.getRequestDispatcher(file).forward(request, response);
			}
			response.getWriter().flush();
		}
	}

}
