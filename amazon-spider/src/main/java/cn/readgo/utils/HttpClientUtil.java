package cn.readgo.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Created by lidongyang on 2015/6/1.
 */
public class HttpClientUtil {
    private static Logger log = Logger.getLogger(HttpClientUtil.class);

    public HttpClientUtil() {
    }

    public static String getResultByUrl(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(5000));
        httpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(5000));
        HttpPost post = new HttpPost();
        try {
            HttpResponse e = httpClient.execute(post);
            HttpEntity e1 = e.getEntity();
            String r = EntityUtils.toString(e1);
            String var7 = r;
            return var7;
        } catch (Throwable var10) {
            log.error("", var10);
        } finally {
            post.abort();
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }

    public static void main(String[] args) {
        String resultByUrl = getResultByUrl("http://www.amazon.cn/gp/search-inside/reftag/ref=litb_rdc_dsk_p_wp_nk_wo?asin=B00931XB8K");
        System.out.println(resultByUrl);
    }

}
