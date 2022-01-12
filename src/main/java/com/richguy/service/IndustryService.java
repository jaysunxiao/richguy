package com.richguy.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.richguy.model.OneNews;
import com.richguy.model.quote.Quote;
import com.richguy.resource.IndustryResource;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class IndustryService {

    public static final Logger logger = LoggerFactory.getLogger(IndustryService.class);

    public static final float DEFAULT_VAlUE = 88.8F;

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Autowired
    private StockService stockService;
    @Autowired
    private TopWordService topWordService;


    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;

    private Deque<Long> topIds = new LinkedList<>();

    private Map<Integer, Integer> topIndustryMap = new HashMap<>();

    public void topIndustry(OneNews news) {
        if (topIds.contains(news.getId())) {
            return;
        }

        if (topIds.size() >= 1000) {
            topIds.removeFirst();
        }

        var stockList = stockService.selectStocks(news);
        var industryList = stockService.selectIndustry(news, stockList);

        for (var industry : industryList) {
            var code = industry.getCode();
            var count = topIndustryMap.computeIfAbsent(code, key -> 0);
            topIndustryMap.put(code, count + 1);
        }

        topIds.add(news.getId());

        // 统计电报词语
        topWordService.topWord(stockService.toFullContent(news));
    }

    public String topIndustryToday() {
        var builder = new StringBuilder();

        builder.append("\uD83C\uDF20电报热点板块次数统计：");

        var topList = topIndustryMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(30)
                .collect(Collectors.toList());

        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        var count = 1;
        for (var pair : topList) {
            var code = pair.getKey();
            var industryResource = industryResources.get(code);
            var quote = bkQuote(code);
            builder.append(StringUtils.format("{}. {}({}) {}", count, industryResource.getName(), StockUtils.toSimpleRatio(quote), pair.getValue()));
            builder.append(FileUtils.LS);
            count++;

            if (count == 10 || count == 20 || count == 30) {
                builder.append(FileUtils.LS);
            }
        }

        topIndustryMap.clear();
        return builder.toString();
    }

    // **************************************************板块相关********************************************************
    public float bkQuote(int code) {
        float quote = DEFAULT_VAlUE;

        try {
            quote = doGetBkQuote(code);
        } catch (Exception e) {
            logger.error("通过同花顺接口api获取板块数据异常", e);
        }

        if (quote == DEFAULT_VAlUE) {
            try {
                quote = doGetBkQuoteByHtml(code);
            } catch (Exception e) {
                logger.error("通过同花顺网站的html获取板块数据异常", e);
            }
        }

        return quote;
    }


    public float doGetBkQuote(int code) throws IOException, InterruptedException {
        var realCode = industryResources.get(code).getRealCode();

        var stockCode = StockUtils.formatCode(realCode);
        var urlTemplate = "http://d.10jqka.com.cn/v4/time/bk_{}/last.js";
        var url = StringUtils.format(urlTemplate, stockCode);

        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var json = HttpUtils.formatJson(responseBody);
        json = StringUtils.substringAfterFirst(json, "\":");
        json = StringUtils.substringBeforeLast(json, "}");

        var quote = JsonUtils.string2Object(json, Quote.class);
        return quote.increaseRatioFloat();
    }

    public float doGetBkQuoteByHtml(int code) throws IOException {
        var stockCode = StockUtils.formatCode(code);

        var url = stockCode.startsWith("3")
                ? StringUtils.format("http://q.10jqka.com.cn/gn/detail/code/{}/", stockCode)
                : StringUtils.format("http://q.10jqka.com.cn/thshy/detail/code/{}/", stockCode);

        var webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.getOptions().setGeolocationEnabled(false);
        webClient.getOptions().setWebSocketEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        var page = webClient.getPage(url);
        var responseBody = page.getWebResponse().getContentAsString();
        var document = Jsoup.parse(responseBody);

        var docs = document.getElementsByAttributeValue("class", "board-zdf");
        var doc = docs.get(0);
        var docStr = doc.text();
        var value = StringUtils.substringAfterLast(docStr, " ");
        value = StringUtils.substringBeforeFirst(value, "%");
        value = value.trim();

        return Float.valueOf(value);
    }
}
