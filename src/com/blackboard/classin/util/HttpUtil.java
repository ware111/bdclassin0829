package com.blackboard.classin.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by wangyan on 2018/8/15.
 */
public class HttpUtil {

	private final static int CONNECT_TIME_OUT = 6000;
	
	public static String sendPost(JSONObject data,String url) {
		String result = "";
		//CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpParams params = null;
		HttpPost post = new HttpPost(url);
		
		params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIME_OUT);
		
		post.addHeader(HTTP.CONTENT_TYPE, "application/json");
		
		StringEntity entity = new StringEntity(data.toJSONString(),Consts.UTF_8);
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		
		post.setEntity(entity);
		
		try {
			response = client.execute(post);
			result = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(response!=null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		long s = 1556208000000L;
		long s2 = 1556265200629L;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println(df.format(new Date(s)));
		System.out.println(df.format(new Date(s2)));
	}
}
