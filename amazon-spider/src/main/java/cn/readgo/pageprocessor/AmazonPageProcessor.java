package cn.readgo.pageprocessor;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ldy on 2016/1/11.
 */
public class AmazonPageProcessor implements PageProcessor{

    private static final AtomicInteger pageNumber = new AtomicInteger(1);

    public void process(Page page) {
        /* isbn */
        List<String> asins = page.getHtml().xpath("//li[@class='s-result-item celwidget']/@data-asin").all();
        page.putField("asins", asins);

        /* title */
        List<String> titles = page.getHtml().xpath("//li[@class='s-result-item celwidget']/div/div/div/div[2]/div[1]/a/@title").all();
        page.putField("titles", titles);

        String nextPageUrl = page.getHtml().xpath("//a[@id='pagnNextLink']/@href").get();
        if(StringUtils.isNotEmpty(nextPageUrl)) {
            page.addTargetRequest(nextPageUrl);
        } else {
            page.putField("end", "1");
        }
        page.putField("pageNumber", "" + pageNumber.getAndAdd(1));

        String listTitle = page.getHtml().xpath("//div[@class='categoryRefinementsSection']//li/strong/text()").get();
        page.putField("listTitle", listTitle);
    }

    public Site getSite() {
        Site site = Site.me().setDomain("amazon").setCharset("utf-8").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(3000);
        site.setSleepTime(3000);
        return site;
    }
}
