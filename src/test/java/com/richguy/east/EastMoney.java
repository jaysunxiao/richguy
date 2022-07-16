package com.richguy.east;

import com.richguy.controller.YiDongController;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class EastMoney {

    @Test
    public void yiDong() {
        var controller = new YiDongController();
        var yiDong = controller.yiDong("000755");
        System.out.println(yiDong);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        var urlTemplate = "http://quotes.money.163.com/service/chddata.html?code={}&start={}&end={}";
        var url = StringUtils.format(urlTemplate, "1399001", "20220710", "20220715");

        var bytes = HttpUtils.getBytes(url);
        var str = new String(bytes, Charset.forName("gb2312"));
        System.out.println(str);
    }
}
