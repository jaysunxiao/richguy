package com.richguy.model;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author godotg
 * @version 3.0
 */
public class IndustryStockVO {

    private String industry;

    private String code;

    public static IndustryStockVO valueOf(String industry, String code) {
        var packet = new IndustryStockVO();
        packet.industry = industry;
        packet.code = code;
        return packet;
    }

    @Override
    public String toString() {
        return StringUtils.format("{}{}{}{}"
                , industry, StringUtils.TAB_ASCII
                , code, StringUtils.TAB_ASCII);
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
