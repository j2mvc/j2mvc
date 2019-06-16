package com.j2mvc.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;

import com.j2mvc.util.Error;
import com.j2mvc.util.Pagination;
import com.j2mvc.util.StringUtils;
import com.j2mvc.util.Utils;

/**
 * 搜索<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔 2015年1月21日
 */
public class Searcher {
	Logger log = Logger.getLogger(getClass());

	/** 条目搜索 */
	public static final String SEARCH_TYPE_ITEM="item";
	/** 分类搜索 */
	public static final String SEARCH_TYPE_CAT="cat";
	
	
	/** 最高的n条记录 */
	int n = 100000;
	/** 索引路径 */
	private String indexDir;
	/** 查询字段组 */
	private String[] queryFields = {"title","subtitle","keywords","content"};
	/** 高亮字前缀 */
	private String lightHeightPrefix = "<span style='color:#CC3300'>";
	/** 高亮字后缀 */
	private String lightHeightPostfix = "</span>";
	
	// 关键词
	// 实例化IKAnalyzer分词器 当为true时，分词器进行智能切分
	Analyzer analyzer = new IndonesianAnalyzer();
	Directory directory;
	IndexReader reader;
	IndexSearcher isearcher;
	Pagination pagination;
	//关键字高亮            
	SimpleHTMLFormatter lightHeightFormatter;
	Highlighter highlighter;
	// 输出结果
	ScoreDoc[] scoreDocs;
	// 分页列表
	List<SearchItem> items = new ArrayList<SearchItem>();
	
	public Searcher(String indexDir) {
		super();
		this.indexDir = indexDir;
	}

	public Searcher(int n, String indexDir,
			String lightHeightPrefix, String lightHeightPostfix) {
		super();
		this.n = n;
		this.indexDir = indexDir;
		this.lightHeightPrefix = lightHeightPrefix;
		this.lightHeightPostfix = lightHeightPostfix;
	}

	public Searcher(int n, String indexDir, String[] queryFields) {
		super();
		this.n = n;
		this.indexDir = indexDir;
		this.queryFields = queryFields;
	}

	public Searcher(int n, String indexDir, String[] queryFields,
			String lightHeightPrefix, String lightHeightPostfix) {
		super();
		this.n = n;
		this.indexDir = indexDir;
		this.queryFields = queryFields;
		this.lightHeightPrefix = lightHeightPrefix;
		this.lightHeightPostfix = lightHeightPostfix;
	}

	public Searcher(String indexDir, String[] queryFields) {
		super();
		this.indexDir = indexDir;
		this.queryFields = queryFields;
	}

	public Searcher(String indexDir, String lightHeightPrefix,
			String lightHeightPostfix) {
		super();
		this.indexDir = indexDir;
		this.lightHeightPrefix = lightHeightPrefix;
		this.lightHeightPostfix = lightHeightPostfix;
	}

	public Searcher(String indexDir, String[] queryFields,
			String lightHeightPrefix, String lightHeightPostfix) {
		super();
		this.indexDir = indexDir;
		this.queryFields = queryFields;
		this.lightHeightPrefix = lightHeightPrefix;
		this.lightHeightPostfix = lightHeightPostfix;
	}

	/**
	 * 取出分页条目
	 * 
	 */
	public List<SearchItem> getItems() {
		return items;
	}

	/**
	 * 解析分页条目
	 * 
	 */
	public void parseItems(int contentLength){
		if(scoreDocs == null || pagination == null)
			return;
		for (int i = pagination.getStartIndex(); i < pagination.getEndIndex(); i++) {
			Document doc;
			try {
				doc = isearcher.doc(scoreDocs[i].doc);
				// 主键
				String id = doc.get("id");
				// 分类
				String catid = doc.get("catId");
				// 标题
				String title = doc.get("title");
				title = title != null ? title : "";
				// 副标题
				String href = doc.get("href");
				// 副标题
				String subtitle = doc.get("subtitle");
				subtitle = subtitle != null ? subtitle : "";
				// 内容
				String content = doc.get("content");
				// 关键字
				String keywords = doc.get("keywords");
				keywords = keywords != null ? keywords : "";
				// 来源
				String source = doc.get("source");
				// 创建时间
				String createTime = doc.get("create_time");
				// 更新时间
				String updateTime = doc.get("update_time");
				// 索引时间
				String indexedTime = doc.get("indexed_time");

				// 关键字高亮
				try {
					String titlehighlightfield = highlighter.getBestFragment(analyzer, "title", title);
					if (titlehighlightfield != null) {
						title = titlehighlightfield;
					}
					String subtitlehighlightfield = highlighter.getBestFragment(analyzer, "subtitle", subtitle);
					if (subtitlehighlightfield != null) {
						subtitle = subtitlehighlightfield;
					}
					String keywordshighlightfield = highlighter.getBestFragment(analyzer, "keywords", keywords);
					if (keywordshighlightfield != null) {
						keywords = keywordshighlightfield;
					}
					content = StringUtils.subHtml(content, contentLength);
					String contenthighlightfield = highlighter.getBestFragment(analyzer, "content", content);
					if (contenthighlightfield != null) {
						content = contenthighlightfield;
					} 
					
					SearchItem item = new SearchItem();
					item.setId(id);
					item.setCatId(catid);
					item.setContent(content);
					item.setCreateTime(createTime!=null&&createTime.matches("\\d+")?Long.parseLong(createTime):0L);
					item.setIndexedTime(indexedTime);
					item.setKeywords(keywords);
					item.setSource(source);
					item.setSubtitle(subtitle);
					item.setTitle(title);
					item.setUpdateTime(updateTime!=null&&updateTime.matches("\\d+")?Long.parseLong(updateTime):0L);
					item.setHref(href);

					BytesRef iBytes = doc.getBinaryValue("images");
					BytesRef fBytes = doc.getBinaryValue("files");
					if(iBytes!=null ){
						Object iObject = Utils.bytesToObject(iBytes.bytes);
						if(iObject!=null && iObject instanceof String[])
							item.setImages((String[])iObject);
					}
					if(fBytes!=null){
						Object fObject = Utils.bytesToObject(fBytes.bytes);
						if(fObject!=null && fObject instanceof String[])
							item.setFiles((String[])fObject);
					}
					items.add(item);
				} catch (InvalidTokenOffsetsException e) {
					log.warn("getInfos InvalidTokenOffsetsException:"
							+ e.getMessage());
				} catch (IOException e) {
					log.warn("getInfos IOException:" + e.getMessage());
				}
			} catch (CorruptIndexException e1) {
				log.warn("isearcher CorruptIndexException:" + e1.getMessage());
			} catch (IOException e1) {
				log.warn("isearcher IOException:" + e1.getMessage());
			}
		}
	}
	
	/**
	 * 执行查询
	 * @param keyword 关键记号
	 * @param pageSize 每页条数
	 * @param page 当前页
	 * 
	 */
	public SearchResult query(String keyword,int pageSize,int page){
		return query(keyword, pageSize, page, null,360);
	}

	public SearchResult query(String keyword,int pageSize,int page,String pageName,int contentLength){
		return query(keyword, pageSize, page, pageName, SEARCH_TYPE_ITEM,contentLength);
	}
	/**
	 * 执行查询
	 * @param keyword 关键记号
	 * @param pageSize 每页条数
	 * @param page 当前页
	 * @param pageName 页号
	 * @param SearchType 搜索类型info:条目，cat：分类
	 * 
	 */
	public SearchResult query(String keyword,int pageSize,int page,String pageName,String SearchType,int contentLength){
		if(keyword == null || "".equals(keyword)){
			return new SearchResult(new Error("未输入关键字。"));
		}
		try {
			// 建立索引对象
			File file = new File(indexDir);
			if(!file.exists()){
				return new SearchResult(new Error("索引目录不存在。"));
			}
			directory = FSDirectory.open(file.toPath());
			// 搜索过程**********************************
			// 实例化搜索器
			reader = DirectoryReader.open(directory);
			//在索引器中使用IKSimilarity相似度评估器
			isearcher = new IndexSearcher(reader);
				
			QueryParser parser = new MultiFieldQueryParser(queryFields, analyzer);
					
			Query query = parser.parse(keyword);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);

			// 搜索相似度最高的n条记录
			TopDocs topDocs = isearcher.search(query, n);
			
			// 分页
			TotalHits totalHits = topDocs.totalHits;
			int total = Integer.parseInt(String.valueOf(totalHits.value)); 
			pagination = new Pagination(total,pageSize, page, pageName);
			// 输出结果
			scoreDocs = topDocs.scoreDocs;
			if(scoreDocs.length == 0){
				return new SearchResult(new Error("未找到结果。"));
			}
			//关键字高亮            
			lightHeightFormatter = new SimpleHTMLFormatter(lightHeightPrefix, lightHeightPostfix);
			highlighter = new Highlighter(lightHeightFormatter, new QueryScorer(query));
			// 解析搜索列表
			if(SearchType.equals(SEARCH_TYPE_ITEM)){
				parseItems(contentLength);
			}else if(SearchType.equals(SEARCH_TYPE_CAT)){
				
			}
			return new SearchResult(highlighter, scoreDocs, pagination, isearcher);			
		} catch (CorruptIndexException e) {
			log.warn("CorruptIndexException:"+e.getMessage());
		} catch (LockObtainFailedException e) {
			log.warn("LockObtainFailedException:"+e.getMessage());
		} catch (IOException e) {
			log.warn("IOException:"+e.getMessage());
		} catch (ParseException e) {
			log.warn("ParseException:"+e.getMessage());
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.warn(e.getMessage());
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					log.warn("close IOException:"+e.getMessage());
				}
			}
		}
		return new SearchResult(new Error("未找到结果。"));
	}
}
