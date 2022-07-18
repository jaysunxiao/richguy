package com.richguy.util;

import com.richguy.model.common.StockPriceAndRise;
import com.richguy.model.five.FiveRangeResult;
import com.richguy.model.netease.StockNetEase;
import com.richguy.model.wencai.WenCaiRequest;
import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

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

}
