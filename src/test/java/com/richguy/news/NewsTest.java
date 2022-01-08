package com.richguy.news;

import com.richguy.model.news.NewsResult;
import com.richguy.model.news.TopNewsResult;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class NewsTest {

    @Test
    public void urlTest() throws IOException, InterruptedException {
        var url = "http://v.juhe.cn/toutiao/index?type=guoji&key={}";

        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var newsResult = JsonUtils.string2Object(responseBody, TopNewsResult.class);
        System.out.println(JsonUtils.object2String(newsResult));
    }


    @Test
    public void contentTest() throws IOException, InterruptedException {
        var url = "http://v.juhe.cn/toutiao/content?uniquekey=f6cb8a65f7788ea0564f031b2caa85d2&key={}";

        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var newsResult = JsonUtils.string2Object(responseBody, NewsResult.class);
        System.out.println(JsonUtils.object2String(newsResult));
    }

    @Test
    public void allNewsTest() throws IOException, InterruptedException {
        var url1 = "http://v.juhe.cn/toutiao/index?type=top&key=2dc3099f0b849bb44e4c0eb5478e82bb";
        var url2 = "http://v.juhe.cn/toutiao/index?type=guoji&key=2dc3099f0b849bb44e4c0eb5478e82bb";
        var url3 = "http://v.juhe.cn/toutiao/index?type=keji&key=2dc3099f0b849bb44e4c0eb5478e82bb";


        var news = new HashSet<String>();

        doGetNews(url1, news);
        doGetNews(url2, news);
        doGetNews(url3, news);

        for (var key : news) {
            System.out.println(newsContent(key));
        }
    }

    public void doGetNews(String url, Set<String> news) throws IOException, InterruptedException {
        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();

        var responseBody = client.send(request, responseBodyHandler).body();
        var newsResult = JsonUtils.string2Object(responseBody, TopNewsResult.class);

        var result = newsResult.getResult();
        if (result == null) {
            return;
        }

        var datas = newsResult.getResult().getData();
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }

        for (var data : datas) {
            news.add(data.getUniquekey());
        }
    }

    public String newsContent(String key) {
        var url = StringUtils.format("http://v.juhe.cn/toutiao/content?uniquekey={}&key=2dc3099f0b849bb44e4c0eb5478e82bb", key);
        try {
            var client = HttpClient.newBuilder().build();
            var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
            var request = HttpRequest.newBuilder(URI.create(url))
                    .GET()
                    .build();

            var responseBody = client.send(request, responseBodyHandler).body();
            var newsResult = JsonUtils.string2Object(responseBody, NewsResult.class);
            return newsResult.getResult().getContent();
        } catch (Exception e) {
        }
        return StringUtils.EMPTY;
    }

}
