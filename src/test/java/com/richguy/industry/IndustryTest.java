package com.richguy.industry;

import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class IndustryTest {

    @Test
    public void gn() throws IOException, ParserConfigurationException, SAXException {
        var html = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("gn.html")));

        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(html)));
        var elements = DomUtils.getElementsByAttribute(document.getDocumentElement(), "class", "cate_items");

        for (var element : elements) {
            var items = DomUtils.getChildElements(element);
            for (var item : items) {
                var code = StringUtils.substringAfterFirst(item.getAttribute("href"), "http://q.10jqka.com.cn/gn/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var content = item.getTextContent();
                System.out.println(StringUtils.format("{}{}{}", code, StringUtils.TAB_ASCII, content));
            }
        }
    }

    @Test
    public void thshy() throws IOException, ParserConfigurationException, SAXException {
        var html = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("thshy.html")));

        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(html)));
        var elements = DomUtils.getElementsByAttribute(document.getDocumentElement(), "class", "cate_items");

        for (var element : elements) {
            var items = DomUtils.getChildElements(element);
            for (var item : items) {
                var code = StringUtils.substringAfterFirst(item.getAttribute("href"), "http://q.10jqka.com.cn/thshy/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var content = item.getTextContent();
                System.out.println(StringUtils.format("{}{}{}", code, StringUtils.TAB_ASCII, content));
            }
        }
    }

}
