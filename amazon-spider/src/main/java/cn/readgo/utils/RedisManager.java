package cn.readgo.utils;

import cn.com.cibtc.redis.IRedis;
import cn.com.cibtc.redis.RedisFactory;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by ldy on 2016/1/14.
 */
public class RedisManager {
    private static final Logger logger = Logger.getLogger(RedisManager.class);

    public static void main(String[] args) {
        IRedis shardedRedis = RedisFactory.getShardedRedis();
//        shardedRedis.setString("test1", "ldddddddy", -1);
        for(int i = 1; i <= 10; i++) {
            shardedRedis.lpush("list", "" + i, -1);
        }

        List<String> list = shardedRedis.lrange("list", 0, 30);
        long len = shardedRedis.llen("list");
        System.out.println(list);

        for(int i = 1; i <= 10; i++) {
            shardedRedis.rpush("list", "" + i, -1);
        }

        len = shardedRedis.llen("list");
        list = shardedRedis.lrange("list", 0, 30);
        System.out.println(list);

    }

}
