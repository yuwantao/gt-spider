package cn.readgo.pageprocessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * Created by ldy on 2016/1/13.
 */
public class BookCategoryPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        List<Selectable> topNodes = page.getHtml().xpath("//ul[@class='zg_hrsr']/li").nodes();
        List<Selectable> bottomNodes = page.getHtml().xpath("//div[@class='bucket']/div[@class='content']/ul/li").nodes();
        topNodes.addAll(bottomNodes);

        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < topNodes.size(); i++) {
            //拼接类目面包屑
            Selectable node = topNodes.get(i);
            List<String> anchorTextList = node.xpath("//a/text()").all();
            for (int j = 0; j < anchorTextList.size(); j++) {
                strBuilder.append(anchorTextList.get(j));
                if(j != anchorTextList.size() - 1) {
                    strBuilder.append(" > ");
                } else {
                    strBuilder.append("\1");
                }
            }
            //设置分隔符
            if(i == topNodes.size() - bottomNodes.size() - 1) {
                strBuilder.append(",");
            }
        }
        page.putField("bookCategory", strBuilder.toString());
    }

    @Override
    public Site getSite() {
        return Site.me().setDomain("www.amazon.cn").setSleepTime(3000).setRetryTimes(3).setCycleRetryTimes(3);
    }
}
