package cn.readgo.pageprocessor;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>标题：OzonPageProcessor </p>
 * <p>
 * 功能描述：
 * </p>
 * <p>创建日期：2016/3/3 16:50 </p>
 * <p>作者：ldy </p>
 * <p>版本：1.0 </p>
 */
public class OzonPageProcessor implements PageProcessor {

    private static final String PAGE_PATHNAME = "D:\\ozon-data\\detail pages\\";

    @Override
    public void process(Page page) {
//        String text = page.getRawText();
//        String id = (String) page.getRequest().getExtra("id");
//        FileUtil.writeContent(PAGE_PATHNAME + id, text, true);
        page.setSkip(true);
        String html = page.getHtml().toString();
        Document doc = Jsoup.parse(html);
        String isbn = doc.select("div:containsOwn(isbn) + div").html();
        String title = doc.select("h1.bItemName").text();
        String bookId = doc.select("div#jsArticleView").text().split(":")[1];
        String price = doc.select("div.bSale_BasePriceCover").text();
        String stock = doc.select("div.eOneTile_priceStock").text();
        String titleBak = doc.select("div:containsOwn(Ориг) + div").text();
        String author = doc.select("div:containsOwn(Автор) + div").text();
        String editor = doc.select("div:containsOwn(Редактор) + div").text();
        String translator = doc.select("div:containsOwn(Переводчик) + div").text();
        String seriesBookStr = doc.select("div.eProductDescriptionText_text").text();
        Pattern pattern = Pattern.compile("проекта\\s+\"(.+)\"");
        String seriesBook = "";
        Matcher matcher = pattern.matcher(seriesBookStr);
        if (matcher.find()) {
            seriesBook = matcher.group(1);
        }
        String language = doc.select("div:containsOwn(Язык) + div").text();
        String press = doc.select("div:containsOwn(Издательство) + div").text();
        String pubDate = doc.select("div:containsOwn(Год выпуска) + div").text();

        String versionAvailable = "";
        pattern = Pattern.compile("\"Type\":\"([\\p{L},\\s]*)");
        pattern.matcher(html);
        List<String> types = new ArrayList<>();
        while (matcher.find()) {
            types.add(matcher.group(1));
        }
        pattern = Pattern.compile("\"ReleaseYear\":\"(\\d+\\s+г.)");
        pattern.matcher(html);
        List<String> releaseYears = new ArrayList<>();
        while (matcher.find()) {
            releaseYears.add((matcher.group(1)));
        }
        for (int i=0;i<types.size();i++) {
            versionAvailable = types.get(i) + " " + releaseYears.get(i) + ";";
        }

        String pagerNumber = doc.select("div:containsOwn(Количество) + div").text();
        String folio = doc.select("div:containsOwn(Формат издания) + div").text();
        String circulation = doc.select("div:containsOwn(Тираж) + div").text();
        String binding = doc.select("div:containsOwn(Переплет) + div").text();
        String paper = doc.select("div:containsOwn(Мелованная бумага)").text();
        String illustrations = doc.select("div:containsOwn(Цветные иллюстрации) + div").text();
        String content = doc.select(":containsOwn(Содержание) ~ span").text();
        String introduction = doc.select("div.eProductDescriptionText_text").text();
        String classification = doc.select("div.bBreadCrumbs").text();
        String basicInfo = doc.select("div.bItemProperties").text();

	    //populate to excel
	    File file = new File("e://download//ozon_data.xls");
	    InputStream in = null;
	    try {
		    in = new FileInputStream(file);
		    HSSFWorkbook workbook = new HSSFWorkbook(in);
		    HSSFSheet sheet = workbook.getSheetAt(0);
		    int lastRowNum = sheet.getLastRowNum();
		    HSSFRow lastRow = sheet.getRow(lastRowNum);
		    HSSFCell lastIdCell = lastRow.getCell(0);
		    lastIdCell.setCellType(Cell.CELL_TYPE_STRING);
		    String idStr = lastIdCell.getStringCellValue();

		    HSSFRow row = sheet.createRow(lastRowNum + 1);

		    HSSFCell cell = row.createCell(0);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(Integer.valueOf(idStr) + 1);

		    cell = row.createCell(1);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(page.getUrl().get());

		    cell = row.createCell(2);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(isbn);

		    cell = row.createCell(3);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(title);

		    cell = row.createCell(4);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(bookId);

		    cell = row.createCell(5);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(price);

		    cell = row.createCell(6);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(stock);

		    cell = row.createCell(7);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(titleBak);

		    cell = row.createCell(8);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(author);

		    cell = row.createCell(9);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(editor);

		    cell = row.createCell(10);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(translator);

		    cell = row.createCell(11);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(seriesBook);

		    cell = row.createCell(12);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(language);

		    cell = row.createCell(13);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(press);

		    cell = row.createCell(14);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(pubDate);

		    cell = row.createCell(15);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(versionAvailable);

		    cell = row.createCell(16);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(pagerNumber);

		    cell = row.createCell(17);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(folio);

		    cell = row.createCell(18);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(circulation);

		    cell = row.createCell(19);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(binding);

		    cell = row.createCell(20);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(paper);

		    cell = row.createCell(21);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(illustrations);

		    cell = row.createCell(22);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(content);

		    cell = row.createCell(23);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(introduction);

		    cell = row.createCell(24);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(classification);

		    cell = row.createCell(25);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue(basicInfo);

		    cell = row.createCell(26);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue("");

		    FileOutputStream out;
		    try {
			    out = new FileOutputStream(file);
			    workbook.write(out);
			    out.close();
		    } catch (FileNotFoundException e) {
			    e.printStackTrace();
		    } catch (IOException e) {
			    e.printStackTrace();
		    }
	    } catch (FileNotFoundException e) {
		    e.printStackTrace();
	    } catch (IOException e) {
		    e.printStackTrace();
	    }

//        OzonBook ozonBook = new OzonBook();
//        ozonBook.setUrl(page.getUrl().get());
//        ozonBook.setIsbn(isbn);
    }

    @Override
    public Site getSite() {
        Site site = Site.me().setDomain("ozon.ru");
        site.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
        site.addHeader("Referer", "http://www.ozon.ru/catalog/1138366/?newperiod=1");
        site.setTimeOut(10 * 1000);
        site.setRetryTimes(5);
        site.setCycleRetryTimes(10);
        site.setSleepTime(5000);
        return site;
    }
}
