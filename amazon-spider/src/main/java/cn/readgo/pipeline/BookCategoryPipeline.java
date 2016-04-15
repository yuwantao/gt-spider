package cn.readgo.pipeline;

import cn.com.cibtc.redis.RedisFactory;
import cn.readgo.model.BookCategory;
import org.apache.commons.codec.digest.DigestUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;

/**
 * Created by ldy on 2016/1/13.
 */
public class BookCategoryPipeline implements Pipeline {
    private static final String ITEM_PREFIX = "item_";

    @Override
    public void process(ResultItems resultItems, Task task) {
        int bookId = (Integer) resultItems.getRequest().getExtra("bookId");
        String isbn = resultItems.getRequest().getExtra("isbn").toString();//条形码或isbn
        String isbn10 = resultItems.getRequest().getExtra("isbn10").toString();//条形码或isbn
        String bookCategory = resultItems.get("bookCategory");
        boolean bool = new BookCategory().set("BOOK_ID", bookId)
                .set("ISBN", isbn)
                .set("ISBN10", isbn10)
                .set("CATEGORY", bookCategory)
                .set("CREATE_TIME", new Date())
                .save();
        //删除已经使用的附加信息，释放redis内存
        if(bool) {
            String field = DigestUtils.shaHex(resultItems.getRequest().getUrl());
            RedisFactory.getShardedRedis().delHashField((ITEM_PREFIX + task.getUUID()), field);
        }
    }
}
