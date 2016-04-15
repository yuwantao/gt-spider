package cn.readgo.spider.crawlerjob;

import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Page;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.Map;

/**
 * Created by ldy on 2016/1/27.
 */
public class TaskScheduler {
    //限定的同时执行的任务总数
    private static final int TASK_LIMIT_TOTAL = 10;
    //限定针对同一个站点,同时执行的任务总数
    private static final int TASK_LIMIT_EACH_SITE = 1;
    //每个任务对应的spider
    private static Map<String, Spider> spiderMap = Maps.newHashMap();
    //计数 针对各个站点的正在执行的任务
    private static Map<String, Integer> countMap = Maps.newHashMap();


    private void run() {
        List<CrawlerTask> tasks = CrawlerTask.dao.find("select * from crawler_task where status='2'");
        int newCount = TASK_LIMIT_TOTAL - tasks.size();

        int pageNumber = 1;
        while(true) {
            Page<CrawlerTask> newTasks = CrawlerTask.dao.paginate(pageNumber, newCount, "select * from crawler_task where status='1' order by id asc");
            for (CrawlerTask task : tasks) {
                Long taskId = task.getLong(CrawlerTask.ID);
                String targetSite = task.getStr(CrawlerTask.TARGET_SITE);
                Integer eachSiteTaskCount = countMap.get(targetSite);

            }
        }
    }

    private Spider createSpider(CrawlerTask task) {

        return null;
    }


}
