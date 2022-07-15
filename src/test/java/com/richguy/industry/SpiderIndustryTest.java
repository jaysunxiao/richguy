package com.richguy.industry;

import com.richguy.model.IndustryStockVO;
import com.richguy.model.packet.SpiderIndustry;
import com.richguy.model.packet.SpiderStock;
import com.richguy.resource.IndustryResource;
import com.richguy.util.IndustryUtils;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.orm.lpmap.FileChannelMap;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.interpreter.ExcelResourceReader;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author godotg
 * @version 3.0
 */
@Ignore
public class SpiderIndustryTest {

    public static final long KEY = 1;
    private static final Logger logger = LoggerFactory.getLogger(SpiderIndustryTest.class);

    @Test
    public void industryToRealIndustryTest() throws IOException, ParserConfigurationException, SAXException {
        industryToRealIndustry();
    }

    @Test
    public void industryStockTest() throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        var industryStocks = spiderIndustry();

        var builder = new StringBuilder();
        for (var industryStock : industryStocks) {
            builder.append(industryStock.toString()).append(FileUtils.LS);
        }
        FileUtils.writeStringToFile(new File("industry.txt"), builder.toString());
        System.out.println(industryStocks.size());
    }


    /**
     * 爬取行业的真正代码
     */
    public void industryToRealIndustry() throws IOException {
        var reader = new ExcelResourceReader();
        var list = (List<IndustryResource>) reader.read(ClassUtils.getFileFromClassPath("excel/IndustryResource.xlsx"), IndustryResource.class);

        for (var industryResource : list) {
            var code = industryResource.getCode();
            var realCode = 0;
            try {
                realCode = IndustryUtils.realCode(code);
            } catch (Exception e) {
            }
            var info = StringUtils.format("{}{}{}{}{}", code, StringUtils.TAB_ASCII, industryResource.getName(), StringUtils.TAB_ASCII, realCode);
            System.out.println(info);
        }
    }

    /**
     * 爬取同花顺所有行业信息
     */
    public List<IndustryStockVO> spiderIndustry() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        ProtocolManager.initProtocol(Set.of(SpiderStock.class, SpiderIndustry.class));

        var reader = new ExcelResourceReader();
        var list = (List<IndustryResource>) reader.read(ClassUtils.getFileFromClassPath("excel/IndustryResource.xlsx"), IndustryResource.class);
        var resourceMap = list.stream().collect(Collectors.toMap(key -> key.getCode(), value -> value));
        var map = new FileChannelMap<>("industryDb", SpiderIndustry.class);
        if (map.get(KEY) == null) {
            map.put(KEY, SpiderIndustry.valueOf(1, 1));
        }
        var spiderPacket = map.get(KEY);

        var industryStocks = new ArrayList<IndustryStockVO>();

        var max = 100_0000;
        for (int i = spiderPacket.getIndex(); i < 100_0000; i++) {
            if (i == max - 1) {
                logger.info("已经完成");
                return industryStocks;
            }
            var industryResource = resourceMap.get(i);
            if (industryResource == null) {
                continue;
            }

            spiderPacket = map.get(KEY);
            var stockUrlTemplate = String.valueOf(industryResource.getCode()).startsWith("3")
                    ? "http://q.10jqka.com.cn/gn/detail/field/264648/order/desc/page/{}/ajax/1/code/{}"
                    : "http://q.10jqka.com.cn/thshy/detail/field/199112/order/desc/page/{}/ajax/1/code/{}";

            var count = spiderPacket.getCount();
            for (int j = count; j <= 227; j++) {
                var url = StringUtils.format(stockUrlTemplate, j, industryResource.getCode());
                var command = StringUtils.format("node {} {}", "D:\\github\\richguy\\spider\\spider.js", url);
                var str = OSUtils.execCommand(command);

                if (str.contains("window.location.href=\"/") || str.contains("Nginx forbidden")) {
                    System.out.println(StringUtils.format("---------------------------{}", i));
                    map.put(KEY, SpiderIndustry.valueOf(i, j));
                    map.close();
                    return industryStocks;
                }

                str = StringUtils.substringAfterFirst(str, "<tbody>");
                str = StringUtils.substringBeforeLast(str, "</tbody>");
                str = StringUtils.format("<tbody> {} </tbody>", str);
                str = str.replaceAll("alt=\"\">", "/>");

                var documentBuilderFactory = DocumentBuilderFactory.newInstance();
                var documentBuilder = documentBuilderFactory.newDocumentBuilder();
                var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(str)));
                var stockElements = DomUtils.getChildElements(document.getDocumentElement());

                if (CollectionUtils.isEmpty(stockElements) || str.contains("暂无成份股数据")) {
                    map.put(KEY, SpiderIndustry.valueOf(i + 1, 1));
                    logger.info("*************************************[{}]爬取完毕", industryResource.getCode());
                    break;
                }

                for (var stockElement : stockElements) {
                    var stockAttributes = DomUtils.getChildElements(stockElement);
                    var code = stockAttributes.get(1).getTextContent();
                    var industryStock = IndustryStockVO.valueOf(String.valueOf(industryResource.getCode()), code);
                    industryStocks.add(industryStock);
                    System.out.println(industryStock);
                }
            }
        }

        return industryStocks;
    }

}
