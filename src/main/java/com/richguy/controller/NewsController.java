package com.richguy.controller;


import com.richguy.model.OneNews;
import com.richguy.model.Telegraph;
import com.richguy.model.common.StockPriceAndRise;
import com.richguy.model.level.NewsLevelEnum;
import com.richguy.model.level.NewsPushEvent;
import com.richguy.model.level.TelegraphNewsEvent;
import com.richguy.model.result.BaseResponse;
import com.richguy.model.result.CodeEnum;
import com.richguy.resource.IndustryResource;
import com.richguy.resource.KeyWordResource;
import com.richguy.resource.PushWordResource;
import com.richguy.resource.StockResource;
import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.QqBotService;
import com.richguy.service.StockService;
import com.richguy.util.DateUtils;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.zfoo.protocol.util.FileUtils.LS;

@Controller
@CrossOrigin
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    public static String INDEX_HTML = StringUtils.EMPTY;
    public static String INDEX_TEMPLATE_HTML = StringUtils.EMPTY;

    static {
        try {
            INDEX_HTML = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("index.html")));
            INDEX_TEMPLATE_HTML = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("index-template.html")));
        } catch (IOException e) {
            throw new RunException(e);
        }
    }

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
    private Storage<String, PushWordResource> pushWordResources;
    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;

    @GetMapping(value = "/")
    @ResponseBody
    public String telegraphNews() {
        return INDEX_HTML;
    }

    @GetMapping(value = "/json")
    @ResponseBody
    public BaseResponse telegraphNewsJson() {
        return BaseResponse.valueOf(CodeEnum.OK, databaseService.database.getTelegraphs());
    }

    @GetMapping(value = "/range")
    @ResponseBody
    public String telegraphNewsJson(@RequestParam("s") String start, @RequestParam(value = "e", required = false) String end) throws ParseException {
        var startTime = TimeUtils.getZeroTimeOfDay(TimeUtils.dayStringToDate(start).getTime());
        var endTime = StringUtils.isBlank(end) ? Long.MAX_VALUE : TimeUtils.dayStringToDate(end).getTime();

        var telegraphs = databaseService.database.getTelegraphs()
                .stream()
                .filter(it -> it.getMiddle() <= startTime && startTime <= endTime)
                .collect(Collectors.toList());

        var bodyBuilder = new StringBuilder();
        var jsBuilder = new StringBuilder();
        var clipboardHtml = "<div id=\"{}\" data-clipboard-text=\"{}\">";
        var clipboardJs = "var btn{} = new ClipboardJS(document.getElementById('{}'));";
        for (var telegraph : telegraphs) {
            var id = telegraph.getLeft();
            var content = telegraph.getRight();
            bodyBuilder.append(StringUtils.format(clipboardHtml, id, content));
            bodyBuilder.append(content.replaceAll(LS, "<br>"));
            bodyBuilder.append("<br>");
            bodyBuilder.append("<hr>");
            bodyBuilder.append("</div>");

            jsBuilder.append(StringUtils.format(clipboardJs, id, id)).append(LS);
        }
        return StringUtils.format(INDEX_TEMPLATE_HTML, bodyBuilder.toString(), jsBuilder);
    }


    /**
     * 财联社新闻推送
     */
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronPushQQ1() throws IOException {
        var response = requestForTelegraph();
        var telegraphNews = toNews(response);
        EventBus.syncSubmit(TelegraphNewsEvent.valueOf(telegraphNews));
        doPush(telegraphNews, 2.1F);
    }

    @Scheduler(cron = "0 21 9 * * ?")
    public void cronPushQQ2() throws IOException {
        cronPushQQ1();
    }

    @Scheduler(cron = "0 23 9 * * ?")
    public void cronPushQQ3() throws IOException {
        cronPushQQ1();
    }

    @Scheduler(cron = "0 25 9 * * ?")
    public void cronPushQQ4() throws IOException {
        cronPushQQ1();
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
            if (database.getTelegraphs().stream().anyMatch(it -> it.getLeft() == news.getId())) {
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
                if (CollectionUtils.isEmpty(stockList)) {
                    builder.append(StringUtils.format("A级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.A));
                } else {
                    builder.append(StringUtils.format("B级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.B));
                }
            } else if (keyWordResources.getAll().stream().map(it -> it.getWord()).anyMatch(it -> content.contains(it))) {
                if (CollectionUtils.isEmpty(stockList)) {
                    builder.append(StringUtils.format("C级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.C));
                } else {
                    builder.append(StringUtils.format("D级电报  {}", dateStr));
                    EventBus.syncSubmit(NewsPushEvent.valueOf(NewsLevelEnum.D));
                }
            }

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
                builder.append(LS);
                builder.append(StringUtils.format("\uD83D\uDCA5【{}】", title));
            }

            builder.append(LS);
            builder.append(LS);

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
                    var fiveRange = StockUtils.stockPriceAndRise(stock.getCode());
                    stockMap.put(stock, fiveRange);
                }

                if (CollectionUtils.isNotEmpty(stockMap)) {
                    otherBuilder.append(LS);
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
                    otherBuilder.append(LS);
                    otherBuilder.append("\uD83D\uDCA7板块：");
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
                otherBuilder.append(LS);
                otherBuilder.append("\uD83D\uDCA7热词：");
                for (var word : keyWords) {
                    otherBuilder.append(word).append("  ");
                }
            }

            if (otherBuilder.length() > 0) {
                builder.append(LS);
                builder.append(otherBuilder);
            }

            var telegraphContent = builder.toString();

            // qq容易被封，不在qq推送了
            if (pushWordResources.getAll().stream().anyMatch(it -> telegraphContent.contains(it.getWord()))) {
//                qqBotService.pushGroupMessage(telegraphContent);
            }

            // 将已经加入处理过的电报，存入到数据库中
            database.addTelegraph(news.getId(), news.getCtime(), telegraphContent);

            logger.info(telegraphContent);
        }
    }


    /**
     * 获取最新电报
     */
    public Telegraph requestForTelegraph() throws IOException {
        var url = "https://www.cls.cn/nodeapi/updateTelegraphList";
        var responseBody = HttpUtils.puppeteer(url);
        var document = Jsoup.parse(responseBody);
        responseBody = document.getElementsByTag("pre").text();
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

}
