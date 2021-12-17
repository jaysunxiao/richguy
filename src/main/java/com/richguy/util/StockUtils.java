package com.richguy.util;

import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

/**
 * @author jaysunxiao
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
            logger.error("");
        }

        return false;
    }

    public static String toSimpleRatio(float value) {
        var decimal = new BigDecimal(value);
        return decimal.setScale(1, RoundingMode.HALF_UP).toString();
    }

}
