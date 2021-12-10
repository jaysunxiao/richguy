package com.richguy.service;

import com.richguy.model.stock.IndustryStock;
import com.richguy.packet.SpiderIndustry;
import com.richguy.packet.SpiderStock;
import com.richguy.resource.IndustryResource;
import com.zfoo.orm.lpmap.FileChannelMap;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.interpreter.ExcelResourceReader;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class IndustryService {

    public static final long KEY = 1;
    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "Referer", "http://q.10jqka.com.cn/",
            "hexin-v",
            "A-yTc4JuoQ75g7UyP31o4LAevcEbpdkrkv-klkYp-5U7YIL_brVg3-JZdTiV",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    public List<IndustryStock> spiderIndustry() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        ProtocolManager.initProtocol(Set.of(SpiderStock.class, SpiderIndustry.class));

        var reader = new ExcelResourceReader();
        var list = (List<IndustryResource>) reader.read(ClassUtils.getFileFromClassPath("excel/IndustryResource.xlsx"), IndustryResource.class);

        var map = new FileChannelMap<>("industryDb", SpiderIndustry.class);
        if (map.get(KEY) == null) {
            map.put(KEY, SpiderIndustry.valueOf(30_0000, 1));
        }
        var spiderPacket = map.get(KEY);

        var industryStocks = new ArrayList<IndustryStock>();

        for (int i = spiderPacket.getIndex(); i < 100_0000; i++) {
            var industryResource = getIndustryResource(list, i);
            if (industryResource == null) {
                map.put(KEY, SpiderIndustry.valueOf(i, 1));
                continue;
            }

            spiderPacket = map.get(KEY);
            var stockUrlTemplate = String.valueOf(industryResource.getCode()).startsWith("3")
                    ? "http://q.10jqka.com.cn/gn/detail/field/264648/order/desc/page/{}/ajax/1/code/{}"
                    : "http://q.10jqka.com.cn/thshy/detail/field/199112/order/desc/page/{}/ajax/1/code/{}";

            var count = spiderPacket.getCount();
            for (int j = count; j <= 227; j++) {
                var client = HttpClient.newBuilder().build();
                var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
                var request = HttpRequest.newBuilder(URI.create(StringUtils.format(stockUrlTemplate, j, industryResource.getCode())))
                        .headers(ArrayUtils.listToArray(StockService.HEADERS, String.class))
                        .GET()
                        .build();

                var str = client.send(request, responseBodyHandler).body();

                if (str.contains("Ngnix")) {
                    System.out.println(StringUtils.format("---------------------------{}", i));
                    map.put(KEY, SpiderIndustry.valueOf(i, j));
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
                    map.put(KEY, SpiderIndustry.valueOf(i + 1, 1));
                    map.close();
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

    public IndustryResource getIndustryResource(List<IndustryResource> list, int id) {
        var optional = list.stream().filter(it -> it.getCode() == id).findFirst();
        return optional.orElse(null);
    }
}
