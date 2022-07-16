package com.richguy.stock;

import com.richguy.resource.StockResource;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.storage.interpreter.ResourceReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
@Ignore
public class StockCapTest {

    @Test
    public void test() throws IOException {
        var reader = new ResourceReader();
        var list = (List<StockResource>) reader.read(ClassUtils.getFileFromClassPath("excel/StockResource.xlsx"), StockResource.class, "xlsx");

        var total = 0D;
        for (var resource : list) {
//            var cap = Double.parseDouble(StringUtils.trim(StringUtils.substringBeforeLast(resource.getCap(), "亿")));
//            total += cap;
        }
        System.out.println(total);
    }

}
