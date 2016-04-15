package cn.readgo.spider;

import cn.com.cibtc.redis.IRedis;
import cn.com.cibtc.redis.RedisFactory;
import cn.readgo.model.Book;
import cn.readgo.model.BookCategory;
import cn.readgo.pageprocessor.BookCategoryPageProcessor;
import cn.readgo.pipeline.BookCategoryPipeline;
import cn.readgo.schedule.RedisScheduler;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.List;

/**
 * Created by ldy on 2016/1/13.
 */
public class BookCategorySpider {
    private static final Logger logger = Logger.getLogger(BookCategorySpider.class);

    private static final int REQUEST_LIMIT = 80;//队列里边保持的最少抓取任务数
    private static final int PAGE_START = 171;//从mysql取数据的分页起始值
    private static final int PAGE_SIZE = 200;//分页大小

    public static void main(String[] args) {
        initDbConfig();

        Spider spider = Spider.create(new BookCategoryPageProcessor()).addPipeline(new BookCategoryPipeline());
        spider.setScheduler(new RedisScheduler(RedisFactory.getShardedRedis()));
        spider.thread(1).setUUID("www.amazon.cn");

//        addRequest(spider);

        spider.start();

        //监控抓取队列的任务数，不足的时候就添加任务
        Thread t = new Thread(new RequestHandler(spider));
        t.setDaemon(false);
        t.start();
    }

    /**
     * 初始化数据库配置信息
     */
    private static void initDbConfig() {
        C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://192.168.10.203:3306/DEV_CIBTC_PDS", "root", "123456");
        c3p0Plugin.start();

        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(c3p0Plugin);
        activeRecordPlugin.addMapping("BOOK", "BOOK_ID", Book.class);
        activeRecordPlugin.addMapping("AMAZON_BOOK_CATEGORY", "ID", BookCategory.class);
        activeRecordPlugin.start();
    }

    /**
     * 检查队列里边剩下的抓取任务，不足的时候添加任务到队列
     */
    private static class RequestHandler implements Runnable {
        private static boolean flag = true;
        private Spider spider;

        public RequestHandler(Spider spider) {
            this.spider = spider;
        }

        @Override
        public void run() {
            logger.info("request handler started...");
            while(true) {
                int leftRequestsCount = -1;
                if (spider.getScheduler() instanceof MonitorableScheduler) {
                    leftRequestsCount = ((MonitorableScheduler) spider.getScheduler()).getLeftRequestsCount(spider);
                }
                if (leftRequestsCount < REQUEST_LIMIT) {
                    if (flag) {
                        logger.info("left request count:" + leftRequestsCount + ", start to add new request..");
                        flag = addRequest(spider);
                    }
                }
            }
        }

    }

    /**
     * 添加抓取任务
     * @param spider
     * @return true：还有待抓取的任务，false：没有待抓取的任务
     */
    private static boolean addRequest(Spider spider) {
        //查询分页起始页码
        IRedis shardedRedis = RedisFactory.getShardedRedis();
        String pageStartStr = shardedRedis.getString("book_cate_page_start");
        String lastBookId = shardedRedis.getString("book_cate_book_id");
//        int pageStart = StringUtils.isNotEmpty(pageStartStr) ? Integer.valueOf(pageStartStr) : PAGE_START;
        int bookId = StringUtils.isNotEmpty(lastBookId) ? Integer.valueOf(lastBookId) : 0;

        logger.info("book id:" + bookId);

        //从数据库查询数据，加入抓取队列
        String isbn10 = null;
        String isbn = null;
        Request request = null;
        Page<Book> paginate = Book.dao.paginate(0, PAGE_SIZE, "select * from BOOK where BOOK_ID > ? and ISBN like '978%'", bookId);
        List<Book> list = paginate.getList();
        if(CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (Book book : list) {
            bookId = book.getInt("BOOK_ID");
            isbn10 = book.getStr("ISBN10");
            isbn = book.getStr("ISBN");

            if(StringUtils.isNotEmpty(isbn10)) {
                request = new Request("http://www.amazon.cn/dp/" + isbn10);
                request.putExtra("bookId", bookId);
                request.putExtra("isbn", isbn);
                request.putExtra("isbn10", isbn10);
                spider.addRequest(request);
            }
        }
        //设置新的起始页码
//        pageStart += 1;
//        shardedRedis.setString("book_cate_page_start", "" + pageStart, -1);
        shardedRedis.setString("book_cate_book_id", "" + bookId, -1);

        return true;
    }

}
