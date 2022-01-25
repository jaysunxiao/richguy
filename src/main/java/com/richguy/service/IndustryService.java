package com.richguy.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.richguy.model.OneNews;
import com.richguy.model.quote.Quote;
import com.richguy.resource.IndustryResource;
import com.richguy.util.HttpUtils;
import com.richguy.util.IndustryUtils;
import com.richguy.util.StockUtils;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class IndustryService {

    public static final Logger logger = LoggerFactory.getLogger(IndustryService.class);

    public static final float DEFAULT_VAlUE = 88.8F;

    @Autowired
    private StockService stockService;
    @Autowired
    private TopWordService topWordService;
    @Autowired
    private DatabaseService databaseService;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    public void topIndustry(OneNews news) {
        var database = databaseService.database;

        if (database.getTopNewIds().contains(news.getId())) {
            return;
        }

        var stockList = stockService.selectStocks(news);
        var industryList = stockService.selectIndustry(news, stockList);

        for (var industry : industryList) {
            var code = industry.getCode();
            database.addTopIndustry(code);
        }

        database.addTopNewsId(news.getId());

        // 统计电报词语
        topWordService.topWord(stockService.toFullContent(news));
    }

    public String topIndustryToday() {
        var builder = new StringBuilder();

        builder.append("\uD83C\uDF20电报热点板块次数统计：");

        var database = databaseService.database;

        var topList = database.getTopIndustryMap().entrySet()
                .stream()
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
            var quoteSimpleRatio = StockUtils.toSimpleRatio(quote);
            var name = industryResource.getName();

            if (database.getOldTopIndustryMap().containsKey(code)) {
                var oldRank = topIndustryRank(code, database.getOldTopIndustryMap());
                var newRank = topIndustryRank(code, database.getTopIndustryMap());
                var changeRank = oldRank - newRank;
                if (changeRank >= 0) {
                    builder.append(StringUtils.format("{}. {}({}) ({}) +{}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                } else {
                    builder.append(StringUtils.format("{}. {}({}) ({}) -{}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                }
            } else {
                builder.append(StringUtils.format("{}. {}({}) ({})", count, name, quoteSimpleRatio, pair.getValue()));
            }
            builder.append(FileUtils.LS);

            count++;

            if (count == 10 || count == 20 || count == 30) {
                builder.append(FileUtils.LS);
            }
        }

        database.clearTopIndustryMap();
        return builder.toString();
    }

    public int topIndustryRank(int code, Map<Integer, Integer> topIndustryMap) {
        if (!topIndustryMap.containsKey(code)) {
            return 0;
        }

        var topList = topIndustryMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        var rank = 0;
        for (int i = 0; i < topList.size(); i++) {
            var topIndustry = topList.get(i);
            if (topIndustry.getKey() == code) {
                rank = i + 1;
                break;
            }
        }

        return rank;
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

        var responseBody = HttpUtils.get(url);
        var json = HttpUtils.formatJson(responseBody);
        json = StringUtils.substringAfterFirst(json, "\":");
        json = StringUtils.substringBeforeLast(json, "}");

        var quote = JsonUtils.string2Object(json, Quote.class);
        return quote.increaseRatioFloat();
    }

    public float doGetBkQuoteByHtml(int code) throws IOException {
        var url = IndustryUtils.industryHtmlUrl(code);

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
