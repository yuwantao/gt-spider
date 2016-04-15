package cn.readgo.pageprocessor;

import cn.readgo.utils.FileUtil;
import cn.readgo.utils.Sys;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * Created by ldy on 2016/1/22.
 */
public class SubjectBookPageProcessor implements PageProcessor {
    private static final Logger logger = Logger.getLogger(SubjectBookPageProcessor.class);

    @Override
    public void process(Page page) {
        Object detail = page.getRequest().getExtra("detail");
        if(detail != null && "1".equals(detail.toString())) {
            processDetailPage(page);
        } else {
            processListPage(page);
        }
    }

    private void processListPage(Page page) {
        if (StringUtils.isEmpty(page.getHtml().toString()) ||
                page.getHtml().toString().contains("Enter the characters you see below")) {
            page.addTargetRequest(page.getRequest());
            changeUserAgent();
            return;
        }

        Object subjectCode = page.getRequest().getExtra("subjectCode");

        List<String> hrefs = Lists.newArrayList();
        List<Selectable> nodes = page.getHtml().xpath("//div[@class='categoryRefinementsSection']//li").nodes();
        for (Selectable node : nodes) {
            String numberValue = node.xpath("//a/span[2]/text()").get();
            if(numberValue != null && numberValue.trim().endsWith(")")) {
                hrefs.add(node.xpath("//a/@href").get());
//                System.out.print(node.xpath("//a/span[1]/text()").get() + ", ");
//                System.out.println(node.xpath("//a/@href").get());
            }
        }

        if(CollectionUtils.isNotEmpty(hrefs)) {
            for (String href : hrefs) {
                page.addTargetRequest(new Request(href).putExtra("subjectCode", subjectCode));
            }
        } else {
            //解析book详情页以及下一页链接加入队列；
            List<String> detailLinks = page.getHtml().xpath("//li[@class='s-result-item celwidget']/div/div/div/div[2]/div[1]/a/@href").all();
            for (String detailLink : detailLinks) {
                Request request = new Request(detailLink);
                request.putExtra("detail", "1");
                request.putExtra("subjectCode", subjectCode);
                page.addTargetRequest(request);
            }

            String nextPageUrl = page.getHtml().xpath("//a[@id='pagnNextLink']/@href").get();
            if(StringUtils.isNotEmpty(nextPageUrl)) {
                page.addTargetRequest(nextPageUrl);
                page.addTargetRequest(new Request(nextPageUrl).putExtra("subjectCode", subjectCode));
            }
        }
        page.setSkip(true);//抓取的列表页不进行数据存储
    }

    private void processDetailPage(Page page) {
        String title = page.getHtml().xpath("//span[@id='productTitle']/text()").get();
        if (StringUtils.isEmpty(title)) {
            logger.warn("title is null,url:" + page.getRequest().getUrl());
            FileUtil.writeContent("d:\\error\\error.html", page.getHtml().toString(), false);
            page.addTargetRequest(page.getRequest());
            changeUserAgent();
            return;
        }

        String isbn10 = null;
        String isbn13 = null;
        List<Selectable> nodes = page.getHtml().xpath("//div[@class='content']/ul/li").nodes();
        for (Selectable node : nodes) {
            String str = node.toString();
            if(!str.contains("ISBN-10:") && !str.contains("ISBN-13:")) {
                continue;
            }
            if (str.contains("ISBN-10:")) {
                isbn10 = str.substring(str.indexOf("</b>") + "</b>".length(), str.indexOf("</li>")).trim();
            } else if(str.contains("ISBN-13:")) {
                isbn13 = str.substring(str.indexOf("</b>") + "</b>".length(), str.indexOf("</li>")).trim();
            }
            if (StringUtils.isNotEmpty(isbn10) && StringUtils.isNotEmpty(isbn13)) {
                break;
            }
        }

        if(StringUtils.isEmpty(isbn10) && StringUtils.isEmpty(isbn13)) {
            logger.warn("isbn10 and isbn13 is null, url:" + page.getRequest().getUrl());
        } else if(StringUtils.isEmpty(isbn10)) {
            logger.warn("isbn10 is null, url:" + page.getRequest().getUrl());
        } else if(StringUtils.isEmpty(isbn13)) {
            logger.warn("isbn13 is null, url:" + page.getRequest().getUrl());
        }

        Object subjectCode = page.getRequest().getExtra("subjectCode");
        page.putField("subjectCode", subjectCode);
        page.putField("bookTitle", title);
        page.putField("isbn10", isbn10);
        page.putField("isbn13", isbn13);
    }

    /**
     * 更换UA
     */
    private void changeUserAgent() {
        Sys.uaQueue.add(this.getSite().getUserAgent());
        String userAgent = Sys.uaQueue.poll();
        if (StringUtils.isNotEmpty(userAgent)) {
            this.getSite().setUserAgent(userAgent);
        }
    }

    @Override
    public Site getSite() {
        Site site = Site.me().setCycleRetryTimes(50).setRetryTimes(5).setSleepTime(3500).setDomain("subject_book.amazon.com");
        site.setTimeOut(5000);
        site.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//        site.setHttpProxy(new HttpHost("46.105.152.77", 8888));
        return site;
    }
}
