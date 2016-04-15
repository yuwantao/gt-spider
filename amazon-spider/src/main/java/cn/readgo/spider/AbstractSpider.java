package cn.readgo.spider;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * Created by ldy on 2016/1/25.
 */
public abstract class AbstractSpider {
    /**
     * 初始化数据库配置
     */
    protected void initDbConfig() {
        C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://192.168.10.203:3306/DEV_CIBTC_PDS", "root", "123456");
        c3p0Plugin.start();

        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(c3p0Plugin);
        addTableConfig(activeRecordPlugin);
        activeRecordPlugin.start();
    }

    /**
     * 添加表映射
     * @param activeRecordPlugin
     */
    protected abstract void addTableConfig(ActiveRecordPlugin activeRecordPlugin);

}
