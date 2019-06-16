package com.j2mvc.searcher;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

public class SearchFieldType {

	public static FieldType normalFieldType() {

		//创建字段类型
		FieldType type = new FieldType();
		
		//设置反向索引存储信息
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		//内容是否存储
		type.setStored(true);
		//是否分词
		type.setTokenized(false);
		//是否标准化
		type.setOmitNorms(true);
		//是否建立正向索引
		type.setDocValuesType(DocValuesType.SORTED);
		//是否存储词项向量信息
		type.setStoreTermVectors(true);
		//是否存储词项偏移量
		type.setStoreTermVectorOffsets(true);
		//是否存储词项位置
		type.setStoreTermVectorPositions(true);
		//是否存储词项附加信息
		type.setStoreTermVectorPayloads(true);
		//索引字段类型对象，不可以修改
		type.freeze();
		
		return type;

	}

	public static FieldType tokenizedFieldType() {

		//创建字段类型
		FieldType type = new FieldType();
		
		//设置反向索引存储信息
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		//内容是否存储
		type.setStored(true);
		//是否分词
		type.setTokenized(true);
		//是否标准化
		type.setOmitNorms(true);
		//是否建立正向索引
		type.setDocValuesType(DocValuesType.SORTED);
		//是否存储词项向量信息
		type.setStoreTermVectors(true);
		//是否存储词项偏移量
		type.setStoreTermVectorOffsets(true);
		//是否存储词项位置
		type.setStoreTermVectorPositions(true);
		//是否存储词项附加信息
		type.setStoreTermVectorPayloads(true);
		//索引字段类型对象，不可以修改
		type.freeze();
		
		return type;

	}
}
