package com.richguy.service;

import com.richguy.resource.IgnoreTopWordResource;
import com.richguy.resource.IndustryResource;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class TopWordService {

    @Autowired
    private NewsService newsService;
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private IndustryService industryService;


    @ResInjection
    private Storage<String, IgnoreTopWordResource> ignoreTopWordResources;
    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    // 解析文本
    public void topWord(String content) {
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

    public String topWordToday() {
        var builder = new StringBuilder();

        builder.append("\uD83D\uDCA5热点词语次数统计：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        // 新闻热词统计
        newsService.newsTopWord();

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

        database.clearTopWorldMap();

        return builder.toString();
    }

    public int topWordRank(String word, Map<String, Integer> topWordMap) {
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


    // -----------------------------------------------------------------------
    public String topIndustryToday() {
        var ignoreIndustry = Set.of(300900, 308594, 301636, 303944, 301490);

        var builder = new StringBuilder();

        builder.append("\uD83C\uDF20电报热点板块次数统计：");

        var database = databaseService.database;

        var topList = database.getTopIndustryMap().entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(30)
                .collect(Collectors.toList());

        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        var count = 1;
        for (var pair : topList) {
            var code = pair.getKey();
            if (ignoreIndustry.contains(code)) {
                continue;
            }

            var industryResource = industryResources.get(code);
            var quote = industryService.bkQuote(code);
            var quoteSimpleRatio = StockUtils.toSimpleRatio(quote);
            var name = industryResource.getName();

            if (database.getOldTopIndustryMap().containsKey(code)) {
                var oldRank = topIndustryRank(code, database.getOldTopIndustryMap());
                var newRank = topIndustryRank(code, database.getTopIndustryMap());
                var changeRank = oldRank - newRank;
                if (changeRank >= 0) {
                    builder.append(StringUtils.format("{}. {}({}) ({}) +{}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                } else {
                    builder.append(StringUtils.format("{}. {}({}) ({}) {}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                }
            } else {
                builder.append(StringUtils.format("{}. {}({}) ({}) +0", count, name, quoteSimpleRatio, pair.getValue()));
            }
            builder.append(FileUtils.LS);

            count++;

            if (count == 10 || count == 20 || count == 30) {
                builder.append(FileUtils.LS);
            }
        }

        database.clearTopIndustryMap();
        return builder.toString();
    }


    public int topIndustryRank(int code, Map<Integer, Integer> topIndustryMap) {
        if (!topIndustryMap.containsKey(code)) {
            return 0;
        }

        var topList = topIndustryMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        var rank = 0;
        for (int i = 0; i < topList.size(); i++) {
            var topIndustry = topList.get(i);
            if (topIndustry.getKey() == code) {
                rank = i + 1;
                break;
            }
        }

        return rank;
    }

}
