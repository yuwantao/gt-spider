package cn.readgo.spider.crawlerjob;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by ldy on 2016/1/27.
 */
public class CrawlerTaskDetail extends Model<CrawlerTaskDetail> {
    public static final CrawlerTaskDetail dao = new CrawlerTaskDetail();
    /**
     * 任务详情id
     */
    public static final String ID = "id";
    /**
     * 任务id
     */
    public static final String TASK_ID = "task_id";
    /**
     * isbn/keyword/url等
     */
    public static final String CONTENT = "content";
    /**
     * 任务详情类型（isbn/keyword/url等）
     */
    public static final String TYPE = "type";
    /**
     * 任务执行状态（未执行、执行中、已执行）
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
