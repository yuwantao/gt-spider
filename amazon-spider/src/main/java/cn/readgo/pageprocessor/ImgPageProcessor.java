package cn.readgo.pageprocessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by ldy on 2016/1/12.
 */
public class ImgPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        System.out.println(page.getHtml().toString());
    }

    @Override
    public Site getSite() {
        return Site.me();
    }
}
