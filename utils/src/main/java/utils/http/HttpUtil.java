package main.java.utils.http;

import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;

public class HttpUtil {
    public static String sendPost(String url, String data,String cookie) {
        String response = null;

        try {
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse httpresponse = null;
            try {
                httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                StringEntity stringentity = new StringEntity(data,
                        ContentType.create("application/json", "UTF-8"));
                //HttpEntity stringentity = new StringEntity("");
                httppost.setEntity(stringentity);
                httpresponse = httpclient.execute(httppost);
                response = EntityUtils
                        .toString(httpresponse.getEntity());

            } finally {
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpresponse != null) {
                    httpresponse.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static String doGet(String apiUrl,String cookie,String token) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            // 创建客户端连接对象
            client = HttpClients.createDefault();
            // 构建Get请求对象
            System.out.println(apiUrl);
            HttpGet get = new HttpGet(apiUrl);
            if (StringUtils.isNotBlank(cookie)){
                get.setHeader("cookie",cookie);
            }
            if (StringUtils.isNotBlank(token)){
                get.setHeader("token",token);
            }
            RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(3000)
                    .setSocketTimeout(20000).build();
            get.setConfig(config);
            // 获取返回对象
            response = client.execute(get);
            // 整理返回值
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭连接和流
            try {
                if (client != null) {
                    client.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        String apiUrl = "https://mobilecrm.58.com/crm25m/street/opp/queryOppBasicInfo?oppId=99c04339-09f6-48c9-8f14-9de8342b0495";
        String cookie = "crm.auth=RzhoO8zJQdXs8aoiBFiraVeEJbfpaPS2OBAofS1mQdz5ue_YZtvgYw";
        String s = doGet(apiUrl, cookie, null);
        System.out.println(s);
    }
}
