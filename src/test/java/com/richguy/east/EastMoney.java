package com.richguy.east;

import com.richguy.controller.YiDongPriceController;
import com.richguy.controller.YiDongRealTimeController;
import com.richguy.util.HttpUtils;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class EastMoney {

    @Test
    public void apiTest() {
        var urlTemplate = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}";
        var url = StringUtils.format(urlTemplate, "1399106", "20220710", "20220718");

        var bytes = HttpUtils.getBytes(url);
        var str = new String(bytes, Charset.forName("gb2312"));
        System.out.println(str);
    }

    @Test
    public void yiDongRealTime() {
        var controller = new YiDongRealTimeController();
        var yiDong = controller.yiDong("000755");
        System.out.println(yiDong);
    }

    @Test
    public void yiDongPrice() {
        var controller = new YiDongPriceController();
        var yiDong = controller.yiDong(3, "000957");
        System.out.println(yiDong);
    }

    @Test
    public void netEaseTest() {
        var stock = StockUtils.stockOfNetEase("000755");
        System.out.println(JsonUtils.object2StringPrettyPrinter(stock));

        System.out.println(JsonUtils.object2StringPrettyPrinter(StockUtils.stockPriceAndRise(755)));
    }
}
