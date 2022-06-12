package com.richguy.service;

import com.richguy.model.news.NewsResult;
import com.richguy.model.news.TopNewsResult;
import com.richguy.resource.IgnoreTopWordResource;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 统计新闻的高频词汇
 *
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class TopNewsService {

    private static final Logger logger = LoggerFactory.getLogger(TopNewsService.class);

    @Value("${juhe.topNewsUrl}")
    private String topNewsUrl;

    @Value("${juhe.guojiNewsUrl}")
    private String guojiNewsUrl;

    @Value("${juhe.kejiNewsUrl}")
    private String kejiNewsUrl;

    @Value("${juhe.newsUrl}")
    private String newsUrl;

    @Autowired
    private DatabaseService databaseService;

    @ResInjection
    private Storage<String, IgnoreTopWordResource> ignoreTopWordResources;


    public String topWordToday() {
        var builder = new StringBuilder();

        builder.append("\uD83D\uDCA5热点词语次数统计：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        // 新闻热词统计
        newsTopWord();

        var database = databaseService.database;

        var topWords = database.getTopWordMap().entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(60)
                .collect(Collectors.toList());

        var count = 1;
        for (var pair : topWords) {
            var word = pair.getKey();
            var value = pair.getValue();

            if (database.getOldTopWordMap().containsKey(word)) {
                var oldRank = topWordRank(word, database.getOldTopWordMap());
                var newRank = topWordRank(word, database.getTopWordMap());
                var changeRank = oldRank - newRank;
                if (changeRank >= 0) {
                    builder.append(StringUtils.format("{}. {}({}) +{}", count, word, value, changeRank));
                } else {
                    builder.append(StringUtils.format("{}. {}({}) {}", count, word, value, changeRank));
                }
            } else {

                builder.append(StringUtils.format("{}. {}({}) ", count, word, value));
            }

            builder.append(FileUtils.LS);

            count++;

            if (count == 10 || count == 20 || count == 30 || count == 40 || count == 50) {
                builder.append(FileUtils.LS);
            }
        }

        database.clearTopWordMap();

        return builder.toString();
    }

    private void newsTopWord() {
        var newsKeys = new HashSet<String>();

        doGetNewsKey(topNewsUrl, newsKeys);
        doGetNewsKey(guojiNewsUrl, newsKeys);
        doGetNewsKey(kejiNewsUrl, newsKeys);

        for (var key : newsKeys) {
            var content = newsContent(key);
            topWord(content);
        }
    }

    private void doGetNewsKey(String url, Set<String> newsKeys) {
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
            logger.info("获取新闻详细信息未知异常", e);
        }
    }

    private String newsContent(String key) {
        var url = StringUtils.format(newsUrl, key);
        try {
            var responseBody = HttpUtils.get(url);
            var newsResult = JsonUtils.string2Object(responseBody, NewsResult.class);
            return newsResult.getResult().getContent();
        } catch (Exception e) {
            logger.info("获取新闻详细信息未知异常", e);
        }
        return StringUtils.EMPTY;
    }

    // 解析文本
    private void topWord(String content) {
        var analysisResult = ToAnalysis.parse(content);

        var words = analysisResult
                .getTerms()
                .stream()
                .filter(it -> ArrayUtils.isNotEmpty(it.termNatures().termNatures))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("null")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("en")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("d")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("f")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("v")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).anyMatch(nature -> nature.nature.natureStr.contains("n")))
                .filter(it -> StringUtils.isNotBlank(it.getName()))
                .collect(Collectors.toList());

        var keyWords = words.stream()
                .map(it -> StringUtils.trim(it.getName()))
                .filter(it -> !ignoreTopWordResources.contain(it))
                .collect(Collectors.toList());

        for (var word : keyWords) {
            databaseService.database.addTopWordMap(word);
        }
    }


    private int topWordRank(String word, Map<String, Integer> topWordMap) {
        if (!topWordMap.containsKey(word)) {
            return 0;
        }

        var topList = topWordMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        var rank = 0;
        for (int i = 0; i < topList.size(); i++) {
            var topIndustry = topList.get(i);
            if (topIndustry.getKey().equals(word)) {
                rank = i + 1;
                break;
            }
        }

        return rank;
    }

}
