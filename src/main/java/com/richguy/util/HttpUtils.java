package com.richguy.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;

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

}
