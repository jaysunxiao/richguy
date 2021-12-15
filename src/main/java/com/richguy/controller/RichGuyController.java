package com.richguy.controller;


import com.richguy.model.Telegraph;
import com.richguy.model.five.FiveRange;
import com.richguy.model.five.FiveRangeResult;
import com.richguy.model.quote.Quote;
import com.richguy.resource.IndustryResource;
import com.richguy.resource.KeyWordResource;
import com.richguy.resource.StockResource;
import com.richguy.service.RichGuyService;
import com.richguy.service.StockService;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RichGuyController {

    private static final Logger logger = LoggerFactory.getLogger(RichGuyController.class);

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @Autowired
    private RichGuyService richGuyService;
    @Autowired
    private StockService stockService;

    @ResInjection
    private Storage<String, KeyWordResource> keyWordResources;


    private Deque<Long> pushIds = new LinkedList<>();

    @Scheduler(cron = "0 * * * * ?")
    public void cronPushQQ() throws IOException, InterruptedException {
        var response = requestForTelegraph();

        if (response.getData() == null) {
            return;
        }

        var rollData = response.getData().getRollData();
        if (CollectionUtils.isEmpty(rollData)) {
            return;
        }

        doPush(response);
    }

    public void doPush(Telegraph telegraph) {
        var rollData = telegraph.getData().getRollData();

        for (var news : rollData) {
            if (news.getType() != -1) {
                continue;
            }
            if (pushIds.contains(news.getId())) {
                continue;
            }


            var level = StringUtils.trim(news.getLevel());
            var title = StringUtils.trim(news.getTitle());
            var content = StringUtils.trim(news.getContent());
            var dateStr = TimeUtils.dateFormatForDayTimeString(news.getCtime() * TimeUtils.MILLIS_PER_SECOND);

            var builder = new StringBuilder();
            if (level.equals("A")) {
                builder.append(StringUtils.format("⭐A级Max {}", dateStr));
            } else if (level.equals("B")) {
                builder.append(StringUtils.format("B级电报 {}", dateStr));
            } else if (keyWordResources.getAll().stream().map(it -> it.getWord()).anyMatch(it -> content.contains(it))) {
                builder.append(StringUtils.format("{}级电报 {}", level, dateStr));
            } else {
                continue;
            }

            if (pushIds.size() >= 1000) {
                pushIds.removeFirst();
            }

            if (StringUtils.isNotEmpty(title)) {
                builder.append(FileUtils.LS);
                builder.append(StringUtils.format("\uD83D\uDCA5【{}】", title));
            }

            builder.append(FileUtils.LS);
            builder.append(FileUtils.LS);

            var simpleContent = StringUtils.trim(StringUtils.substringAfterFirst(content, "】"));
            if (StringUtils.isNotEmpty(simpleContent)) {
                builder.append(simpleContent);
            } else {
                builder.append(content);
            }

            // 添加相关股票--------------------------------------------------------------------------------------------
            var stockStr = StringUtils.EMPTY;
            if (CollectionUtils.isNotEmpty(news.getStocks())) {
                var stockNameList = news.getStocks().stream().map(it -> it.getName()).collect(Collectors.toList());
                stockStr = StringUtils.joinWith(StringUtils.COMMA, stockNameList.toArray());
            }

            var otherBuilder = new StringBuilder();
            var stockList = stockService.selectStocks(StringUtils.format("{} {}", builder, stockStr));
            if (CollectionUtils.isNotEmpty(stockList)) {
                var stockMap = new HashMap<StockResource, FiveRange>();
                for (var stock : stockList) {
                    var fiveRange = stockFiveRange(stock.getCode());
                    if (fiveRange == null) {
                        continue;
                    }
                    stockMap.put(stock, fiveRange);
                }

                if (CollectionUtils.isNotEmpty(stockMap)) {
                    otherBuilder.append(FileUtils.LS);
                    otherBuilder.append("\uD83D\uDCA7股票：");
                    stockMap.entrySet().stream()
                            .sorted((a, b) -> Float.compare(b.getValue().increaseRatioFloat(), a.getValue().increaseRatioFloat()))
                            .forEach(it -> {
                                var industry = it.getKey();
                                var fiveRange = it.getValue();
                                var industryName = industry.getName();
                                otherBuilder.append(StringUtils.format("{}({})  ", industryName, fiveRange.increaseRatio()));
                            });
                }
            }

            // 添加板块
            var industryList = stockService.selectIndustry(builder.toString(), stockList);
            if (CollectionUtils.isNotEmpty(industryList)) {
                var bkMap = new HashMap<IndustryResource, Quote>();
                for (var industry : industryList) {
                    var quote = bkQuote(industry.getRealCode());
                    if (quote == null) {
                        continue;
                    }
                    bkMap.put(industry, quote);
                }

                if (CollectionUtils.isNotEmpty(bkMap)) {
                    otherBuilder.append(FileUtils.LS);
                    otherBuilder.append("\uD83C\uDF20板块：");
                    bkMap.entrySet().stream()
                            .sorted((a, b) -> Float.compare(b.getValue().increaseRatioFloat(), a.getValue().increaseRatioFloat()))
                            .limit(13)
                            .forEach(it -> {
                                var industry = it.getKey();
                                var quote = it.getValue();
                                var industryName = industry.getName();
                                otherBuilder.append(StringUtils.format("{}({})  ", industryName, quote.increaseRatio()));
                            });
                }
            }


            // 添加关键词
            var keyWords = new HashSet<String>();
            if (CollectionUtils.isNotEmpty(news.getSubjects())) {
                for (var subject : news.getSubjects()) {
                    keyWords.add(StringUtils.trim(subject.getSubjectName()));
                }
            }
            for (var keyWordResource : keyWordResources.getAll()) {
                if (content.contains(keyWordResource.getWord())) {
                    keyWords.add(keyWordResource.getWord());
                }
            }

            if (CollectionUtils.isNotEmpty(keyWords)) {
                otherBuilder.append(FileUtils.LS);
                otherBuilder.append("\uD83D\uDD25热词：");
                for (var word : keyWords) {
                    otherBuilder.append(word).append("  ");
                }
            }

            if (otherBuilder.length() > 0) {
                builder.append(FileUtils.LS);
                builder.append(otherBuilder);
            }

            var bot = richGuyService.bot;
            var telegraphContent = builder.toString().replaceAll("习近平", "喜大大");

            for (var pushGroupId : pushGroupIds) {
                var group = bot.getGroup(pushGroupId);
                group.sendMessage(telegraphContent);
            }

            pushIds.add(news.getId());
            logger.info(telegraphContent);
        }
    }


    /**
     * 获取最新电报
     */
    public Telegraph requestForTelegraph() throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create("https://www.cls.cn/nodeapi/updateTelegraphList"))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var response = JsonUtils.string2Object(responseBody, Telegraph.class);

        return response;
    }

    public FiveRange stockFiveRange(int code) {
        FiveRange fiveRange = null;
        for (int i = 0; i < 3; i++) {
            try {
                fiveRange = doGetStockFiveRange(code);
                break;
            } catch (Exception e) {
                ThreadUtils.sleep(3 * TimeUtils.MILLIS_PER_SECOND);
                continue;
            }
        }
        return fiveRange;
    }

    public FiveRange doGetStockFiveRange(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var client = HttpClient.newBuilder().build();

        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", stockCode);

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var json = HttpUtils.formatJson(responseBody);
        var fiveRange = JsonUtils.string2Object(json, FiveRangeResult.class);
        return fiveRange.getItems();
    }

    public Quote bkQuote(int code) {
        Quote quote = null;
        for (int i = 0; i < 3; i++) {
            try {
                quote = doGetBkQuote(code);
                break;
            } catch (Exception e) {
                ThreadUtils.sleep(3 * TimeUtils.MILLIS_PER_SECOND);
                continue;
            }
        }
        return quote;
    }

    public Quote doGetBkQuote(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
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
        return quote;
    }

}
