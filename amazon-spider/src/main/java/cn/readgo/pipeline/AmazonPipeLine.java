package cn.readgo.pipeline;

import cn.readgo.spider.AmazonSpider;
import cn.readgo.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ldy on 2016/1/11.
 */
public class AmazonPipeLine implements Pipeline {

    private static final File file = new File(AmazonSpider.class.getProtectionDomain().getCodeSource()
            .getLocation().getFile());

    public void process(ResultItems resultItems, Task task) {
        List<String> titles = resultItems.get("titles");
        List<String> asins = resultItems.get("asins");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < titles.size(); i++) {
            stringBuilder.append(asins.get(i))
                        .append("   ")
                        .append(titles.get(i));
            if(i != titles.size() - 1) {
                stringBuilder.append("\r\n");
            }
        }
        String listTilte = resultItems.get("listTitle");
        String pathname = file.getParent() + "\\" + listTilte + "——" + task.getUUID() + ".txt";
        FileUtil.writeContent(pathname, stringBuilder.toString(), true);

        String end = resultItems.get("end");
        String pageNumber = resultItems.get("pageNumber");
        if (StringUtils.isEmpty(end)) {
            System.out.println("第" + pageNumber + "页抓取完成..");
        } else {
            System.out.println("第" + pageNumber + "页(最后一页)抓取完成。文件存储路径：" + pathname);
        }
    }
}
