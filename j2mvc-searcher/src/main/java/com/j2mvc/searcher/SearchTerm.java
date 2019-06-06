package com.j2mvc.searcher;

import org.apache.lucene.search.BooleanClause;

/**
 * 搜索Term<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔
 * 2015年1月21日
 */
public class SearchTerm {
	/** 关键词 */
	private String keyword;
	/** 字段名称 */
	private String fieldname;
	/** 游标 */
	private BooleanClause.Occur occur;

	public SearchTerm(String keyword, String fieldname,
			BooleanClause.Occur occur) {
		this.keyword = keyword;
		this.fieldname = fieldname;
		this.occur = occur;
	}

	public SearchTerm() {
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public BooleanClause.Occur getOccur() {
		return occur;
	}

	public void setOccur(BooleanClause.Occur occur) {
		this.occur = occur;
	}
	
}
