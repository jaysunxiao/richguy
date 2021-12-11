package com.richguy.model.quote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zfoo.protocol.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

    private String name;
    private String pre;
    private String data;

    public String nowIndex() {
        if (StringUtils.isEmpty(data)) {
            return pre;
        }

        var splits = data.split(StringUtils.SEMICOLON_REGEX);
        var nowData = splits[splits.length - 1];
        var nowSplits = nowData.split(StringUtils.COMMA_REGEX);
        var nowIndex = nowSplits[1];
        return nowIndex;
    }

    public float increaseRatioFloat() {
        var preIndex = Float.parseFloat(pre);
        var nowIndex = Float.parseFloat(nowIndex());
        var increaseRatio = (nowIndex - preIndex) / preIndex * 100;
        return increaseRatio;
    }

    public String increaseRatio() {
        var decimal = new BigDecimal(increaseRatioFloat());
        return decimal.setScale(1, RoundingMode.HALF_UP).toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
