package com.j2mvc.searcher;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import com.j2mvc.util.StringUtils;
import com.j2mvc.util.Utils;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 创建索引<BR>
 * 贵州沃尔达科技有限公司
 * @author 杨朔 2015年1月21日
 */
public class Indexer {
	static SimpleDateFormat formatter = SearchConfiguration.DATE_TIME_FORMAT;
	static String indexDir;
	static int batchPerNum = 500;

	/**
	 * 创建索引
	 * @param list
	 * @param indexDir
	 * 
	 */
	public static Integer indexed(List<SearchItem> list, String indexDir) {
		Integer indexedTotal = 0;
		if (list == null) {
			return 0;
		}

		File file = new File(indexDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		File lock = new File(indexDir+File.separator+"write.lock");
		if(lock.exists())
			lock.delete();
		
		Directory directory = null;
		try {
			directory = FSDirectory.open(file);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		if(directory == null)
			System.err.println("创建索引,directory为空.");

		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_35,analyzer);
		iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, iwConfig);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			closeWriter(writer);
			return 0;
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			closeWriter(writer);
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			closeWriter(writer);
			return 0;
		} 

		int size = list.size();
		try {
			for (int i = 0; i < size; i++) {
	
				SearchItem item = list.get(i);
	
				String content = item.getContent();
				String id = item.getId();
				if (id == null) {
					System.err.println("创建索引,遍历searchInfo 列表，第" + i + "条id为空.");
					continue;
				}
				Term term = new Term("id", id);
	
				writer.deleteDocuments(term);
				
	
				Document doc = new Document();
				doc.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("catId", item.getCatId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("href", item.getHref(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("title", item.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("content", StringUtils.removeHtmlTag(content), Field.Store.YES, Field.Index.ANALYZED));
	
				doc.add(new Field("keywords", item.getKeywords(), Field.Store.YES, Field.Index.ANALYZED));
	
				byte[] iBytes = Utils.objectToBytes(item.getImages());
				if (iBytes != null)
					doc.add(new Field("images", iBytes));
				byte[] fBytes = Utils.objectToBytes(item.getFiles());
				if (fBytes != null)
					doc.add(new Field("files", fBytes));
				doc.add(new Field("source", item.getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	
				doc.add(new Field("create_time", item.getCreateTime(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("update_time", item.getCreateTime(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("indexed_time", formatter.format(new Date()), Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc);
			}
			indexedTotal = writer.numDocs();
			writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return indexedTotal;
	}
	/**
	 * 关闭写
	 * @param writer
	 */
	private static void closeWriter(IndexWriter writer){
		if (writer != null)
			try {
				writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 创建分类索引
	 * @param list
	 * @param indexDir
	 * 
	 */
	public static Integer indexCats(List<SearchCat> list, String indexDir) {
		Integer indexedTotal = Integer.valueOf(0);

		Analyzer analyzer = new IKAnalyzer();

		Directory directory = null;
		IndexWriter writer = null;
		try {
			File file = new File(indexDir);
			if(!file.exists()){
				file.mkdirs();
			}
			directory = FSDirectory.open(file);

			if (list == null) {
				return indexedTotal;
			}

			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);

			iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

			writer = new IndexWriter(directory, iwConfig);

			for (int i = 0; i < list.size(); i++) {
				SearchCat cat = list.get(i);

				Term term = new Term("id", cat.getId());
				writer.deleteDocuments(term);

				Document doc = new Document();
				doc.add(new Field("id",cat.getId(), Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("names", cat.getNames(), Field.Store.YES,Field.Index.ANALYZED));

				writer.addDocument(doc);
			}

			indexedTotal = writer.numDocs();

			writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			closeWriter(writer);
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			closeWriter(writer);
		} catch (IOException e) {
			e.printStackTrace();
			closeWriter(writer);
		} finally {
			closeWriter(writer);
		}
		return indexedTotal;
	}

	/**
	 * 删除索引
	 * @param indexDir
	 * @param term
	 */
	public static void deleteIndexedes(File indexDir, Term term) {
		Analyzer analyzer = new IKAnalyzer();
		Directory directory = null;
		IndexWriter writer = null;
		try {
			directory = FSDirectory.open(indexDir);

			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(directory, iwConfig);

			writer.deleteDocuments(term);
		} catch (Exception e) {
			closeWriter(writer);
			e.printStackTrace();
		} finally {
			closeWriter(writer);
		}
	}
}
