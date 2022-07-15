package com.richguy.model.east;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EastMoneyData {

    private int total;

    private List<EastMoneyIndustry> diff;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<EastMoneyIndustry> getDiff() {
        return diff;
    }

    public void setDiff(List<EastMoneyIndustry> diff) {
        this.diff = diff;
    }
}
