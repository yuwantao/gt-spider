package cn.readgo.utils;

import com.google.common.collect.Queues;

import java.util.Queue;

/**
 * <p>标题：Sys </p>
 * <p>
 *    功能描述：系统常量类
 * </p>
 * <p>创建日期：2016/2/17 10:41 </p>
 * <p>作者：lidongyang </p>
 * <p>版本：1.0 </p>
 */
public class Sys {
    /**
     * user agent队列
     */
    public static final Queue<String> uaQueue = Queues.newArrayDeque();
}
