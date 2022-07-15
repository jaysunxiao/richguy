package com.richguy.html;

import com.richguy.model.stock.QuotesResult;
import com.richguy.service.IndustryService;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author godotg
 * @version 3.0
 */
@Ignore
public class HtmlTest {

    @Test
    public void stockTest() throws IOException, InterruptedException {
        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", "600010");
        var responseBody = HttpUtils.get(url);
        System.out.println(responseBody);
    }

    @Test
    public void quoteTest() throws IOException, InterruptedException {
        var url = StringUtils.format("http://d.10jqka.com.cn/v4/time/bk_{}/last.js", "881129");
        var responseBody = HttpUtils.get(url);
        System.out.println(responseBody);
    }

    @Test
    public void htmlunitTest() throws IOException, InterruptedException {
        var industryService = new IndustryService();
        var result = industryService.doGetBkQuoteByHtml(881101);
        System.out.println(result);
        result = industryService.doGetBkQuoteByHtml(307550);
        System.out.println(result);
    }

    @Test
    public void juheTest() throws IOException, InterruptedException {
        var str = "http://web.juhe.cn:8080/finance/stock/hs?gid=sh{}&key=15f41a912789ce8a7eff4a05586ca2dc";
        var url = StringUtils.format(str, "601952");

        var responseBody = HttpUtils.get(url);
        var quote = JsonUtils.string2Object(responseBody, QuotesResult.class);
        System.out.println(quote.getResult().get(0).getBaseData().getRate());
    }
}
