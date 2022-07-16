package com.richguy.controller;


import com.richguy.model.OneNews;
import com.richguy.model.Telegraph;
import com.richguy.model.common.StockPriceAndRise;
import com.richguy.model.five.FiveRangeResult;
import com.richguy.model.level.NewsLevelEnum;
import com.richguy.model.level.NewsPushEvent;
import com.richguy.model.level.TelegraphNewsEvent;
import com.richguy.model.stock.QuotesResult;
import com.richguy.model.wencai.WenCaiRequest;
import com.richguy.resource.IndustryResource;
import com.richguy.resource.KeyWordResource;
import com.richguy.resource.StockResource;
import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.QqBotService;
import com.richguy.service.StockService;
import com.richguy.util.DateUtils;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.event.AppStartAfterEvent;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsController implements ApplicationListener<AppStartAfterEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    public static final float DEFAULT_VAlUE = 88.8F;


    @Value("${juhe.stockUrl}")
    private String juheStockUrl;

    @Autowired
    private QqBotService qqBotService;
    @Autowired
    private StockService stockService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private DatabaseService databaseService;


    @ResInjection
    private Storage<String, KeyWordResource> keyWordResources;
    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    /**
     * 财联社新闻推送
     */
    @Scheduler(cron = "0 * * * * ?")
    public void cronPushQQ() throws IOException, InterruptedException {
        var response = requestForTelegraph();
        var telegraphNews = toNews(response);
        EventBus.syncSubmit(TelegraphNewsEvent.valueOf(telegraphNews));
        doPush(telegraphNews, 2.1F);
    }

    /**
     * 财联社新闻推送，会拉取更多的消息，寻找那些被忽略的消息
     */
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronPushQQ60() throws IOException, InterruptedException {
        var response = requestForTelegraph60();
        var telegraphNews = toNews(response);
        EventBus.syncSubmit(TelegraphNewsEvent.valueOf(telegraphNews));
        doPush(telegraphNews, 3.1F);
    }

    public void doPush(List<OneNews> telegraphNews, float ratio) {
        if (CollectionUtils.isEmpty(telegraphNews)) {
            return;
        }

        var database = databaseService.database;

        var avgReadingNum = telegraphNews.stream().mapToInt(it -> it.getReadingNum()).average().getAsDouble();
        var avgShareNum = telegraphNews.stream().mapToInt(it -> it.getShareNum()).average().getAsDouble();
        var avgReading = avgReadingNum * ratio;
        var avgShare = avgShareNum * ratio;

        for (var news : telegraphNews) {
            if (database.getPushTelegraphIds().contains(news.getId())) {
                continue;
            }

            var stockList = stockService.selectStocks(news);

            var level = StringUtils.trim(news.getLevel());
            var title = StringUtils.trim(news.getTitle());
            var content = StringUtils.trim(news.getContent());
            var dateStr = DateUtils.dateFormatForDayTimeString(news.getCtime() * TimeUtils.MILLIS_PER_SECOND);

            var builder = new StringBuilder();
            if (level.equals("A")) {
                builder.append(StringUtils.format("⭐S级Max  {}", dateStr));
                EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.S));
            } else if (level.equals("B")) {
                if (keyWordResources.getAll().stream().map(it -> it.getWord()).anyMatch(it -> content.contains(it))) {
                    builder.append(StringUtils.format("A级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.A));
                } else if (CollectionUtils.isNotEmpty(stockList)) {
                    builder.append(StringUtils.format("C级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.C));
                } else {
                    builder.append(StringUtils.format("B级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.B));
                }
            }

//            else if (keyWordResources.getAll().stream().map(it -> it.getWord()).anyMatch(it -> content.contains(it))) {
//                if (CollectionUtils.isEmpty(stockList)) {
//                    builder.append(StringUtils.format("B级电报  {}", dateStr));
//                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.B));
//                } else {
//                    builder.append(StringUtils.format("C级电报  {}", dateStr));
//                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.C));
//                }
//            }

//            else if (news.getReadingNum() >= avgReading || news.getShareNum() >= avgShare) {
//                var ctime = news.getCtime() * TimeUtils.MILLIS_PER_SECOND;
//                var diff = TimeUtils.now() - ctime;
//                if (CollectionUtils.isNotEmpty(stockList)) {
//                    builder.append(StringUtils.format("D级电报  {}", dateStr));
//                    builder.append(FileUtils.LS);
//                    builder.append(StringUtils.format("阅读[{}W]  分享[{}] old1 {}", StockUtils.toSimpleRatio(news.getReadingNum() / 10000.0F), news.getShareNum(), ratio));
//                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.C));
//                } else if (diff < 60 * TimeUtils.MILLIS_PER_MINUTE) {
//                    builder.append(StringUtils.format("C级电报  {}", dateStr));
//                    builder.append(FileUtils.LS);
//                    builder.append(StringUtils.format("阅读[{}W]  分享[{}] old2 {}", StockUtils.toSimpleRatio(news.getReadingNum() / 10000.0F), news.getShareNum(), ratio));
//                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.C));
//                } else {
//                    builder.append(StringUtils.format("D级电报  {}", dateStr));
//                    builder.append(FileUtils.LS);
//                    builder.append(StringUtils.format("阅读[{}W]  分享[{}] old3 {}", StockUtils.toSimpleRatio(news.getReadingNum() / 10000.0F), news.getShareNum(), ratio));
//                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.D));
//                }
//            }
            else {
                continue;
            }

            if (StringUtils.isNotEmpty(title)) {
                builder.append(FileUtils.LS);
                builder.append(StringUtils.format("\uD83D\uDCA5【{}】", title));
            }

            builder.append(FileUtils.LS);
            builder.append(FileUtils.LS);

            var simpleContent = StringUtils.trim(StringUtils.substringAfterFirst(content, "】"));
            if (StringUtils.isNotEmpty(simpleContent)) {
                builder.append(toSimpleContent(simpleContent));
            } else {
                builder.append(toSimpleContent(content));
            }

            // 添加相关股票--------------------------------------------------------------------------------------------

            var otherBuilder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(stockList)) {
                var stockMap = new HashMap<StockResource, StockPriceAndRise>();
                for (var stock : stockList) {
                    var fiveRange = stockPriceAndRise(stock.getCode());
                    stockMap.put(stock, fiveRange);
                }

                if (CollectionUtils.isNotEmpty(stockMap)) {
                    otherBuilder.append(FileUtils.LS);
                    otherBuilder.append("\uD83D\uDCA7票票：");
                    stockMap.entrySet().stream()
                            .sorted((a, b) -> Float.compare(b.getValue().getRise(), a.getValue().getRise()))
                            .forEach(it -> {
                                var industry = it.getKey();
                                var stockPriceAndRise = it.getValue();
                                var industryName = industry.getName();

                                var price = StockUtils.toSimpleRatio(stockPriceAndRise.getPrice());
                                var rise = StockUtils.toSimpleRatio(stockPriceAndRise.getRise());
                                otherBuilder.append(StringUtils.format("{}#{}({})  ", industryName, price, rise));
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

            var telegraphContent = builder.toString().replaceAll("习近平", "喜大大");

            qqBotService.pushGroupMessage(telegraphContent);

            // 将已经加入处理过的电报，存入到数据库中
            database.addPushTelegraphId(news.getId());

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

    public Telegraph requestForTelegraph60() throws IOException, InterruptedException {
        var url = "https://www.cls.cn/nodeapi/updateTelegraphList?rn=60";
        var responseBody = HttpUtils.get(url);
        var response = JsonUtils.string2Object(responseBody, Telegraph.class);
        return response;
    }

    private List<OneNews> toNews(Telegraph telegraph) {
        if (telegraph.getData() == null) {
            return Collections.emptyList();
        }

        var rollData = telegraph.getData().getRollData();
        if (CollectionUtils.isEmpty(rollData)) {
            return Collections.emptyList();
        }

        var telegraphNews = rollData.stream().filter(it -> it.getType() == -1).collect(Collectors.toList());
        return telegraphNews;
    }

    // **************************************************股票相关********************************************************


    /**
     * 通过爬虫获取股票价格有概率失败，所以一共有3个实现，轮流使用不同的实现
     *
     * @param code 股票的代码
     */
    public StockPriceAndRise stockPriceAndRise(int code) {
        var stockPriceAndRise = StockPriceAndRise.valueOf(DEFAULT_VAlUE, DEFAULT_VAlUE);

        try {
            stockPriceAndRise = doGetByThs(code);
        } catch (Exception e) {
            logger.info("同花顺接口api获取股票数据异常");
        }

        if (stockPriceAndRise.getRise() == DEFAULT_VAlUE) {
            try {
                stockPriceAndRise = doGetByXueQiu(code);
            } catch (Exception e) {
                logger.error("雪球接口api获取股票数据异常");
            }
        }

        if (stockPriceAndRise.getRise() == DEFAULT_VAlUE) {
            try {
                stockPriceAndRise = doGetByWenCai(code);
            } catch (Exception e) {
                logger.error("问财接口api获取股票数据异常");
            }
        }

        if (stockPriceAndRise.getRise() == DEFAULT_VAlUE) {
            try {
                stockPriceAndRise = doGetByJuhe(code);
            } catch (Exception e) {
                logger.error("聚合接口api获取股票数据异常");
            }
        }

        if (stockPriceAndRise.getRise() == DEFAULT_VAlUE) {
            logger.error("获取股票数据异常，没有任何一个接口可以获取到股票数据");
        }

        return stockPriceAndRise;
    }


    /**
     * 获取实时价格的第一种方式，调用失败，自动使用第二种doGetStockFiveRangeByJuhe
     */
    public StockPriceAndRise doGetByThs(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", stockCode);
        var responseBody = HttpUtils.get(url);
        var json = HttpUtils.formatJson(responseBody);
        var fiveRange = JsonUtils.string2Object(json, FiveRangeResult.class);

        return StockPriceAndRise.valueOf(Float.parseFloat(fiveRange.getItems().getBuy1()), fiveRange.getItems().increaseRatioFloat());
    }

    /**
     * 获取实时价格的第二种方式，调用失败，自动使用第三种getStockFiveRangeByWenCai
     */
    public StockPriceAndRise doGetByJuhe(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.hsCode(code);

        var url = StringUtils.format(juheStockUrl, stockCode);
        var responseBody = HttpUtils.get(url);
        var quote = JsonUtils.string2Object(responseBody, QuotesResult.class);

        var stock = quote.getResult().get(0);
        return StockPriceAndRise.valueOf(Float.parseFloat(stock.getStockData().getNowPri()), Float.parseFloat(stock.getBaseData().getRate()));
    }

    /**
     * 获取实时价格的第三种方式
     */
    public StockPriceAndRise doGetByWenCai(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var request = WenCaiRequest.valueOf(stockCode, 50, 1, "Ths_iwencai_Xuangu", "stock", "2.0");
        var responseBody = HttpUtils.post("http://www.iwencai.com/customized/chart/get-robot-data", request);

        var priceNode = JsonUtils.getNode(responseBody, "latest_price");
        var price = priceNode.asDouble();

        var riseNode = JsonUtils.getNode(responseBody, "rise_fall_rate");
        var rise = riseNode.asDouble();
        return StockPriceAndRise.valueOf((float) price, (float) rise);
    }


    /**
     * 获取实时价格的第四种方式
     */
    public StockPriceAndRise doGetByXueQiu(int code) throws IOException {
        var stockCode = StockUtils.hsCode(code).toUpperCase();

        var html = HttpUtils.html(StringUtils.format("https://xueqiu.com/S/{}", stockCode));

        var document = Jsoup.parse(html);

        var priceDocs = document.getElementsByAttributeValue("class", "stock-current");
        var priceNode = priceDocs.get(0);
        var price = StringUtils.substringAfterFirst(priceNode.text(), "¥").trim();

        var riseDocs = document.getElementsByAttributeValue("class", "stock-change");
        var riseNode = riseDocs.get(0);
        var rise = StringUtils.substringAfterFirst(riseNode.text(), " ").trim();
        if (rise.contains("+")) {
            rise = StringUtils.substringAfterFirst(rise, "+").trim();
        }
        rise = StringUtils.substringBeforeLast(rise, "%").trim();

        return StockPriceAndRise.valueOf(Float.parseFloat(price), Float.parseFloat(rise));
    }


    public String toSimpleContent(String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        var str = content;
        if (content.startsWith("财联社")) {
            str = StringUtils.substringAfterFirst(content, "，");
        }
        if (StringUtils.isEmpty(str)) {
            return content;
        }
        return str.trim();
    }

    @Override
    public void onApplicationEvent(AppStartAfterEvent event) {
        var stock = stockPriceAndRise(1);

        System.out.println(JsonUtils.object2StringPrettyPrinter(stock));
    }


}
