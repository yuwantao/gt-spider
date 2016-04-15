package cn.readgo.schedule;

import cn.com.cibtc.redis.IRedis;
import cn.com.cibtc.redis.RedisFactory;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Use Redis as url scheduler for distributed crawlers.<br>
 * 2016/1/15 重写RedisScheduler做功能扩展，集成自己的redis客户端.<br>
 * @author code4crafter@gmail.com <br>
 * @author ldy
 * @since 0.2.0
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private IRedis shardedRedis;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    public RedisScheduler() {
        this.shardedRedis = RedisFactory.getShardedRedis();
        setDuplicateRemover(this);
    }

    public RedisScheduler(IRedis shardedRedis) {
        this.shardedRedis = shardedRedis;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        shardedRedis.delString(getSetKey(task));
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        boolean isDuplicate = shardedRedis.isSetMemberExists(getSetKey(task), request.getUrl());
        if (!isDuplicate) {
            shardedRedis.addSetString(getSetKey(task), request.getUrl(), -1);
        }
        return isDuplicate;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        shardedRedis.rpush(getQueueKey(task), request.getUrl(), -1);
        if (request.getExtras() != null) {
            String field = DigestUtils.shaHex(request.getUrl());
            String value = JSON.toJSONString(request);
            shardedRedis.setHashString((ITEM_PREFIX + task.getUUID()), field, value, -1);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        String url = shardedRedis.lpop(getQueueKey(task));
        if (url == null) {
            return null;
        }
        String key = ITEM_PREFIX + task.getUUID();
        String field = DigestUtils.shaHex(url);
        String requestStr = shardedRedis.getHashString(key, field);
        if (requestStr != null && requestStr.trim().length() > 0) {
            Request o = JSON.parseObject(requestStr, Request.class);
            shardedRedis.delHashField(key, field);
            return o;
        }
        Request request = new Request(url);
        return request;
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Long size = shardedRedis.llen(getQueueKey(task));
        return size.intValue();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Long size = shardedRedis.getSetSize(getSetKey(task));
        return size.intValue();
    }
}
