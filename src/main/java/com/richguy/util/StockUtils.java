package com.richguy.util;

import com.richguy.model.common.StockPriceAndRise;
import com.richguy.model.five.FiveRangeResult;
import com.richguy.model.netease.StockNetEase;
import com.richguy.model.wencai.WenCaiRequest;
import com.richguy.model.yidong.StockHistory;
import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
public abstract class StockUtils {

    private static final Logger logger = LoggerFactory.getLogger(StockUtils.class);

    public static String formatCode(int code) {
        var stockCode = String.valueOf(code);
        switch (stockCode.length()) {
            case 0:
                stockCode = "000000";
                break;
            case 1:
                stockCode = StringUtils.format("00000{}", stockCode);
                break;
            case 2:
                stockCode = StringUtils.format("0000{}", stockCode);
                break;
            case 3:
                stockCode = StringUtils.format("000{}", stockCode);
                break;
            case 4:
                stockCode = StringUtils.format("00{}", stockCode);
                break;
            case 5:
                stockCode = StringUtils.format("0{}", stockCode);
                break;
            case 6:
                break;
            default:
        }
        return stockCode;
    }

    public static String hsCode(int code) {
        var stockCode = formatCode(code);
        stockCode = stockCode.startsWith("6")
                ? StringUtils.format("sh{}", stockCode)
                : StringUtils.format("sz{}", stockCode);
        return stockCode;
    }


    /**
     * 是否在交易时间
     */
    public static boolean tradingTime() {
        try {
            var now = TimeUtils.now();
            var simpleDateStr = TimeUtils.dateFormatForDayString(now);
            var startTime = TimeUtils.stringToDate(StringUtils.format("{} 09:10:00", simpleDateStr)).getTime();
            var endTime = TimeUtils.stringToDate(StringUtils.format("{} 11:30:00", simpleDateStr)).getTime();

            if (TimeUtils.timeBetween(now, startTime, endTime)) {
                return true;
            }

            startTime = TimeUtils.stringToDate(StringUtils.format("{} 12:50:00", simpleDateStr)).getTime();
            endTime = TimeUtils.stringToDate(StringUtils.format("{} 15:00:00", simpleDateStr)).getTime();

            if (TimeUtils.timeBetween(now, startTime, endTime)) {
                return true;
            }

            return false;
        } catch (ParseException e) {
            logger.error("开盘时间解析错误");
        }

        return false;
    }

    public static String toSimpleRatio(float value) {
        var decimal = new BigDecimal(value);
        return decimal.setScale(1, RoundingMode.HALF_UP).toString();
    }


    // **************************************************股票相关********************************************************


    /**
     * 通过爬虫获取股票价格有概率失败，所以一共有3个实现，轮流使用不同的实现
     *
     * @param code 股票的代码
     */
    public static final float DEFAULT_VAlUE = 88.8F;

    public static StockPriceAndRise stockPriceAndRise(int code) {
        var stockPriceAndRise = StockPriceAndRise.valueOf(DEFAULT_VAlUE, DEFAULT_VAlUE);

        try {
            var stockCode = StockUtils.formatCode(code);
            stockCode = stockCode.startsWith("6")
                    ? StringUtils.format("0{}", stockCode)
                    : StringUtils.format("1{}", stockCode);
            var stock = stockOfNetEase(stockCode);
            stockPriceAndRise = StockPriceAndRise.valueOf(Float.parseFloat(stock.getPrice()), Float.parseFloat(stock.getPercent()) * 100);
        } catch (Exception e) {
            logger.info("网易接口api获取股票数据异常");
        }

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
            logger.error("获取股票数据异常，没有任何一个接口可以获取到股票数据");
        }

        return stockPriceAndRise;
    }

    public static StockNetEase stockOfNetEase(String stockCode) {
        try {
            var url = StringUtils.format("https://api.money.126.net/data/feed/{}", stockCode);
            var responseBody = HttpUtils.get(url);
            var json = HttpUtils.formatJson(responseBody);
            json = StringUtils.substringAfterFirst(json, ":");
            json = StringUtils.substringBeforeLast(json, "}");
            var stock = JsonUtils.string2Object(json, StockNetEase.class);
            return stock;
        } catch (Exception e) {
            throw new RunException(e);
        }
    }

    public static StockPriceAndRise doGetByThs(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", stockCode);
        var responseBody = HttpUtils.get(url);
        var json = HttpUtils.formatJson(responseBody);
        var fiveRange = JsonUtils.string2Object(json, FiveRangeResult.class);

        return StockPriceAndRise.valueOf(Float.parseFloat(fiveRange.getItems().getBuy1()), fiveRange.getItems().increaseRatioFloat());
    }


    public static StockPriceAndRise doGetByWenCai(int code) throws IOException, InterruptedException {
        var stockCode = StockUtils.formatCode(code);
        var request = WenCaiRequest.valueOf(stockCode, 50, 1, "Ths_iwencai_Xuangu", "stock", "2.0");
        var responseBody = HttpUtils.post("http://www.iwencai.com/customized/chart/get-robot-data", request);

        var priceNode = JsonUtils.getNode(responseBody, "latest_price");
        var price = priceNode.asDouble();

        var riseNode = JsonUtils.getNode(responseBody, "rise_fall_rate");
        var rise = riseNode.asDouble();
        return StockPriceAndRise.valueOf((float) price, (float) rise);
    }


    public static StockPriceAndRise doGetByXueQiu(int code) throws IOException {
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



    // **************************************************股票相关********************************************************

    public static final int PIAN_LI_SIZE = 7;

    // 股票偏离
    public static List<StockHistory> pianLi(String stockCode, boolean realTime) {
        stockCode = stockCode.startsWith("6")
                ? StringUtils.format("0{}", stockCode)
                : StringUtils.format("1{}", stockCode);

        var urlTemplate = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}";

        var startDate = TimeUtils.dateFormatForDayString(TimeUtils.now() - TimeUtils.MILLIS_PER_DAY * 28).replaceAll("-", "");
        var endDate = TimeUtils.dateFormatForDayString(TimeUtils.now()).replaceAll("-", "");

        var url = StringUtils.format(urlTemplate, stockCode, startDate, endDate);

        var bytes = HttpUtils.getBytes(url);
        var result = new String(bytes, Charset.forName("gb2312"));
        var rowSplits = result.split(FileUtils.LS);


        var list = new ArrayList<StockHistory>();
        for (int i = 1; i < rowSplits.length; i++) {
            var splits = rowSplits[i].split(StringUtils.COMMA_REGEX);
            var date = StringUtils.trim(splits[0]);
            var code = StringUtils.trim(splits[1]);
            var name = StringUtils.trim(splits[2]);
            var endPrice = new BigDecimal(StringUtils.trim(splits[3])).setScale(2, RoundingMode.HALF_UP);
            if (endPrice.floatValue() == 0) {
                continue;
            }
            var huanShou = StringUtils.isBlank(splits[10])
                    ? StringUtils.EMPTY
                    : new BigDecimal(StringUtils.trim(splits[10])).setScale(2, RoundingMode.HALF_UP).toString();
            var chengJiao = StringUtils.isBlank(splits[12])
                    ? StringUtils.EMPTY
                    : new BigDecimal(StringUtils.trim(splits[12])).divide(new BigDecimal(1_0000_0000), 1, RoundingMode.HALF_UP).toString();
            var stockHistory = StockHistory.valueOf(date, code, name, endPrice, huanShou, chengJiao);

            list.add(stockHistory);
        }

        // 将最新的数据添加到最前面
        if (realTime) {
            var stock = StockUtils.stockOfNetEase(stockCode);
            try {
                var stockDateStr = StringUtils.substringBeforeLast(stock.getTime(), " ").replaceAll("/", "-");
                var stockDate = TimeUtils.dayStringToDate(stockDateStr);
                var csvDate = TimeUtils.dayStringToDate(list.get(0).getDate());
                if (!TimeUtils.isSameDay(stockDate, csvDate)) {
                    var newList = new ArrayList<StockHistory>();
                    var date = stockDateStr;
                    var code = list.get(0).getCode();
                    var name = StringUtils.format("{}实时", list.get(0).getName());
                    var endPrice = new BigDecimal(StringUtils.trim(stock.getPrice())).setScale(2, RoundingMode.HALF_UP);
                    var huanShou = StringUtils.EMPTY;
                    var chengJiao = new BigDecimal(stock.getTurnover()).divide(new BigDecimal(1_0000_0000), 1, RoundingMode.HALF_UP).toString();
                    var stockHistory = StockHistory.valueOf(date, code, name, endPrice, huanShou, chengJiao);
                    newList.add(stockHistory);
                    newList.addAll(list);
                    list = newList;
                }
            } catch (ParseException e) {
                throw new RunException(e);
            }
        }

        for (int i = 0; i < PIAN_LI_SIZE; i++) {
            var stockHistory = list.get(i);
            var endPrice = stockHistory.getEndPrice();
            var twoStartPrice = list.get(i + 2).getEndPrice();
            var threeStartPrice = list.get(i + 3).getEndPrice();
            var twoDecimal = endPrice.subtract(twoStartPrice).multiply(new BigDecimal(100)).divide(twoStartPrice, 2, RoundingMode.HALF_UP);
            var threeDecimal = endPrice.subtract(threeStartPrice).multiply(new BigDecimal(100)).divide(threeStartPrice, 2, RoundingMode.HALF_UP);
            stockHistory.setTwoPianLi(twoDecimal);
            stockHistory.setThreePianLi(threeDecimal);
        }
        return list;
    }

}
