package com.j2mvc.searcher;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import com.j2mvc.util.Error;
import com.j2mvc.util.Pagination;

public class SearchResult {
	//关键字高亮  
	private Highlighter highlighter;
	// 所有结果
	private ScoreDoc[] scoreDocs;
	// 分页
	private Pagination pagination;
	//在索引器中使用IKSimilarity相似度评估器
	private IndexSearcher isearcher;
	// 错误
	private Error error;
	
	public SearchResult(Highlighter highlighter, ScoreDoc[] scoreDocs,
			Pagination pagination, IndexSearcher isearcher) {
		super();
		this.highlighter = highlighter;
		this.scoreDocs = scoreDocs;
		this.pagination = pagination;
		this.isearcher = isearcher;
	}
	public SearchResult(Error error) {
		super();
		this.error = error;
	}
	public Highlighter getHighlighter() {
		return highlighter;
	}
	public void setHighlighter(Highlighter highlighter) {
		this.highlighter = highlighter;
	}
	public ScoreDoc[] getScoreDocs() {
		return scoreDocs;
	}
	public void setScoreDocs(ScoreDoc[] scoreDocs) {
		this.scoreDocs = scoreDocs;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
	public IndexSearcher getISearcher() {
		return isearcher;
	}
	public void setISearcher(IndexSearcher isearcher) {
		this.isearcher = isearcher;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	
}
