package cn.readgo.pipeline;

import cn.readgo.model.SubjectBook;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;

/**
 * Created by ldy on 2016/1/25.
 */
public class SubjectBookPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        String subjectCode = resultItems.get("subjectCode");
        String bookTitle = resultItems.get("bookTitle");
        String isbn10 = resultItems.get("isbn10");
        String isbn13 = resultItems.get("isbn13");

        SubjectBook subjectBook = new SubjectBook();
        subjectBook.set("subject_code", subjectCode);
        subjectBook.set("book_title", bookTitle);
        subjectBook.set("isbn10", isbn10);
        subjectBook.set("isbn", isbn13);
        subjectBook.set("create_time", new Date());
        subjectBook.save();
    }
}
