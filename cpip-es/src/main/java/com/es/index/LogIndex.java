package com.es.index;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.es.util.EsClient;

public class LogIndex {
	
	private static Logger log = Logger.getLogger(LogIndex.class);
	//分别建log2017，log2018，log2019索引名
	//索引名称
	private static String indexName = "log2017";
	//索引类型
	private static String indexType = "t_log2017";
	private static TransportClient trClient = null;
	
	public static Settings createSetting() {
		Settings setting = Settings.builder()
				.put("index.number_of_shards", 5)
				.put("index.number_of_replicas", 1)
				.build();
		return setting;
	}
	
	/**
	 * 创建标签mapping
	 * @return
	 */
	public static XContentBuilder createMapping() {
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder().startObject()
					// properties 声明字段
					// string 类型 如果analyzed的话结果是text;如果not_analyzed的话结果是keyword
					.startObject("properties")
					.startObject("id").field("type", "string").field("index", "not_analyzed").endObject()
					.startObject("name").field("type", "string").field("index", "not_analyzed").endObject()
					.startObject("idCard").field("type", "string").field("index", "not_analyzed").endObject()
					.startObject("createtime").field("type", "date").field("format","yyyy-MM-dd HH:mm:ss").field("index", "not_analyzed").endObject()
					.startObject("LOCATION").field("type", "geo_point").endObject()
					.endObject().endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping;
	}

	/**
	 * 创建标签索引
	 * 第三步 创建索引(数据库) 
	 */
	public static void createIndex() {
		XContentBuilder mapping = createMapping();
		trClient = EsClient.getEsClient();
		// 创建索引
		trClient.admin().indices().prepareCreate(indexName)
				/* .setSettings(setting) */.get();
		// 把type和mapping放入index
		PutMappingRequestBuilder putMapping = trClient.admin()
				.indices().preparePutMapping(indexName)
				.setType(indexType)
				.setSource(mapping);
		PutMappingResponse response = putMapping.get();
		
		if (!response.isAcknowledged()) {
			log.info("无法创建mapping");
		} else {
			log.info("创建mapping成功");
		}
	}
	
	/**
	 * 删除索引
	 */
	public static void deleteIndex() {
		trClient = EsClient.getEsClient();
		DeleteIndexResponse deleteIndexResponse = trClient.admin().indices().prepareDelete(indexName)
                .execute().actionGet();
	}
	
	public static void main(String[] args) {
		createIndex();
		//deleteIndex();
	}

}
