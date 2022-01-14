package com.richguy.industry;

import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.StringUtils;
import org.jsoup.Jsoup;
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
public class IndustryTest {

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    @Test
    public void gnHtmlTest() throws IOException, InterruptedException {
        var gnUrl = "http://q.10jqka.com.cn/gn/";

        var client = HttpClient.newBuilder().build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(gnUrl))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var html = client.send(request, responseBodyHandler).body();

        var document = Jsoup.parse(html);

        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var gnEle = cateEle.children();
            for (var ele : gnEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/gn/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var content = ele.text();
                System.out.println(StringUtils.format("{}{}{}", code, StringUtils.TAB_ASCII, content));
            }
        }
    }

    @Test
    public void thshyHtmlTest() throws IOException, InterruptedException {
        var gnUrl = "http://q.10jqka.com.cn/thshy/";

        var client = HttpClient.newBuilder().build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(gnUrl))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .GET()
                .build();

        var html = client.send(request, responseBodyHandler).body();

        var document = Jsoup.parse(html);

        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var gnEle = cateEle.children();
            for (var ele : gnEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/thshy/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var content = ele.text();
                System.out.println(StringUtils.format("{}{}{}", code, StringUtils.TAB_ASCII, content));
            }
        }
    }


}
