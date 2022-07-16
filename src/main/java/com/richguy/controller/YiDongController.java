package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.model.yidong.StockHistory;
import com.richguy.service.RichGuyService;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zfoo.protocol.util.StringUtils.TAB_ASCII;

/**
 * 异动
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class YiDongController {

    private static final int PIAN_LI_SIZE = 7;

    @Autowired
    private RichGuyService richGuyService;

    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        var message = event.getMessage();
        if (!message.startsWith("yd")) {
            return;
        }

        try {
            doYiDong(message);
        } catch (Exception e) {
            richGuyService.pushGroupMessageNow(e.getMessage());
        }
    }

    public void doYiDong(String message) throws ParseException {
        var splits = message.split(StringUtils.SPACE);

        if (ArrayUtils.length(splits) != 2) {
            richGuyService.pushGroupMessageNow(StringUtils.format("\uD83C\uDE32请输入异动的正确的语法格式：{}------------------yd stock_code ------------------", FileUtils.LS));
            return;
        }

        var stockCode = StockUtils.formatCode(Integer.parseInt(splits[1]));
        var yiDong = yiDong(stockCode);

        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                richGuyService.pushGroupMessageNow(yiDong);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    // 总异动
    public String yiDong(String stockCode) {
        var daPaList = stockCode.startsWith("6") ? pianLi("000001") : pianLi("399106");
        ThreadUtils.sleep(3000);
        var stockList = pianLi(stockCode);

        var daPa = daPaList.get(0);
        var stock = stockList.get(0);

        var builder = new StringBuilder();
        builder.append(StringUtils.format("日期{}{}({}){}两日偏离{}三日偏离", TAB_ASCII, daPa.getName(), daPa.getCode(), TAB_ASCII, TAB_ASCII)).append(FileUtils.LS);
        for (int i = 0; i < PIAN_LI_SIZE; i++) {
            var daPaHistory = daPaList.get(i);
            builder.append(StringUtils.format("{}{}{}{}{}%{}{}%"
                    , daPaHistory.getDate(), TAB_ASCII, daPaHistory.getEndPrice().toString()
                    , TAB_ASCII, daPaHistory.getTwoPianLi().toString(), TAB_ASCII, daPaHistory.getThreePianLi().toString()));
            builder.append(FileUtils.LS);
        }
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("日期{}{}价格{}换手{}成交金额{}两日偏离{}三日偏离"
                , TAB_ASCII, stock.getName(), TAB_ASCII, TAB_ASCII, TAB_ASCII, TAB_ASCII, TAB_ASCII));
        builder.append(FileUtils.LS);

        for (int i = 0; i < PIAN_LI_SIZE; i++) {
            var stockHistory = stockList.get(i);
            var daPaHistory = daPaList.get(i);

            var twoPianLi = stockHistory.getTwoPianLi().subtract(daPaHistory.getTwoPianLi()).toString();
            var threePianLi = stockHistory.getThreePianLi().subtract(daPaHistory.getThreePianLi()).toString();
            builder.append(StringUtils.format("{}{}{}¥{}{}%{}{}亿{}{}%{}{}%"
                    , stockHistory.getDate(), TAB_ASCII, stockHistory.getEndPrice().toString(), TAB_ASCII, stockHistory.getHuanShou()
                    , TAB_ASCII, stockHistory.getChengJiao(), TAB_ASCII, twoPianLi, TAB_ASCII, threePianLi));
            builder.append(FileUtils.LS);
        }
        builder.append(FileUtils.LS);

        return builder.toString();
    }

    // 股票偏离
    public List<StockHistory> pianLi(String stockCode) {
        stockCode = stockCode.startsWith("6")
                ? StringUtils.format("0{}", stockCode)
                : StringUtils.format("1{}", stockCode);

        var urlTemplate = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}";

        var startDate = TimeUtils.dateFormatForDayString(TimeUtils.now() - TimeUtils.MILLIS_PER_DAY * 28).replaceAll("-", "");
        var endDate = TimeUtils.dateFormatForDayString(TimeUtils.now()).replaceAll("-", "");

        var url = StringUtils.format(urlTemplate, stockCode, startDate, endDate);

        var bytes = HttpUtils.getBytes(url);
        var result = new String(bytes, Charset.forName("gb2312"));
        System.out.println(result);
        var rowSplits = result.split(FileUtils.LS);

        var list = new ArrayList<StockHistory>();
        for (int i = 1; i < rowSplits.length; i++) {
            var splits = rowSplits[i].split(StringUtils.COMMA_REGEX);

            var date = StringUtils.substringAfterFirst(StringUtils.trim(splits[0]), "-").replaceAll("-", "/");
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
