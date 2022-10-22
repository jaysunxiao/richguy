package com.richguy.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.util.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
public abstract class HttpUtils {

    public static RestTemplate restTemplate;

    static {
        int timeout = 15000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);

        restTemplate = new RestTemplate(clientHttpRequestFactory);
    }


    public static String get(String url) throws IOException, InterruptedException {
        return restTemplate.getForObject(url, String.class);
    }

    public static byte[] getBytes(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }

    public static String post(String url, Object jsonObject) throws IOException, InterruptedException {
        return restTemplate.postForObject(url, jsonObject, String.class);
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
