package com.richguy.news;

import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.StringUtils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author godotg
 * @version 3.0
 */
@Ignore
public class NewsParseTest {

    @Test
    public void test() throws IOException {
        var txt = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("news.txt")));

        var analysisResult = ToAnalysis.parse(txt);

        var words = analysisResult
                .getTerms()
                .stream()
                .filter(it -> ArrayUtils.isNotEmpty(it.termNatures().termNatures))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("null")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("en")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("d")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("f")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).noneMatch(nature -> nature.nature.natureStr.startsWith("v")))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).anyMatch(nature -> nature.nature.natureStr.startsWith("n")))
                .filter(it -> StringUtils.isNotBlank(it.getName()))
                .collect(Collectors.toList());

        var map = new HashMap<String, Pair<Integer, Term>>();
        for (var word : words) {
            var value = map.computeIfAbsent(word.getName(), it -> new Pair<>(0, word));
            map.put(word.getName(), new Pair<>(value.getKey() + 1, word));
        }
        var topWords = map.entrySet().stream().sorted((a, b) -> b.getValue().getKey() - a.getValue().getKey()).collect(Collectors.toList());

        System.out.println(words);
    }


    @Test
    public void aa() throws IOException {
        var txt = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("news.txt")));

        var analysisResult = ToAnalysis.parse(txt);

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

        var map = new HashMap<String, Integer>();
        var keyWords = words.stream().map(it -> StringUtils.trim(it.getName())).collect(Collectors.toList());
        for (var word : keyWords) {
            var value = map.computeIfAbsent(word, it -> 0);
            map.put(word, value + 1);
        }
        var topWords = map.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).collect(Collectors.toList());

        System.out.println(words);
    }

}
