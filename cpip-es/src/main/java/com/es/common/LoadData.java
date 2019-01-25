package com.es.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.es.util.EsClient;

public class LoadData {

	// 索引名称
	private static String indexName = "log2018";
	// 索引类型
	private static String indexType = "t_log2018";
	
	private static TransportClient trClient = null;
	
	//数据长度
	private static int size = 10;
	//数据开始时间
	private static String startTime = "2018-01-01 00:00:00";
	//数据结束时间
	private static String endTime = "2019-01-01 00:00:00";
	
	static {
		trClient = EsClient.getEsClient();
	}

	/**
	 * 插入数据
	 * @throws Exception
	 */
	public static void insertTestDataEs() throws Exception {
		String[] idCards = {"522121198568523581","522121198568523582","522121198568523583"};
		List<LogModel> list = getDataList(size);
		BulkRequestBuilder builder =  trClient.prepareBulk();
		for(LogModel logModel : list) {
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject();
			int index = (int) (Math.random()*idCards.length);
			String idCard = idCards[index];
			xContentBuilder.field("id", logModel.getId());
			xContentBuilder.field("name", logModel.getName());
			xContentBuilder.field("idCard", idCard);
			xContentBuilder.field("createtime", logModel.getCreatetime());
			xContentBuilder.endObject();
			builder.add(trClient.prepareIndex(indexName, indexType).setSource(xContentBuilder));
		}
		BulkResponse bulkResponse = builder.execute().actionGet();
		System.out.println(bulkResponse);
	}

	/**
	 *  生成数据
	 * @param szie 
	 * @return
	 */
	public static List<LogModel> getDataList(int szie){
		List<LogModel> list = new ArrayList<LogModel>();
		String[] names = {"系统登录","数据查询","我的订单","我的消息","购买中心","信息查询","审批中心"};
 		for(int i = 0 ;i < szie ; i++) {
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			int index = (int) (Math.random()*names.length);
			String name = names[index];
			String createtime = randomDate(startTime, endTime);
			System.out.println(id + " " + name + " " + createtime + " ");
			LogModel logModel = new LogModel();
			logModel.setId(id);
			logModel.setName(name);
			logModel.setCreatetime(createtime);
			list.add(logModel);
		}
		return list;
	}

	private static String randomDate(String beginDate,String endDate){
		try {
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date start = format.parse(beginDate);
	         Date end = format.parse(endDate);
	         if(start.getTime() >= end.getTime()){return null;}
	         long date = random(start.getTime(),end.getTime());
	         return format.format(new Date(date));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long)(Math.random() * (end - begin));
		if(rtn == begin || rtn == end){
			return random(begin,end);
		}  
		return rtn;
	}

	public static void main(String[] args) {
		try {
			insertTestDataEs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
