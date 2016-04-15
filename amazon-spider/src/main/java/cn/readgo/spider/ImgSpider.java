package cn.readgo.spider;

import cn.com.cibtc.redis.RedisFactory;
import cn.readgo.pageprocessor.ImgPageProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * Created by ldy on 2016/1/12.
 */
public class ImgSpider {
    public static void main(String[] args) {
//        Request request = new Request("http://www.amazon.cn/gp/search-inside/reftag/ref=litb_rdc_dsk_p_wp_nk_wo?asin=B00931XB8K");
//        request.setMethod("POST");
//        Spider.create(new ImgPageProcessor()).addRequest(request).start();


        boolean ret = RedisFactory.getShardedRedis().addSetString("set_test", "112221", -1);
        System.out.println(ret);
    }
}
