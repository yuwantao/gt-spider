package cn.readgo.spider;

import cn.readgo.pageprocessor.OzonPageProcessor;
import cn.readgo.schedule.RedisScheduler;
import cn.readgo.utils.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.xsoup.Xsoup;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>标题：OzonSpider </p>
 * <p>
 * 功能描述：
 * </p>
 * <p>创建日期：2016/3/3 16:51 </p>
 * <p>作者：ldy </p>
 * <p>版本：1.0 </p>
 */
public class OzonSpider {
    private HttpClientDownloader downloader = new HttpClientDownloader();

    private static final String JSON_PATHNAME = "d:\\ozon-data\\ozon-json-data";
    private static final String LINK_PATHNAME = "d:\\ozon-data\\ozon-hrefs";

    public static void main(String[] args) {
//        new OzonSpider().crawlDetailLinks();
        Spider spider = Spider.create(new OzonPageProcessor()).
                setScheduler(new RedisScheduler())
                ;
        List<String> links = FileUtil.readFileByLinesToList(LINK_PATHNAME);
        for (String link : links) {
            Request request = new Request(link);
            String id = link.substring(link.indexOf("id/"), link.lastIndexOf("/"));
            request.putExtra("id", id);
            spider.addRequest(request);
        }
        spider.start();
    }

    public void crawlDetailLinks() {
        //抓取第一屏的数据
        Request request = new Request("http://www.ozon.ru/catalog/1138364/?price=5-1039578&sort=new");
        Site site = Site.me().setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
        site.setTimeOut(10000);
        Html html = downloader.download(request, site.toTask()).getHtml();
        List<String> firstHrefs = Xsoup.select(html.toString(), "//a[@class='jsUpdateLink bOneTile_link']/@href").list();

        //循环下载瀑布流的数据
        downloadJson();
        List<String> links = parseLinks();

        //合并数据存储到文件
        List<String> hrefs = Lists.newArrayList();
        hrefs.addAll(firstHrefs);
        hrefs.addAll(links);

        FileUtil.writeContent(LINK_PATHNAME, hrefs, true);
        //随便看下去重后多少
        Set<String> set = Sets.newHashSet();
        for (String href : hrefs) {
            set.add(href);
        }
        System.out.println("去重后结果总数：" + set.size());
    }

    public void downloadJson() {
        int offset = 30;
        for(int i = 0; i < 25; i++) {
            System.out.println(offset);
            String str = postData("{\"context\":\"catalog\",\"facetId\":61,\"facetParams\":\"catalog=1138364&price=5-1039578&sort=new\",\"searchText\":\"\",\"limit\":42,\"offset\":" + offset + ",\"sortType\":1,\"year\":null}");
            System.out.println(str);
            System.out.println();

            offset += 42;

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FileUtil.writeContent(JSON_PATHNAME, str, true);
        }
    }

    public List<String> parseLinks() {
        Set<String> set = Sets.newHashSet();
        List<String> hrefs = Lists.newArrayList();
        List<String> list = FileUtil.readFileByLinesToList(JSON_PATHNAME);
        for (String line : list) {
            JSONObject jsonObject = JSONObject.parseObject(line);
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray tiles = d.getJSONArray("Tiles");
            for (Object tile : tiles) {
                JSONObject dataJson = (JSONObject) tile;
                String href = dataJson.getString("Href");
                href = "http://www.ozon.ru" + href;
                hrefs.add(href);
                set.add(href);
            }
        }
        System.out.println("去重后结果总数：" + set.size());
        return hrefs;
    }

    public String postData(String json) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://www.ozon.ru/json/tiles.asmx/gettiles");
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            HttpResponse res = client.execute(post);
            System.out.println(res.getStatusLine().getStatusCode());
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

//    private void parseLinks() {
//        Set<String> set = Sets.newHashSet();
//        byte[] bytesFromFile = FileUtil.getBytesFromFile(new File("d:\\ozon-data\\old\\ozon.ru"));
//        String html = new String(bytesFromFile);
//        List<String> list = Xsoup.select(html, "//a[@class='jsUpdateLink bOneTile_link']/@href").list();
//        List<String> list1 = Xsoup.select(html, "//a[@class='bOneTile_link jsUpdateLink']/@href").list();
//        list.addAll(list1);
//        for (String s : list) {
//            System.out.println(s);
//            set.add(s);
//        }
//        System.out.println(list.size() + ", " + set.size());
//    }
}
