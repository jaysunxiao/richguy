package com.richguy.stock;

import com.richguy.service.StockService;
import com.zfoo.protocol.util.FileUtils;
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
public class SpiderStockTest {

    private static final Logger logger = LoggerFactory.getLogger(SpiderStockTest.class);

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

}
