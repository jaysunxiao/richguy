package com.richguy.model.east;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EastMoneyIndustry {

    // BK1077
    private String f12;

    // 90
    private String f13;

    @JsonProperty("f14")
    private String gn;


    public String getF12() {
        return f12;
    }

    public void setF12(String f12) {
        this.f12 = f12;
    }

    public String getF13() {
        return f13;
    }

    public void setF13(String f13) {
        this.f13 = f13;
    }

    public String getGn() {
        return gn;
    }

    public void setGn(String gn) {
        this.gn = gn;
    }
}
