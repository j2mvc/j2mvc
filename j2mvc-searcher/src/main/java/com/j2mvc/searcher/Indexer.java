package com.j2mvc.searcher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import com.j2mvc.util.StringUtils;
import com.j2mvc.util.Utils;

/**
 * 创建索引<BR>
 * 贵州沃尔达科技有限公司
 * 
 * @author 杨朔 2015年1月21日
 */
public class Indexer {
	static SimpleDateFormat formatter = SearchConfiguration.DATE_TIME_FORMAT;
	static String indexDir;
	static int batchPerNum = 500;

	/**
	 * 创建索引
	 * 
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
		File lock = new File(indexDir + File.separator + "write.lock");
		if (lock.exists())
			lock.delete();

		Directory directory = null;
		try {
			directory = FSDirectory.open(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		if (directory == null)
			System.err.println("创建索引,directory为空.");

		Analyzer analyzer = new IndonesianAnalyzer();
		IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
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

				doc.add(new StringField("id", id, Field.Store.YES));
				doc.add(new StringField("catId", item.getCatId(), Field.Store.YES));
				if (!StringUtils.isEmpty(item.getImage()))
					doc.add(new StringField("image", item.getImage(), Field.Store.YES));
				if (!StringUtils.isEmpty(item.getVideo()))
					doc.add(new StringField("video", item.getVideo(), Field.Store.YES));
				if (!StringUtils.isEmpty(item.getDescri()))
					doc.add(new StringField("descri", item.getDescri(), Field.Store.YES));
				if (!StringUtils.isEmpty(item.getExtra()))
					doc.add(new StringField("extra", item.getExtra(), Field.Store.YES));
				if (!StringUtils.isEmpty(item.getHref()))
					doc.add(new StringField("href", item.getHref(), Field.Store.YES));
				
				doc.add(new TextField("title", item.getTitle(), Field.Store.YES));
				doc.add(new TextField("content", StringUtils.removeHtmlTag(content), Field.Store.YES));
				if(item.getKeywords()!=null)
				doc.add(new TextField("keywords", item.getKeywords(), Field.Store.YES));

				byte[] iBytes = Utils.objectToBytes(item.getImages());
				if (iBytes != null)
					doc.add(new StoredField("images", iBytes));
				byte[] fBytes = Utils.objectToBytes(item.getFiles());
				if (fBytes != null)
					doc.add(new StoredField("files", fBytes));
				if(item.getSource()!=null)
				doc.add(new StringField("source", item.getSource(), Field.Store.YES));
				doc.add(new StringField("create_time",
						item.getCreateTime() != null ? item.getCreateTime().toString() : "", Field.Store.YES));
				doc.add(new StringField("update_time",
						item.getCreateTime() != null ? item.getCreateTime().toString() : "", Field.Store.YES));
				doc.add(new StringField("indexed_time", formatter.format(new Date()), Field.Store.YES));

				writer.addDocument(doc);
			}
			indexedTotal = writer.numRamDocs();
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
	 * 
	 * @param writer
	 */
	private static void closeWriter(IndexWriter writer) {
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
	 * 
	 * @param list
	 * @param indexDir
	 * 
	 */
	public static Integer indexCats(List<SearchCat> list, String indexDir) {
		Integer indexedTotal = 0;

		Analyzer analyzer = new IndonesianAnalyzer();

		Directory directory = null;
		IndexWriter writer = null;
		try {
			File file = new File(indexDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			directory = FSDirectory.open(file.toPath());

			if (list == null) {
				return indexedTotal;
			}

			IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);

			iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

			writer = new IndexWriter(directory, iwConfig);

			for (int i = 0; i < list.size(); i++) {
				SearchCat cat = list.get(i);

				Term term = new Term("id", cat.getId());
				writer.deleteDocuments(term);

				Document doc = new Document();
				doc.add(new StringField("id", cat.getId(), Field.Store.YES));
				doc.add(new Field("names", cat.getNames(), SearchFieldType.tokenizedFieldType()));

				writer.addDocument(doc);
			}

			indexedTotal = writer.numRamDocs();

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
	 * 
	 * @param indexDir
	 * @param term
	 */
	public static void deleteIndexedes(File indexDir, Term term) {
		Analyzer analyzer = new IndonesianAnalyzer();
		Directory directory = null;
		IndexWriter writer = null;
		try {
			directory = FSDirectory.open(indexDir.toPath());

			IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
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

	/**
	 * 删除索引 此操作将会删除索引目录，然后重建
	 * 
	 * @param indexDir 索引目录
	 */
	public static void clearIndexedes(String indexDir) {
		File file = new File(indexDir);
		if (file.exists()) {
			file.delete();
		}
		file.mkdirs();
	}
}
