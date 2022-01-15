package com.richguy.industry;

import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class IndustryTest {

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Test
    public void gnHtmlTest() throws IOException, InterruptedException {
        var list = IndustryUtils.gn();
        for (var ele : list) {
            System.out.println(StringUtils.format("{}{}{}", ele.getKey(), StringUtils.TAB_ASCII, ele.getValue()));
        }
    }

    @Test
    public void thshyHtmlTest() throws IOException, InterruptedException {
        var list = IndustryUtils.thshy();
        for (var ele : list) {
            System.out.println(StringUtils.format("{}{}{}", ele.getKey(), StringUtils.TAB_ASCII, ele.getValue()));
        }
    }


}
