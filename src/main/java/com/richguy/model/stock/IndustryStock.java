package com.richguy.model.stock;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class IndustryStock {

    private String industry;

    private String code;

    public static IndustryStock valueOf(String industry, String code) {
        var packet = new IndustryStock();
        packet.industry = industry;
        packet.code = code;
        return packet;
    }

    @Override
    public String toString() {
        return StringUtils.format("{}{}{}{}"
                , industry, StringUtils.TAB_ASCII
                , industry, StringUtils.TAB_ASCII);
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
