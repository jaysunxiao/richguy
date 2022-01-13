package com.richguy.service;

import com.richguy.resource.IgnoreTopWordResource;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class TopWordService {

    @Autowired
    private NewsService newsService;

    private Map<String, Integer> topWordMap = new HashMap<String, Integer>();

    @ResInjection
    private Storage<String, IgnoreTopWordResource> ignoreTopWordResources;

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
            var value = topWordMap.computeIfAbsent(word, it -> 0);
            topWordMap.put(word, value + 1);
        }
    }

    public String topWordToday() {
        var builder = new StringBuilder();

        builder.append("\uD83D\uDCA5热点词语次数统计：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        // 新闻热词统计
        newsService.newsTopWord();

        var topWords = topWordMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(50)
                .collect(Collectors.toList());

        var count = 1;
        for (var pair : topWords) {
            var code = pair.getKey();
            var value = pair.getValue();

            builder.append(StringUtils.format("{}({}) ", code, value));

            count++;

            if (count == 10 || count == 20 || count == 30 || count == 40) {
                builder.append(FileUtils.LS);
                builder.append(FileUtils.LS);
            }
        }

        topWords.clear();

        return builder.toString();
    }
}
