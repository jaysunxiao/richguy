package com.richguy.service;

import com.richguy.model.news.NewsResult;
import com.richguy.model.news.TopNewsResult;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Value("${juhe.topNewsUrl}")
    private String topNewsUrl;

    @Value("${juhe.guojiNewsUrl}")
    private String guojiNewsUrl;

    @Value("${juhe.kejiNewsUrl}")
    private String kejiNewsUrl;

    @Value("${juhe.newsUrl}")
    private String newsUrl;

    @Autowired
    private TopWordService topWordService;

    public void newsTopWord() {
        var newsKeys = new HashSet<String>();

        doGetNewsKey(topNewsUrl, newsKeys);
        doGetNewsKey(guojiNewsUrl, newsKeys);
        doGetNewsKey(kejiNewsUrl, newsKeys);

        for (var key : newsKeys) {
            var content = newsContent(key);
            topWordService.topWord(content);
        }
    }

    public void doGetNewsKey(String url, Set<String> newsKeys) {
        try {
            var responseBody = HttpUtils.get(url);
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
                newsKeys.add(data.getUniquekey());
            }
        } catch (Exception e) {
            logger.error("获取新闻详细信息未知异常", e);
        }
    }

    public String newsContent(String key) {
        var url = StringUtils.format(newsUrl, key);
        try {
            var responseBody = HttpUtils.get(url);
            var newsResult = JsonUtils.string2Object(responseBody, NewsResult.class);
            return newsResult.getResult().getContent();
        } catch (Exception e) {
            logger.error("获取新闻详细信息未知异常", e);
        }
        return StringUtils.EMPTY;
    }

}
