package com.richguy.industry;

import com.richguy.spider.SpiderIndustryService;
import com.zfoo.protocol.util.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
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
    public void industryToRealIndustryTest() throws IOException, ParserConfigurationException, SAXException {
        var industryService = new SpiderIndustryService();
        industryService.industryToRealIndustry();
    }

    @Test
    public void industryStockTest() throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        var industryService = new SpiderIndustryService();
        var industryStocks = industryService.spiderIndustry();

        var builder = new StringBuilder();
        for (var industryStock : industryStocks) {
            builder.append(industryStock.toString()).append(FileUtils.LS);
        }
        FileUtils.writeStringToFile(new File("industry.txt"), builder.toString());
        System.out.println(industryStocks.size());
    }

}
