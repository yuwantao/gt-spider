package cn.readgo.spider.crawlerjob;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by ldy on 2016/1/27.
 */
public class CrawlerTask extends Model<CrawlerTask> {
    public static final CrawlerTask dao = new CrawlerTask();
    /**
     * 任务id
     */
    public static final String ID = "id";
    /**
     * 客户端（任务提交者）
     */
    public static final String CLIENT = "client";
    /**
     * 目标站点（抓取源）
     */
    public static final String TARGET_SITE = "target_site";
    /**
     * 任务状态（未执行、执行中、已执行）
     */
    public static final String STATUS = "status";
    /**
     * 任务创建时间
     */
    public static final String CREATE_TIME = "create_time";
    /**
     * 任务更新时间（主要是 更新状态的时候更新该值）
     */
    public static final String UPDATE_TIME = "update_time";
}
