package com.richguy.util;

import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public abstract class IndustryUtils {

    private static List<Pair<Integer, String>> industryList(String url) throws IOException, InterruptedException {
        var list = new ArrayList<Pair<Integer, String>>();

        var html = HttpUtils.get(url);
        var document = Jsoup.parse(html);
        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var industryEle = cateEle.children();
            for (var ele : industryEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/thshy/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                list.add(new Pair<>(Integer.parseInt(StringUtils.trim(code)), codeName));
            }
        }
        return list;
    }

    public static String industryHtmlUrl(int code) {
        var stockCode = StockUtils.formatCode(code);

        var url = stockCode.startsWith("3")
                ? StringUtils.format("http://q.10jqka.com.cn/gn/detail/code/{}/", stockCode)
                : StringUtils.format("http://q.10jqka.com.cn/thshy/detail/code/{}/", stockCode);

        return url;
    }

    public static int realCode(int code) {
        var urlTemplate = "http://q.10jqka.com.cn/gn/detail/code/{}/";
        if (String.valueOf(code).startsWith("8")) {
            return code;
        }
        var url = StringUtils.format(urlTemplate, code);
        var command = StringUtils.format("node {} {}", "spider\\spider.js", url);
        var str = OSUtils.execCommand(command);

        str = StringUtils.substringAfterFirst(str, "<h3>");
        str = StringUtils.substringBeforeLast(str, "</h3>");
        str = StringUtils.substringAfterFirst(str, "<span>");
        str = StringUtils.substringBeforeLast(str, "</span>");
        var realCode = str.trim();
        return Integer.parseInt(realCode);
    }

    public static List<Pair<Integer, String>> gn() throws IOException, InterruptedException {
        var url = "http://q.10jqka.com.cn/gn/";
        var list = new ArrayList<Pair<Integer, String>>();

        var html = HttpUtils.get(url);
        var document = Jsoup.parse(html);
        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var industryEle = cateEle.children();
            for (var ele : industryEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/gn/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                list.add(new Pair<>(Integer.parseInt(StringUtils.trim(code)), codeName));
            }
        }
        return list;
    }

    public static List<Pair<Integer, String>> thshy() throws IOException, InterruptedException {
        var url = "http://q.10jqka.com.cn/thshy/";
        var list = new ArrayList<Pair<Integer, String>>();

        var html = HttpUtils.get(url);
        var document = Jsoup.parse(html);
        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var industryEle = cateEle.children();
            for (var ele : industryEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/thshy/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                list.add(new Pair<>(Integer.parseInt(StringUtils.trim(code)), codeName));
            }
        }
        return list;
    }

    public static List<Pair<Integer, String>> allIndustryList() throws IOException, InterruptedException {
        var list = new ArrayList<Pair<Integer, String>>();
        list.addAll(gn());
        list.addAll(thshy());
        return list;
    }

}
