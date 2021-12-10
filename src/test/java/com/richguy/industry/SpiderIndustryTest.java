package com.richguy.industry;

import com.richguy.service.IndustryService;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.ThreadUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class SpiderIndustryTest {

    @Test
    public void industryTest() throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        var industryService = new IndustryService();
        var industryStocks = industryService.spiderIndustry();

        var builder = new StringBuilder();
        for (var industryStock : industryStocks) {
            builder.append(industryStock.toString()).append(FileUtils.LS);
        }
        FileUtils.writeStringToFile(new File("C:\\Users\\JM\\Desktop\\photo\\industry.txt"), builder.toString());
        System.out.println(industryStocks.size());
    }


    @Test
    public void getCookie() {
        var command = StringUtils.format("node {} {}", "E:\\mygithub\\richguy\\spider\\spider.js", "http://q.10jqka.com.cn/gn/detail/field/264648/order/desc/page/1/ajax/1/code/301558");
        var result = OSUtils.execCommand(command);
        System.out.println(result);
        var cookie = "";
        if (StringUtils.isNotEmpty(result) && result.contains("vvvv=")) {
            cookie = StringUtils.substringAfterFirst(result, "; v=");

        } else {
            cookie = StringUtils.substringAfterFirst(result, "v=");
        }
        System.out.println(cookie);
    }

    @Test
    public void test() {
        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver", "E:\\软件\\chromedriver.exe");

        var driver = new ChromeDriver();
        driver.get("http://q.10jqka.com.cn/gn/detail/field/264648/order/desc/page/1/ajax/1/code/301558");

        String title = driver.getTitle();
        System.out.printf(title);

        ThreadUtils.sleep(3000000);

        driver.close();

    }
}
