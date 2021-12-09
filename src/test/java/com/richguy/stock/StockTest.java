package com.richguy.stock;

import com.richguy.service.StockService;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class StockTest {

    private static final Logger logger = LoggerFactory.getLogger(StockTest.class);

    @Test
    public void stockTest() throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        var stockService = new StockService();
        var stocks = stockService.spiderStocks();

        var builder = new StringBuilder();
        for (var stock : stocks) {
            builder.append(stock.toString()).append(FileUtils.LS);
        }
        FileUtils.writeStringToFile(new File("C:\\Users\\JM\\Desktop\\photo\\stock.txt"), builder.toString());
        System.out.println(stocks.size());
    }

    @Test
    public void test() {
        var command = StringUtils.format("node {} {}", "E:\\mygithub\\richguy\\spider\\spider.js", "http://q.10jqka.com.cn/index/index/board/all/field/zdf/order/desc/page/1/ajax/1/");
        var html = OSUtils.execCommand(command);

        if (StringUtils.isBlank(html)) {
            logger.error("执行node命令[command:{}]发生返回了空的结果", command);
            return;
        }
        if (html.startsWith("zfoo_error")) {
            logger.error("执行node命令[command:{}]发生内部错误[error:{}]", command, html);
            return;
        }

        System.out.println(html);
    }

}
