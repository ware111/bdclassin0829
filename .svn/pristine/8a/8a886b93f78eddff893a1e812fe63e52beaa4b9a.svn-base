package com.blackboard.classin.util;

import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.BbCourseClassinCourse;
import com.blackboard.classin.entity.CourseStudentPOJO;
import com.blackboard.classin.entity.CourseStudentPOJO.ErrorInfo;

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseMembershipDbLoader;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Description HttpClient
 * @Author wangy
 * @Date 2019/4/16
 */
public class HttpClient {

    //日记记录(日志记录工具类在此不提供)
    static final Logger log = Logger.getLogger(HttpClient.class);

    private static  CloseableHttpClient httpClient = null;
    private static CloseableHttpResponse response = null;
    private static  RequestConfig requestConfig = null;

    private static HttpClientConnectionManager connManager = null;
    //只等待10秒，有可能抛出InterruptedException, ExecutionException 异常
    private static HttpClientConnection conn = null;

    private static ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            Args.notNull(response, "HTTP response");
            final HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                final HeaderElement he = it.nextElement();
                final String param = he.getName();
                final String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (final NumberFormatException ignore) {
                    }
                }
            }
            return 1;
        }

    };

    static {
        //创建HTTP连接管理器
        connManager = new BasicHttpClientConnectionManager();

        // 通过址默认配置创建一个httpClient实例
        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .setKeepAliveStrategy(myStrategy)
                .evictExpiredConnections()
                .build();

        requestConfig =RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
    }

    /**
     * doGET
     * @param url
     * @return
     */
    public static String doGet(String url) {

        CloseableHttpResponse response = null;
        String result ="";
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            result =EntityUtils.toString(entity);

        } catch (ClientProtocolException e) {
            result = "timeout";
            log.info( "服务器连接出现异常",e );
        } catch (IOException e) {
            result = "timeout";
            log.info( "服务器连接出现异常",e );
        } catch (Exception e) {
            result = "timeout";
            log.info( "服务器连接出现异常",e );
        }finally {
            if (null != response) {
                try{
                    response.close();
                } catch (IOException e) {
                    log.info("关闭服务器连接出现异常",e );
                }
            }

            if (null != httpClient) {
                try{
                    httpClient.close();
                } catch (IOException e) {
                    log.info("关闭服务器连接出现异常",e );
                }
            }

            if(connManager != null){
                connManager.closeExpiredConnections();
            }

        }
        return result;
    }

    /**
     * POST请求
     * @param url
     * @param data
     * @return
     */
    public static String doPost(String url, String params) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.addHeader("Cache-Control","no-cache");
        
        try {
            HttpEntity entityParam = new StringEntity(params,"UTF-8");
            httpPost.setEntity(entityParam);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode="+statusCode);
            HttpEntity entity = response.getEntity();
            result =EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            log.info( "服务器连接出现异常",e );
        } catch (IOException e) {
            log.info( "服务器连接出现异常",e );
        } catch (Exception e) {
            log.info( "服务器连接出现异常",e );
        } finally {
            // 关闭资源
            if (null!= response) {
                try{
                    response.close();
                } catch (IOException e) {
                    log.info("中转服务器连接出现异常",e );
                }
            }
            if (null !=httpClient) {
                try{
                    httpClient.close();
                } catch (IOException e) {
                    log.info("中转服务器连接出现异常",e );
                }
            }
            if(connManager != null){
                connManager.closeExpiredConnections();
            }
        }
        return result;
    }

    //对接课表数据时，所用post请求
    public static String classSchedulePost(String url, String params) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.addHeader("Cache-Control","no-cache");

        try {
            HttpEntity entityParam = new StringEntity(params,"UTF-8");
            httpPost.setEntity(entityParam);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode="+statusCode);
            HttpEntity entity = response.getEntity();
            result =EntityUtils.toString(entity);
        } catch (Exception e) {
            result = "timeout";
            log.info( "服务器连接出现异常",e );
        } finally {
            // 关闭资源
            if (null!= response) {
                try{
                    response.close();
                } catch (IOException e) {
                    log.info("中转服务器连接出现异常",e );
                }
            }
            if (null !=httpClient) {
                try{
                    httpClient.close();
                } catch (IOException e) {
                    log.info("中转服务器连接出现异常",e );
                }
            }
            if(connManager != null){
                connManager.closeExpiredConnections();
            }
        }
        return result;
    }


	public static void main(String args[]){//51886201
		long currentCreateClassTime = System.currentTimeMillis()/1000;	
        
        String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentCreateClassTime);
        String param3 = "timeStamp="+currentCreateClassTime;
		//注册
		String registry_url = "https://www.eeo.cn/partner/api/schooin.api.php?action=register";
		//注册用户
		String param_nickname = "nickname=jay01";
		String param_pwd = "password=18686838039";
		String param_telephone = "telephone=18686838039";
		String param_schoolMemeber = "addToSchoolMember=2";
		StringBuilder strsBuilder = new StringBuilder();
		strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
			.append("&").append(param_nickname).append("&").append(param_pwd).append("&").append(param_schoolMemeber);
		
		String resultRegisterMap = HttpClient.doPost(registry_url, strsBuilder.toString());
		
		log.info("resultRegisterMap is >>>"+resultRegisterMap);
		
		
		
	
	}
    
    public static String authorize (String url) {
    	String reponseContent = "";
    	CloseableHttpClient httpClient1 = null;
        try {
        	httpClient1 = HttpClients.createDefault();
        	HttpPost postMethod = null;
            postMethod = new HttpPost(url );//传入URL地址
           postMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
           long currentTime = System.currentTimeMillis()/1000;
           String params1 = "SID="+Constants.SID;
           String params2 ="safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentTime);
           String params3 ="timeStamp="+currentTime;
           String params4 ="courseName=classinTest";
           String param = params1+"&"+params2+"&"+params3+"&"+params4;
            postMethod.setEntity(new StringEntity(param, Charset.forName("UTF-8")));
            HttpResponse response = httpClient1.execute(postMethod);//获取响应
            int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("HTTP Status Code:" + statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTP请求未成功！HTTP Status Code:" + response.getStatusLine());
			}
			HttpEntity httpEntity = response.getEntity();
			reponseContent = EntityUtils.toString(httpEntity);
			EntityUtils.consume(httpEntity);//释放资源
			System.out.println("响应内容：" + reponseContent);

        } catch (Exception e) {
        	return null;
        }
        return reponseContent;
    }
}