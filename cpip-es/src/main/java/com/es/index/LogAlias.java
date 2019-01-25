package com.es.index;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;

import com.es.util.EsClient;import net.sf.jsqlparser.util.AddAliasesVisitor;

/**
 * 别名使用
 * 
 * @author Administrator
 *
 */
public class LogAlias {

	// 索引名称
	private static String indexName = "log2019";
	private static TransportClient trClient = null;
	
	private static String indexAliasName = "logAll";

	static {
		trClient = EsClient.getEsClient();
	}

	/**
	 * 添加别名
	 */
	public static void addAlias() {
		trClient = EsClient.getEsClient();
		trClient.admin().indices().prepareAliases().addAlias(indexName, indexAliasName)
		.execute().actionGet();
	}

	public static void main(String[] args) {
		addAlias();
	}

}
