/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 * 
 */
package org.wltea.analyzer.sample;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


/**
 * IKAnalyzer 示例
 * 2012-3-2
 * 
 * 以下是结合Lucene3.4 API的写法
 *
 */
public class CopyOfIKAnalyzerDemo {
	
	public static void indexed(File indexDir,String text) throws IOException{

		//实例化IKAnalyzer分词器
		Analyzer analyzer = new IKAnalyzer();	 
		
		Directory directory = null;
		IndexWriter writer = null;
		try {
			//建立内存索引对象
			directory = FSDirectory.open(indexDir);	 
			
			//配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_34 , analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(directory , iwConfig);
			//写入索引
			Document doc = new Document();
			doc.add(new Field("ID", "10000", Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
			writer.addDocument(doc);
			writer.close();
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally{
			if(directory != null){
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void search(File indexDir,String[] queryFields,String keyword){

		//实例化IKAnalyzer分词器
		Analyzer analyzer = new IKAnalyzer();	 
		
		Directory directory = null;
		IndexReader reader = null;
		IndexSearcher searcher = null;
		try {
			//建立索引对象
			directory = FSDirectory.open(indexDir);	 
			
			//配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_34 , analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			
			//搜索过程**********************************
		    //实例化搜索器   
			reader = IndexReader.open(directory);
			searcher = new IndexSearcher(reader);			
					
			//使用QueryParser查询分析器构造Query对象

		    QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35,
		    		  queryFields,analyzer);

		    // Query query = parser.parse(keyword);
			parser.setDefaultOperator(QueryParser.OR_OPERATOR);//.AND_OPERATOR);
			Query query = parser.parse(keyword);
			
			//搜索相似度最高的5条记录
			TopDocs topDocs = searcher.search(query , 5);
			System.out.println("命中：" + topDocs.totalHits);
			//输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++){
				Document targetDoc = searcher.doc(scoreDocs[i].doc);
				System.out.println("内容：" + targetDoc.toString());
			}			
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(directory != null){
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) throws Exception{
		String lucene = System.getProperty("user.dir") + File.separator + "lucene";

	    File indexDir = new File(lucene);
	    if (!indexDir.exists() || !indexDir.isDirectory()) {
	        throw new Exception(indexDir +
	          " does not exist or is not a directory.");
	      }
		//Lucene Document的域名
		String[] fieldName = {"contents"};
		 //检索内容
		String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
		
		String keyword = "贵阳 词典中文";
		CopyOfIKAnalyzerDemo.indexed(indexDir, text);
		CopyOfIKAnalyzerDemo.search(indexDir,fieldName, keyword);
	}
}
