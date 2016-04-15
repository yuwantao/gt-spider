package cn.readgo.spider;

import cn.readgo.model.SubjectBook;
import cn.readgo.pageprocessor.SubjectBookPageProcessor;
import cn.readgo.pipeline.SubjectBookPipeline;
import cn.readgo.schedule.RedisScheduler;
import cn.readgo.utils.FileUtil;
import cn.readgo.utils.Sys;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * Created by ldy on 2016/1/22.
 */
public class SubjectBookSpider extends AbstractSpider {

    private static final String PATHNAME = "D:\\keywords";

    public static void main(String[] args) {
        new SubjectBookSpider().createSpider();
    }

    private void createSpider() {
        initDbConfig();

        initUserAgents();

        Spider spider = Spider.create(new SubjectBookPageProcessor()).addPipeline(new SubjectBookPipeline());
        spider.setScheduler(new RedisScheduler());
        spider.setExitWhenComplete(true);
        spider.setUUID("subject_book.amazon.com");
        spider.thread(1);

        addTargetRequest(spider);

//        Request request = new Request("http://www.amazon.com/Basic-Training-Mathematics-Fitness-Students/dp/0306450364/ref=sr_1_1?s=books&ie=UTF8&qid=1453700155&sr=1-1&keywords=Mathematics");
//        request.putExtra("detail", "1");
//        request.putExtra("subjectCode", "1111111111111");
//        spider.addRequest(request);

        spider.start();

    }

    /**
     * 从文件读种子关键词，拼接url添加到待抓取队列
     * @param spider
     */
    private void addTargetRequest(Spider spider) {
        List<String> lines = FileUtil.readFileByLinesToList(PATHNAME);
        for (String line : lines) {
            String[] array = line.split(",");
            String subjectCode = array[0].trim();
            String keyword = array[1].trim();
            Request request = new Request("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=" + keyword.replaceAll(" ", "+"));
            request.putExtra("subjectCode", subjectCode);
            spider.addRequest(request);
        }
    }

    @Override
    protected void addTableConfig(ActiveRecordPlugin activeRecordPlugin) {
        activeRecordPlugin.addMapping("amazon_subject_book", "id", SubjectBook.class);
    }

    private void initUserAgents() {
        List<String> list = FileUtil.readFileByLinesToList(this.getClass().getResource("/useragent").getFile());
        Sys.uaQueue.addAll(list);
    }
}
