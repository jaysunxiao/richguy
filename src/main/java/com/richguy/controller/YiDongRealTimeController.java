package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.service.QqBotService;
import com.richguy.util.StockUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import static com.zfoo.protocol.util.StringUtils.TAB_ASCII;

/**
 * 异动
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class YiDongRealTimeController {

    @Autowired
    private QqBotService qqBotService;

    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        var message = event.getMessage();
        if (!message.startsWith("yd")) {
            return;
        }

        try {
            yiDong(message);
        } catch (Exception e) {
            qqBotService.pushGroupMessageNow(e.getMessage());
        }
    }

    public void yiDong(String message) throws ParseException {
        var splits = message.split(StringUtils.SPACE);

        if (ArrayUtils.length(splits) != 2) {
            qqBotService.pushGroupMessageNow(StringUtils.format("\uD83C\uDE32请输入异动的正确的语法格式：{}------------------{}yd stock_code{}------------------"
                    , FileUtils.LS, FileUtils.LS, FileUtils.LS));
            return;
        }

        var stockCode = StockUtils.formatCode(Integer.parseInt(splits[1]));
        var yiDong = doYiDong(stockCode);

        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                qqBotService.pushGroupMessageNow(yiDong);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    // 总异动
    public String doYiDong(String stockCode) {
        var daPanList = stockCode.startsWith("6") ? StockUtils.pianLi("000001", true) : StockUtils.pianLi("399106", true);
        var stockList = StockUtils.pianLi(stockCode, true);

        var daPan = daPanList.get(0);
        var stock = stockList.get(0);

        var builder = new StringBuilder();
        builder.append(StringUtils.format("日期{}{}{}{}两日偏离{}三日偏离", TAB_ASCII, daPan.getName(), StringUtils.substringAfterFirst(daPan.getCode(), "'"), TAB_ASCII, TAB_ASCII)).append(FileUtils.LS);
        for (int i = 0; i < StockUtils.PIAN_LI_SIZE; i++) {
            var daPaHistory = daPanList.get(i);
            builder.append(StringUtils.format("{}{}{}{}{}%{}{}%"
                    , daPaHistory.getDate(), TAB_ASCII, daPaHistory.getEndPrice().toString()
                    , TAB_ASCII, daPaHistory.getTwoPianLi().toString(), TAB_ASCII, daPaHistory.getThreePianLi().toString()));
            builder.append(FileUtils.LS);
        }
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("日期{}{}{}换手{}成交{}两日偏离{}三日偏离"
                , TAB_ASCII, stock.getName(), TAB_ASCII, TAB_ASCII, TAB_ASCII, TAB_ASCII, TAB_ASCII));
        builder.append(FileUtils.LS);

        for (int i = 0; i < StockUtils.PIAN_LI_SIZE; i++) {
            var stockHistory = stockList.get(i);
            var daPaHistory = daPanList.get(i);

            var twoPianLi = stockHistory.getTwoPianLi().subtract(daPaHistory.getTwoPianLi()).toString();
            var threePianLi = stockHistory.getThreePianLi().subtract(daPaHistory.getThreePianLi()).toString();
            var simpleDate = StringUtils.substringAfterFirst(StringUtils.trim(stockHistory.getDate()), "-").replaceAll("-", "/");

            builder.append(StringUtils.format("{}{}{}{}{}%{}{}{}{}%{}{}%"
                    , simpleDate, TAB_ASCII, stockHistory.getEndPrice().toString(), TAB_ASCII, stockHistory.getHuanShou()
                    , TAB_ASCII, stockHistory.getChengJiao(), TAB_ASCII, twoPianLi, TAB_ASCII, threePianLi));
            builder.append(FileUtils.LS);
        }
        builder.append(FileUtils.LS);

        return builder.toString();
    }

}
