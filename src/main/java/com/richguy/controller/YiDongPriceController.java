package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.model.command.CommandEnum;
import com.richguy.service.QqBotService;
import com.richguy.util.StockUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static com.zfoo.protocol.util.StringUtils.TAB_ASCII;

/**
 * 异动
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class YiDongPriceController {

    @Autowired
    private QqBotService qqBotService;

    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        var message = event.getMessage();
        if (!message.startsWith(CommandEnum.jxyd.getCommand())) {
            return;
        }

        try {
            jiXianYiDong(message);
        } catch (Exception e) {
            qqBotService.pushGroupMessageNow(e.getMessage());
        }
    }

    public void jiXianYiDong(String message) {
        var splits = message.split(StringUtils.SPACE_REGEX);

        if (ArrayUtils.length(splits) != 3) {
            qqBotService.pushGroupMessageNow(StringUtils.format("\uD83C\uDE32请输入极限异动的正确的语法格式：{}------------------{}jxyd 2/3 stock_code{}------------------"
                    , FileUtils.LS, FileUtils.LS, FileUtils.LS));
            return;
        }

        var pianLiDay = Integer.parseInt(splits[1]);
        var stockCode = StockUtils.formatCode(Integer.parseInt(splits[2]));
        var jiXianYiDong = doJiXianYiDong(pianLiDay, stockCode);

        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                qqBotService.pushGroupMessageNow(jiXianYiDong);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    // 极限异动
    public String doJiXianYiDong(int pianLiDay, String stockCode) {
        var daPanList = stockCode.startsWith("6") ? StockUtils.pianLi("000001", false) : StockUtils.pianLi("399106", false);
        var stockList = StockUtils.pianLi(stockCode, false);

        var daPan = daPanList.get(0);
        var stock = stockList.get(0);

        var daPanStartPrice = pianLiDay == 2 ? daPanList.get(1).getEndPrice() : daPanList.get(2).getEndPrice();
        var stockStartPrice = pianLiDay == 2 ? stockList.get(1).getEndPrice() : stockList.get(2).getEndPrice();

        var builder = new StringBuilder();
        builder.append(StringUtils.format("{}{}{}价格{}涨幅", daPan.getName(), StringUtils.substringAfterFirst(daPan.getCode(), "'"), TAB_ASCII, TAB_ASCII)).append(FileUtils.LS);
        var startPercent = new BigDecimal("2");
        for (int i = 0; i < 41; i++) {
            var daPanUpPercent = startPercent.subtract(new BigDecimal("0.1").multiply(new BigDecimal(i)));
            var daPanPrice = daPan.getEndPrice().multiply(daPanUpPercent.add(new BigDecimal(100))).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            var daPanPianLi = daPanPrice.subtract(daPanStartPrice).multiply(new BigDecimal(100)).divide(daPanStartPrice, 2, RoundingMode.HALF_UP);

            var stockTargetPercent = new BigDecimal("20").add(daPanPianLi);
            var stockPrice = stockStartPrice.multiply(stockTargetPercent.add(new BigDecimal(100))).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            var stockUpPercent = stockPrice.subtract(stock.getEndPrice()).multiply(new BigDecimal(100)).divide(stock.getEndPrice(), 2, RoundingMode.HALF_UP);

            builder.append(StringUtils.format("{}{}{}{}{}%", daPanUpPercent.toString(), TAB_ASCII, stockPrice.toString(), TAB_ASCII, stockUpPercent.toString()));
            builder.append(FileUtils.LS);
        }
        builder.append(FileUtils.LS);
        return builder.toString();
    }

}
