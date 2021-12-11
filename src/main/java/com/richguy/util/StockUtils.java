package com.richguy.util;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public abstract class StockUtils {

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

}
