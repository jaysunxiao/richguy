package com.richguy.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
public abstract class HttpUtils {

    public static final List<String> HEADERS = List.of(
            "accept", "*/*",
            "Accept-Language", "zh-CN,zh;q=0.9",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
    );

    public static String get(String url) throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(HEADERS, String.class))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        var result = client.send(request, responseBodyHandler).body();
        return result;
    }

    public static byte[] getBytes(String url)  {
        try {
            var client = HttpClient.newBuilder().build();

            var responseBodyHandler = HttpResponse.BodyHandlers.ofByteArray();
            var request = HttpRequest.newBuilder(URI.create(url))
                    .headers(ArrayUtils.listToArray(HEADERS, String.class))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            var result = client.send(request, responseBodyHandler).body();
            return result;
        } catch (Exception e) {
            throw new RunException(e);
        }
    }

    public static String post(String url, Object jsonObject) throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();

        var postBody = JsonUtils.object2String(jsonObject);

        var headers = new ArrayList<>(HEADERS);
        headers.add("Content-Type");
        headers.add("application/json");

        var request = HttpRequest.newBuilder(URI.create(url))
                .headers(ArrayUtils.listToArray(headers, String.class))
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();

        var result = client.send(request, responseBodyHandler).body();
        return result;
    }

    public static String formatJson(String json) {
        json = StringUtils.substringAfterFirst(json, "(");
        json = StringUtils.substringBeforeLast(json, ")");
        return json;
    }

    public static String html(String url) throws IOException {
        var webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.getOptions().setGeolocationEnabled(false);
        webClient.getOptions().setWebSocketEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        var page = webClient.getPage(url);
        var responseBody = page.getWebResponse().getContentAsString();
        return responseBody;
    }


    public static String puppeteer(String url) throws IOException {
        var command = StringUtils.format("node {} {}", "/usr/local/spider/spider.js", url);
        var str = OSUtils.execCommand(command);
        return str;
    }

    /**
     * 搜索引擎UA：
     * 百度
     * 谷歌
     * 搜狗
     * 360
     * 神马
     * 今日头条
     * 雅虎
     * 必应
     * qq
     * 腾讯
     */
    private static final List<String> SEARCH_ENGINE_UA_LIST = List.of("baidu", "google", "sogou"
            , "360spider", "yisou", "bytespider", "yahoo", "bingbot", "qq", "tencent");

    public static boolean isSpiderRequest(HttpServletRequest request) {
        var userAgent = request.getHeader("user-agent");
        if (StringUtils.isBlank(userAgent)) {
            return false;
        }

        var lowerCaseUserAgent = userAgent.toLowerCase();
        if (lowerCaseUserAgent.contains("spider") || lowerCaseUserAgent.contains("bot")) {
            if (SEARCH_ENGINE_UA_LIST.stream().anyMatch(it -> lowerCaseUserAgent.contains(it))) {
                return true;
            }
        }
        return false;
    }

}
