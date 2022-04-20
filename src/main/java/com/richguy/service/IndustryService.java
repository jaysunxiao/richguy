package com.richguy.service;

import com.richguy.model.quote.Quote;
import com.richguy.resource.IndustryResource;
import com.richguy.util.HttpUtils;
import com.richguy.util.IndustryUtils;
import com.richguy.util.StockUtils;
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
    private DatabaseService databaseService;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    // **************************************************板块相关********************************************************
    public float bkQuote(int code) {
        float quote = DEFAULT_VAlUE;

        try {
            quote = doGetBkQuote(code);
        } catch (Exception e) {
            logger.error("通过同花顺接口api获取板块数据异常");
        }

        if (quote == DEFAULT_VAlUE) {
            try {
                quote = doGetBkQuoteByHtml(code);
            } catch (Exception e) {
                logger.error("通过同花顺网站的html获取板块数据异常");
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
        var responseBody = HttpUtils.html(url);
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
