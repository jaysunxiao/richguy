package com.richguy.html;

import com.richguy.model.stock.QuotesResult;
import com.richguy.service.IndustryService;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class HtmlTest {

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Test
    public void stockTest() throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();

        var url = StringUtils.format("http://d.10jqka.com.cn/v2/fiverange/hs_{}/last.js", "600010");

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        System.out.println(responseBody);
    }

    @Test
    public void quoteTest() throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();

        var url = StringUtils.format("http://d.10jqka.com.cn/v4/time/bk_{}/last.js", "881129");

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
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

        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var quote = JsonUtils.string2Object(responseBody, QuotesResult.class);
        System.out.println(quote.getResult().get(0).getBaseData().getRate());
    }
}
