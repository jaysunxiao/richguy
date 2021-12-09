package com.richguy.service;

import com.richguy.model.stock.Stock;
import com.richguy.packet.SpiderPacket;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.orm.lpmap.FileChannelMap;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.context.ApplicationListener;
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
public class StockService implements ApplicationListener<AppStartEvent> {

    public static final long KEY = 1;
    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "Referer", "http://q.10jqka.com.cn/",
            "hexin-v",
            "A-yTc4JuoQ75g7UyP31o4LAevcEbpdkrkv-klkYp-5U7YIL_brVg3-JZdTiV",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        try {
            var stocks = spiderStocks();
            System.out.println(stocks);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public List<Stock> spiderStocks() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        var stocks = new ArrayList<Stock>();

        var stockUrlTemplate = "http://q.10jqka.com.cn/index/index/board/all/field/zdf/order/desc/page/{}/ajax/1/";

        ProtocolManager.initProtocol(Set.of(SpiderPacket.class));
        var map = new FileChannelMap<>("db", SpiderPacket.class);
        if (map.get(KEY) == null) {
            map.put(KEY, SpiderPacket.valueOf(15));
        }
        var spiderPacket = map.get(KEY);
        var count = spiderPacket.getCount();

        for (int i = count; i <= 227; i++) {
            var client = HttpClient.newBuilder().build();
            var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
            var request = HttpRequest.newBuilder(URI.create(StringUtils.format(stockUrlTemplate, i)))
                    .headers(ArrayUtils.listToArray(StockService.HEADERS, String.class))
                    .GET()
                    .build();
            var str = client.send(request, responseBodyHandler).body();
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
                map.put(KEY, SpiderPacket.valueOf(i));
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
                var stock = Stock.valueOf(code, name, nowPrice, riseRatio, riseNum, increaseRatio, turnoverRatio, volumeRatio, vibration, turnover, floatingStock, marketValue, pe);
                stocks.add(stock);
                System.out.println(stock);
            }
        }

        return stocks;
    }

}
