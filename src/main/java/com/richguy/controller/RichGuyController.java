package com.richguy.controller;


import com.richguy.model.Telegraph;
import com.richguy.model.five.FiveRangeResult;
import com.richguy.model.stock.QuotesResult;
import com.richguy.model.wencai.WenCaiRequest;
import com.richguy.resource.IndustryResource;
import com.richguy.resource.KeyWordResource;
import com.richguy.resource.StockResource;
import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.richguy.service.StockService;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class RichGuyController {

    private static final Logger logger = LoggerFactory.getLogger(RichGuyController.class);

    public static final float DEFAULT_VAlUE = 88.8F;

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @Value("${juhe.stockUrl}")
    private String juheStockUrl;

    @Autowired
    private RichGuyService richGuyService;
    @Autowired
    private StockService stockService;
    @Autowired
    private IndustryService industryService;


    @ResInjection
    private Storage<String, KeyWordResource> keyWordResources;
    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;

    private Deque<Long> pushIds = new LinkedList<>();

    /**
     * 财联社新闻推送
     */
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
            // 统计行业
            industryService.topIndustry(news);

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
            var stockList = stockService.selectStocks(news);

            var otherBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(stockList)) {
                var stockMap = new HashMap<StockResource, Float>();
                for (var stock : stockList) {
                    var fiveRange = stockFiveRange(stock.getCode());
                    stockMap.put(stock, fiveRange);
                }

                if (CollectionUtils.isNotEmpty(stockMap)) {
                    otherBuilder.append(FileUtils.LS);
                    otherBuilder.append("\uD83D\uDCA7股票：");
                    stockMap.entrySet().stream()
                            .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                            .forEach(it -> {
                                var industry = it.getKey();
                                var fiveRange = it.getValue();
                                var industryName = industry.getName();
                                otherBuilder.append(StringUtils.format("{}({})  ", industryName, StockUtils.toSimpleRatio(fiveRange)));
                            });
                }
            }

            // 添加板块
            var industryList = stockService.selectIndustry(news, stockList);
            if (CollectionUtils.isNotEmpty(industryList)) {
                var bkMap = new HashMap<IndustryResource, Float>();
                for (var industry : industryList) {
                    var quote = industryService.bkQuote(industry.getCode());
                    bkMap.put(industry, quote);
                }

                if (CollectionUtils.isNotEmpty(bkMap)) {
                    otherBuilder.append(FileUtils.LS);
                    otherBuilder.append("\uD83C\uDF20板块：");
                    bkMap.entrySet().stream()
                            .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                            .limit(13)
                            .forEach(it -> {
                                var industry = it.getKey();
                                var quote = it.getValue();
                                var industryName = industry.getName();
                                otherBuilder.append(StringUtils.format("{}({})  ", industryName, StockUtils.toSimpleRatio(quote)));
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
        var url = "https://www.cls.cn/nodeapi/updateTelegraphList";
        var responseBody = HttpUtils.get(url);
        var response = JsonUtils.string2Object(responseBody, Telegraph.class);
        return response;
    }

    // **************************************************股票相关********************************************************

    /**
     * 获取实时价格的第三种方式
     */
    public static float getStockFiveRangeByWenCai(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var request = WenCaiRequest.valueOf(stockCode, 50, 1, "Ths_iwencai_Xuangu", "stock", "2.0");
        var responseBody = HttpUtils.post("http://www.iwencai.com/customized/chart/get-robot-data", request);
        var node = JsonUtils.getNode(responseBody, "rise_fall_rate");
        var value = node.asDouble();
        return (float) value;
    }

    /**
     * 通过爬虫获取股票价格有概率失败，所以一共有3个实现，轮流使用不同的实现
     *
     * @param code 股票的代码
     */
    public float stockFiveRange(int code) {
        float fiveRange = DEFAULT_VAlUE;

        try {
            fiveRange = doGetStockFiveRange(code);
        } catch (Exception e) {
            logger.error("通过同花顺接口api获取股票数据异常", e);
        }

        if (fiveRange == DEFAULT_VAlUE) {
            try {
                fiveRange = doGetStockFiveRangeByJuhe(code);
            } catch (Exception e) {
                logger.error("通过聚合接口api获取股票数据异常", e);
            }
        }

        if (fiveRange == DEFAULT_VAlUE) {
            try {
                fiveRange = getStockFiveRangeByWenCai(code);
            } catch (Exception e) {
                logger.error("通过问财接口api获取股票数据异常", e);
            }
        }

        return fiveRange;
    }

    /**
     * 获取实时价格的第一种方式，调用失败，自动使用第二种doGetStockFiveRangeByJuhe
     */
    public float doGetStockFiveRange(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", stockCode);
        var responseBody = HttpUtils.get(url);
        var json = HttpUtils.formatJson(responseBody);
        var fiveRange = JsonUtils.string2Object(json, FiveRangeResult.class);
        return fiveRange.getItems().increaseRatioFloat();
    }

    /**
     * 获取实时价格的第二种方式，调用失败，自动使用第三种getStockFiveRangeByWenCai
     */
    public float doGetStockFiveRangeByJuhe(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        stockCode = stockCode.startsWith("6")
                ? StringUtils.format("sh{}", stockCode)
                : StringUtils.format("sz{}", stockCode);

        var url = StringUtils.format(juheStockUrl, stockCode);
        var responseBody = HttpUtils.get(url);
        var quote = JsonUtils.string2Object(responseBody, QuotesResult.class);
        return Float.valueOf(quote.getResult().get(0).getBaseData().getRate());
    }
}
