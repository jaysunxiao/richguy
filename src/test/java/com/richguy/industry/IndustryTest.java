package com.richguy.industry;

import com.richguy.resource.IndustryResource;
import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.interpreter.ExcelResourceReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class IndustryTest {

    @Test
    public void allIndustryListlTest() throws IOException, InterruptedException {
        var list = IndustryUtils.allIndustryList();
        for (var ele : list) {
            System.out.println(StringUtils.format("{}{}{}", ele.getKey(), StringUtils.TAB_ASCII, ele.getValue()));
        }
    }


    @Test
    public void diffTest() throws IOException, InterruptedException {
        var reader = new ExcelResourceReader();
        var list = (List<IndustryResource>) reader.read(ClassUtils.getFileFromClassPath("excel/IndustryResource.xlsx"), IndustryResource.class);

        var allIndustry = IndustryUtils.allIndustryList();

        var newIndustrySet = new HashSet<Pair<Integer, String>>();
        for (var industry : allIndustry) {
            if (list.stream().noneMatch(it -> it.getName().equals(StringUtils.trim(industry.getValue())))) {
                newIndustrySet.add(industry);
            }

            if (list.stream().noneMatch(it -> it.getCode() == industry.getKey())) {
                newIndustrySet.add(industry);
            }
        }

        for (var newIndustry : newIndustrySet) {
            System.out.println(newIndustry.getValue());
        }
    }

}
