package com.es.util;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * es connect 
 * @author Administrator
 *
 */
public class EsClient {
	
	private static Properties prop = null;
	private static TransportClient trClient = null;

	public static synchronized void initEsClient(){
		try {
			prop = new Properties();
			InputStream fis = EsClient.class.getClassLoader().getResourceAsStream("elasticsearch.properties");
			prop.load(fis);
			String[] esnodes = prop.getProperty("ip").split(",");
			Settings settings = Settings.builder()
					.put("cluster.name", prop.getProperty("cluster.name"))
					.put("client.transport.sniff", prop.getProperty("client.transport.sniff")).build();
			trClient = new PreBuiltTransportClient(settings);
			for (String node : esnodes) {
				trClient = trClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(node),
						Integer.valueOf(prop.getProperty("port"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 取得实例
	public static synchronized TransportClient getEsClient() {
		if (trClient == null) {
			initEsClient();
		}
		return trClient;
	}
	
	//关闭客户端
	public static void closeEsClient() {
		trClient.close();;
	}
	
	
	public static void main(String[] args) {
		TransportClient trClient = EsClient.getEsClient();
		System.out.println(trClient);
	}

}
