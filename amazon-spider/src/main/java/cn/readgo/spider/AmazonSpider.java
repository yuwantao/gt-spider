package cn.readgo.spider;

import cn.readgo.pageprocessor.AmazonPageProcessor;
import cn.readgo.pipeline.AmazonPipeLine;
import cn.readgo.utils.DateUtils;
import us.codecraft.webmagic.Spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by ldy on 2016/1/11.
 */
public class AmazonSpider {
    public static void main(String[] args) throws IOException {

        System.out.println("////////////////////////////////////////////////////");
        System.out.println("             亚马逊图书信息抓取                                                                      ");
        System.out.println("使用方法：                                                                                                                                   ");
        System.out.println("   1、输入<url>回车        抓取<url>下的图书的ISBN及标题                                     ");
        System.out.println("   2、exit                 退出程序                                                                                ");
        System.out.println("////////////////////////////////////////////////////");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        String cmd;
        System.out.print("请输入亚马逊(列表页URL)>,回车开始抓取：");
        while ((cmd = reader.readLine()) != null) {
            if(cmd == null || cmd.trim().length() == 0) {
                System.out.println("参数错误，请输入正确的列表页URL！");
                System.out.print("亚马逊(列表页URL)>");
                continue;
            }
            if(cmd.equalsIgnoreCase("exit") || !cmd.startsWith("http")) {
                break;
            } else {
                handleISBNs(cmd.trim());
            }
        }

//        handleISBNs("http://www.amazon.cn/s/ref=sr_nr_p_6_1?fst=as%3Aoff&rh=n%3A658390051%2Cn%3A!658391051%2Cn%3A2045366051%2Cn%3A2105882051%2Cn%3A2106247051%2Cp_6%3AAP2G42SQ8EMNK&bbn=2106247051&ie=UTF8&qid=1452476923&rnid=51326071");
    }

    private static void handleISBNs(String url) {
        //
        Spider spider = Spider.create(new AmazonPageProcessor()).addPipeline(new AmazonPipeLine()).addUrl(url);
        String dateStr = DateUtils.getDateStrYYYY_MM_DD_HH_MI_SS(new Date());
        dateStr = dateStr.replaceAll(":", "-");
        spider.setUUID(dateStr);
        spider.start();
        System.out.println("开始抓取图书信息..");
    }

}
