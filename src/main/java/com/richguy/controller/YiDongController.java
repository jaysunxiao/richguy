package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.service.RichGuyService;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * 异动
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class YiDongController {

    @Autowired
    private RichGuyService richGuyService;

    private long lastPushTime = TimeUtils.now();

    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        if (TimeUtils.now() - lastPushTime < TimeUtils.MILLIS_PER_SECOND * 5) {
            return;
        }

        var message = event.getMessage();
        if (StringUtils.isBlank(message)) {
            return;
        }
        message = StringUtils.trim(message).toLowerCase();
        if (!message.startsWith("yd")) {
            return;
        }

        try {
            doYiDong(message);
            lastPushTime = TimeUtils.now();
        } catch (ParseException e) {
            richGuyService.pushGroupMessage(errorMessage());
        }
    }

    public void doYiDong(String message) throws ParseException {
        var splits = message.split(StringUtils.SPACE);

        if (ArrayUtils.length(splits) != 2) {
            richGuyService.pushGroupMessage(errorMessage());
            return;
        }

        var stockCode = StockUtils.formatCode(Integer.parseInt(splits[1]));


        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                richGuyService.pushGroupMessageNow(StringUtils.format("{}%", yiDong(stockCode)));
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    // 总异动
    public String yiDong(String stockCode) {
        var stockPianLi = pianLi(stockCode);
        var daPaPianLi = stockCode.startsWith("6") ? pianLi("000001") : pianLi("399106");
        return stockPianLi.subtract(daPaPianLi).toString();
    }

    // 股票偏离
    public BigDecimal pianLi(String stockCode) {
        stockCode = stockCode.startsWith("6")
                ? StringUtils.format("0{}", stockCode)
                : StringUtils.format("1{}", stockCode);

        var urlTemplate = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}";

        var startDate = TimeUtils.dateFormatForDayString(TimeUtils.now() - TimeUtils.MILLIS_PER_DAY * 3).replaceAll("-", "");
        var endDate = TimeUtils.dateFormatForDayString(TimeUtils.now()).replaceAll("-", "");

        var url = StringUtils.format(urlTemplate, stockCode, startDate, endDate);

        var bytes = HttpUtils.getBytes(url);
        var result = new String(bytes, Charset.forName("gb2312"));
        var splits = result.split(FileUtils.LS);
        var endPrice = Float.parseFloat(StringUtils.trim(splits[1].split(StringUtils.COMMA_REGEX)[3]));
        var startPrice = Float.parseFloat(StringUtils.trim(splits[4].split(StringUtils.COMMA_REGEX)[3]));
        var decimal = new BigDecimal((endPrice - startPrice) / startPrice * 100);
        return decimal.setScale(2, RoundingMode.HALF_UP);
    }

    public String errorMessage() {
        var errorMessage = StringUtils.format("\uD83C\uDE32请输入异动的正确的语法格式：{}------------------yd stock_code ------------------", FileUtils.LS);
        return errorMessage;
    }

}
