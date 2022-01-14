package com.richguy.industry;

import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class IndustryTest {

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
