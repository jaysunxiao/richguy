package com.richguy.service;

import com.richguy.model.stock.IndustryStock;
import com.richguy.packet.SpiderIndustry;
import com.richguy.packet.SpiderStock;
import com.richguy.resource.IndustryResource;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.orm.lpmap.FileChannelMap;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.interpreter.ExcelResourceReader;
import com.zfoo.util.math.RandomUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class IndustryService {

    private static final Logger logger = LoggerFactory.getLogger(IndustryService.class);

    public static final long KEY = 1;

    public List<IndustryStock> spiderIndustry() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        ProtocolManager.initProtocol(Set.of(SpiderStock.class, SpiderIndustry.class));

        var reader = new ExcelResourceReader();
        var list = (List<IndustryResource>) reader.read(ClassUtils.getFileFromClassPath("excel/IndustryResource.xlsx"), IndustryResource.class);
        var resourceMap = list.stream().collect(Collectors.toMap(key -> key.getCode(), value -> value));
        var map = new FileChannelMap<>("industryDb", SpiderIndustry.class);
        if (map.get(KEY) == null) {
            map.put(KEY, SpiderIndustry.valueOf(1, 1));
        }
        var spiderPacket = map.get(KEY);

        var industryStocks = new ArrayList<IndustryStock>();

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
                    var industryStock = IndustryStock.valueOf(String.valueOf(industryResource.getCode()), code);
                    industryStocks.add(industryStock);
                    System.out.println(industryStock);
                }
            }
        }

        return industryStocks;
    }

}
