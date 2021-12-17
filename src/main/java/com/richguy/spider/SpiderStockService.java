package com.richguy.spider;

import com.richguy.packet.SpiderIndustry;
import com.richguy.packet.SpiderStock;
import com.richguy.spider.model.StockVO;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.orm.lpmap.FileChannelMap;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class SpiderStockService {

    public static final long KEY = 1;
    private static final Logger logger = LoggerFactory.getLogger(SpiderStockService.class);

    public List<StockVO> spiderStocks() throws IOException, ParserConfigurationException, SAXException {
        ProtocolManager.initProtocol(Set.of(SpiderStock.class, SpiderIndustry.class));

        var stocks = new ArrayList<StockVO>();

        var stockUrlTemplate = "http://q.10jqka.com.cn/index/index/board/all/field/zdf/order/desc/page/{}/ajax/1/";

        var map = new FileChannelMap<>("stockDb", SpiderStock.class);
        if (map.get(KEY) == null) {
            map.put(KEY, SpiderStock.valueOf(1));
        }
        var spiderPacket = map.get(KEY);
        var count = spiderPacket.getCount();

        for (int i = count; i <= 227; i++) {
            var url = StringUtils.format(stockUrlTemplate, i);
            var command = StringUtils.format("node {} {}", "D:\\github\\richguy\\spider\\spider.js", url);
            var str = OSUtils.execCommand(command);

            if (str.contains("window.location.href=\"/") || str.contains("Nginx forbidden")) {
                System.out.println(StringUtils.format("---------------------------{}", i));
                map.put(KEY, SpiderStock.valueOf(i));
                map.close();
                break;
            }


            str = StringUtils.substringAfterFirst(str, "<tbody>");
            str = StringUtils.substringBeforeLast(str, "</tbody>");
            str = StringUtils.format("<tbody> {} </tbody>", str);
            str = str.replaceAll("alt=\"\">", "/>");

            var documentBuilderFactory = DocumentBuilderFactory.newInstance();
            var documentBuilder = documentBuilderFactory.newDocumentBuilder();
            var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(str)));
            var stockElements = DomUtils.getChildElements(document.getDocumentElement());

            if (CollectionUtils.isEmpty(stockElements)) {
                System.out.println(StringUtils.format("---------------------------{}", i));
                map.put(KEY, SpiderStock.valueOf(i));
                map.close();
                break;
            }

            for (var stockElement : stockElements) {
                var stockAttributes = DomUtils.getChildElements(stockElement);
                var code = stockAttributes.get(1).getTextContent();
                var name = stockAttributes.get(2).getTextContent();
                var nowPrice = stockAttributes.get(3).getTextContent();
                var riseRatio = stockAttributes.get(4).getTextContent();
                var riseNum = stockAttributes.get(5).getTextContent();
                var increaseRatio = stockAttributes.get(6).getTextContent();
                var turnoverRatio = stockAttributes.get(7).getTextContent();
                var volumeRatio = stockAttributes.get(8).getTextContent();
                var vibration = stockAttributes.get(9).getTextContent();
                var turnover = stockAttributes.get(10).getTextContent();
                var floatingStock = stockAttributes.get(11).getTextContent();
                var marketValue = stockAttributes.get(12).getTextContent();
                var pe = stockAttributes.get(13).getTextContent();
                var stock = StockVO.valueOf(code, name, nowPrice, riseRatio, riseNum, increaseRatio, turnoverRatio, volumeRatio, vibration, turnover, floatingStock, marketValue, pe);
                stocks.add(stock);
                System.out.println(stock);
            }
            logger.info("*************************************[{}]爬取完毕", i);
        }

        return stocks;
    }

}
