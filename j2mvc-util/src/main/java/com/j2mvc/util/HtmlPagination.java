package com.j2mvc.util;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * HTML分页
 * @author Worda
 *
 */
public class HtmlPagination {

	/** 文档 */
	private Document document;
	/** 结果集 */
	List<Node> nodes;
	/** 分页对象 */
	Pagination pagination;
	/** 总数 */
	Integer total;
	/**
	 * HTML内容分页
	 * @param html html内容
	 * @param pageSize 每页显示条数
	 * @param p 当前页
	 */
	public HtmlPagination(String html,int pageSize,int p) {
		super();
		this.document = Jsoup.parse(html!=null?html:"");
		parse();
		pagination = new Pagination(total, pageSize, p);
	}
	/**
	 * HTML内容分页
	 * @param html html内容
	 * @param pageSize 每页显示条数
	 * @param p 当前页
	 * @param pageName 当前页参数名
	 */
	public HtmlPagination(String html,int pageSize,int p,String pageName) {
		super();
		this.document = Jsoup.parse(html!=null?html:"");
		parse();
		pagination = new Pagination(total, pageSize, p,pageName);
	}
	/**
	 * 解析HTML
	 */
	private void parse(){
		Elements elems = document.getElementsByTag("body");
		
		Element body = elems.first();
		
		nodes = body.childNodes();
		
		total = nodes.size();
		
	}
	
	public Pagination getPagination() {
		return pagination;
	}
	/** 
	 * 返回html
	 * @return
	 */
	public String html(){
		int startIndex = pagination.getStartIndex() > total ?total:pagination.getStartIndex();
		int endIndex = pagination.getEndIndex() > total ?total:pagination.getEndIndex();
		List<Node> list = nodes.subList(startIndex, endIndex);
		StringBuffer result = new StringBuffer();
		for(int i=0;i<list.size();i++){
			result.append(list.get(i).outerHtml());
		}
		return result.toString();
	}
}
