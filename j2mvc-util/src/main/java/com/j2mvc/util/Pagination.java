package com.j2mvc.util;

import com.j2mvc.util.Pagination;
import com.j2mvc.util.json.JSONFactory;

/**
 * 计算分页 根据总条数据，每页显示条数，当前页数计算上一条或下一条数数字
 * 
 * @author 杨朔
 * 
 */
public class Pagination {
	/** 默认每页显示11个页号 */
	public static final int DEFAULT_MAXNUM = 11;
	/** 默认页号参数名 */
	private String pageName = "page";

	private Integer total, pageSize, page,pageTotal, startIndex, endIndex;
	private String html;

	/**
	 * 构造器
	 * 
	 * @param total 总数
	 * @param pageSize 每页显示条数
	 * @param page 当前页号
	 */
	public Pagination(Integer total, Integer pageSize, Integer page) {
		super();
		this.total = total!=null?total:0;
		this.pageSize = pageSize!=null?pageSize:0;
		this.page = page!=null?page:0;
		cal();
	}
	/**
	 * 构造器
	 * 
	 * @param total 总数
	 * @param pageSize 每页显示条数
	 * @param page 当前页号
	 * @param pageName 页号参数名，默认为“page”
	 */
	public Pagination(Integer total, Integer pageSize, Integer page,String pageName) {
		super();
		this.total = total!=null?total:0;
		this.pageSize = pageSize!=null?pageSize:0;
		this.page = page!=null?page:0;
		this.pageName = pageName !=null ?pageName:this.pageName;
		cal();
	}

	/**
	 * 计算
	 */
	private void cal() {
		// 总页数
		this.pageTotal = ((total+0.0f)/(pageSize + 0.0f))>total/pageSize?(total/pageSize+1): total/pageSize;
		// 设置起始条数
		this.startIndex = page > 1 ? page * pageSize - pageSize : 0;
		// 结束条数
		this.endIndex = startIndex + pageSize;
		this.endIndex = (endIndex > total) ? total : endIndex;
	}

	/**
	 * 返回HTML
	 */
	public String getHtml() {
		html = getHtml("", DEFAULT_MAXNUM,false);
		return html;
	}

	/**
	 * 返回HTML
	 * @param showForce 强制显示分页
	 */
	public String getHtml(boolean showForce) {
		html = getHtml("", DEFAULT_MAXNUM,showForce);
		return html;
	}
	/**
	 * 返回HTML
	 * 
	 * @param maxNum
	 *            每页显示的最大页数，如每页最大显示5页，最好为基数，如果为偶数，会自动加1。
	 */
	public String getHtml(int maxNum) {
		return getHtml("", maxNum,false);
	}
	/**
	 * 返回HTML
	 * 
	 * @param maxNum
	 *            每页显示的最大页数，如每页最大显示5页，最好为基数，如果为偶数，会自动加1。
	 * @param showForce 强制显示分页
	 */
	public String getHtml(int maxNum,boolean showForce) {
		return getHtml("", maxNum,showForce);
	}

	/**
	 * 返回HTML
	 * @param url 访问页URL
	 */
	public String getHtml(String url) {
		return getHtml("", DEFAULT_MAXNUM,false);
	}

	/**
	 * 返回HTML
	 * @param url 访问页URL
	 * @param showForce 强制显示分页
	 */
	public String getHtml(String url,boolean showForce) {
		return getHtml("", DEFAULT_MAXNUM,showForce);
	}

	/**
	 * 返回HTML
	 * 
	 * @param url 访问页URL
	 * @param maxNum 该参数值表示每页显示的最大页号条数，
	 */
	public String getHtml(String url, int maxNum) {
		return getHtml(url,maxNum,false);
	}
	/**
	 * 返回HTML
	 * 
	 * @param url 访问页URL
	 * @param maxNum 该参数值表示每页显示的最大页号条数，
	 */
	public String getHtml(String url, int maxNum,boolean showForce) {
		if (pageTotal < 2 && !showForce)
			// 小于2页，返回空内容
			return "";
		url = url != null ? url : "";
		url = url.indexOf("?") != -1 && !url.endsWith("?") ? url + "&" : url
				+ "?";
		String html = "<div class='page'>" 
				+ "<div>" 
				+ "<ul>";
		html += "<li class='text'>共" + total + "条," + page + "/" + pageTotal+ "页.</li>";

		int startpage = 1;
		maxNum = maxNum % 2 == 0 ? maxNum + 1 : maxNum;
		int endpage = maxNum;
		int showNum = maxNum;
		int halfNum = (maxNum - 1) / 2; // 一半页数

		// stepNum:当前页前页数，或当前页后页数
		// 当前页号 - stepNum 大于1，开始页号为page-halfNum
		if (page - halfNum > 1) {
			startpage = page - halfNum;
		} else {
			startpage = 1;
		}
		// 当前页号 + stepNum 小于总页数，结束页号为page+halfNum
		if (page + halfNum < pageTotal) {
			endpage = page + halfNum;
		} else {
			endpage = pageTotal;
		}
		// 开始页号大于总页数-showNum,且总页数大于showNum，开始页号为总页-showNum
		startpage = startpage > pageTotal - showNum && pageTotal > showNum ? pageTotal
				- showNum
				: startpage;
		// 结束页号小于showNum（每页页数）且小于总页数，结束页号showNum
		endpage = endpage < showNum && endpage < pageTotal ? showNum : endpage;
		endpage = endpage > pageTotal?pageTotal:endpage;

		if (page > 1) {
			html += "<li class='p'>" 
					+ "<a href='"+url+pageName+"=1'>首页</a>"
					+ "</li>" 
					+ "<li class='p'>" 
					+ "<a href='"+ url+pageName+"="+(page-1)+"'>上一页</a>" 
					+ "</li>";
		}
		if (startpage > 1) {
			html += "<li class='p'><a href='"+url+pageName+"=1'>1</a></li>...";
		}
		if (pageTotal > 1)
			for (int i = startpage; i <= endpage; i++) {
				html += "<li " + (i == page ? "class='current'" : "") + ">"
						+ "<a "+ (i == page ? "" : "href='" + url + pageName+"=" + i + "'")+ ">" + i + "</a>" 
						+ "</li>";
			}
		if (endpage > 1 && endpage < pageTotal) {
			html += "..."
					+ "<li class='p'>"
					+ "<a href='" + url + pageName+"=" + (pageTotal)+ "'>" + pageTotal + "</a>"
					+ "</li>";
		}
		if (page < pageTotal) {
			html += "<li class='p'>" 
					+ "<a href='" + url + pageName+"=" + (page + 1)+ "'>下一页</a>" 
					+ "</li>" 
					+ "<li class='p'>" 
					+ "<a href='"+ url + pageName+"=" + (pageTotal) + "'>尾页</a>" 
					+ "</li>";
		}
		html += "</ul>"
				+"</div>"
				+"</div>";
		return html;
	}

	/**
	 * 返回HTML
	 * 
	 * @param url 访问页URL
	 * @param maxNum 该参数值表示每页显示的最大页号条数，
	 */
	public String getHtmlOnlyPage(String url, int maxNum) {

		if (pageTotal < 2)
			// 小于2页，返回空内容
			return "";
		url = url != null ? url : "";
		url = url.indexOf("?") != -1 && !url.endsWith("?") ? url + "&" : url
				+ "?";
		String html = "<div class='page'>" 
				+ "<div>" 
				+ "<ul>";
		html += "<li class='text'>" + page + "/" + pageTotal+ ".</li>";

		int startpage = 1;
		maxNum = maxNum % 2 == 0 ? maxNum + 1 : maxNum;
		int endpage = maxNum;
		int showNum = maxNum;
		int halfNum = (maxNum - 1) / 2; // 一半页数

		// stepNum:当前页前页数，或当前页后页数
		// 当前页号 - stepNum 大于1，开始页号为page-halfNum
		if (page - halfNum > 1) {
			startpage = page - halfNum;
		} else {
			startpage = 1;
		}
		// 当前页号 + stepNum 小于总页数，结束页号为page+halfNum
		if (page + halfNum < pageTotal) {
			endpage = page + halfNum;
		} else {
			endpage = pageTotal;
		}
		// 开始页号大于总页数-showNum,且总页数大于showNum，开始页号为总页-showNum
		startpage = startpage > pageTotal - showNum && pageTotal > showNum ? pageTotal
				- showNum
				: startpage;
		// 结束页号小于showNum（每页页数）且小于总页数，结束页号showNum
		endpage = endpage < showNum && endpage < pageTotal ? showNum : endpage;
		endpage = endpage > pageTotal?pageTotal:endpage;

		if (startpage > 1) {
			html += "<li class='p'><a href='"+url+pageName+"=1'>1</a></li>...";
		}
		if (pageTotal > 1){
			for (int i = startpage; i <= endpage; i++) {
				html += "<li " + (i == page ? "class='current'" : "") + ">"
						+ "<a "+ (i == page ? "" : "href='" + url + pageName+"=" + i + "'")+ ">" + i + "</a>" 
						+ "</li>";
			}
		}
		if (endpage > 1 && endpage < pageTotal) {
				html = html + "..."
						+ "<li class='p'>"
						+ "<a href='" + url + pageName+"=" + pageTotal+ "'>" + pageTotal + "</a>"
						+ "</li>";
		}
		html += "</ul>"
				+"</div>"
				+"</div>";
		return html;
	}

	/**
	 * 返回HTML
	 * 
	 * @param url 访问页URL
	 * @param maxNum 该参数值表示每页显示的最大页号条数，
	 */
	public String getBootstrapHtml(String url, int maxNum,boolean showForce) {
		if (pageTotal < 2 && !showForce)
			// 小于2页，返回空内容
			return "";
		url = url != null ? url : "";
		url = url.indexOf("?") != -1 && !url.endsWith("?") ? url + "&" : url
				+ "?";
		String html = "<nav aria-label=\"Page navigation\">" 
				+ "<ul class=\"pagination\">";

		int startpage = 1;
		maxNum = maxNum % 2 == 0 ? maxNum + 1 : maxNum;
		int endpage = maxNum;
		int showNum = maxNum;
		int halfNum = (maxNum - 1) / 2; // 一半页数

		// stepNum:当前页前页数，或当前页后页数
		// 当前页号 - stepNum 大于1，开始页号为page-halfNum
		if (page - halfNum > 1) {
			startpage = page - halfNum;
		} else {
			startpage = 1;
		}
		// 当前页号 + stepNum 小于总页数，结束页号为page+halfNum
		if (page + halfNum < pageTotal) {
			endpage = page + halfNum;
		} else {
			endpage = pageTotal;
		}
		// 开始页号大于总页数-showNum,且总页数大于showNum，开始页号为总页-showNum
		startpage = startpage > pageTotal - showNum && pageTotal > showNum ? pageTotal
				- showNum
				: startpage;
		// 结束页号小于showNum（每页页数）且小于总页数，结束页号showNum
		endpage = endpage < showNum && endpage < pageTotal ? showNum : endpage;
		endpage = endpage > pageTotal?pageTotal:endpage;

		if (page > 1) {
			html += "<li>" 
					+ "<a href='"+ url+pageName+"="+(page-1)+"' aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a>" 
					+ "</li>";
		}
		if (startpage > 1) {
			html += "<li><a href='"+url+pageName+"=1'>1</a></li>";
		}
		if (pageTotal > 1)
			for (int i = startpage; i <= endpage; i++) {
				html += "<li " + (i == page ? "class='active'" : "") + ">"
						+ "<a "+ (i == page ? "" : "href='" + url + pageName+"=" + i + "'")+ ">" + i + "</a>" 
						+ "</li>";
			}
		if (page < pageTotal) {
			html += "<li class='p'>" 
					+ "<a href='" + url + pageName+"=" + (page + 1)+ "' aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a>" 
					+ "</li>" ;
		}
		html += "</ul>"
				+"</nav>";
		return html;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageTotal() {
		return pageTotal;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public String getPageName() {
		return pageName;
	}
	
	public void setPageTotal(Integer pageTotal) {
		this.pageTotal = pageTotal;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}
	/**
	 * test 分页
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		int page = 1, perno = 1, total = 60;
		// 实例化分页对象
		Pagination pu = new Pagination(total, perno, page);
		// 查询
		// SQL语句分页：pageSize start,perno
		// pu.getStartIndex(), pu.getperno()
		//$("startIndex:" + pu.getStartIndex());
		// 查询得出list
		// 分页跳转链接
		String url = "?";
		 $(pu.getHtml(url,10));
		//$(pu.getHtml(2));
	} 

	public static void $(String s) {
		System.out.println(s);
	}
}
