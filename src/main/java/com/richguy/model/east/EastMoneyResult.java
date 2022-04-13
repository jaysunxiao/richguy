package com.richguy.model.east;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EastMoneyResult {

    private int lt;

    private EastMoneyData data;

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public EastMoneyData getData() {
        return data;
    }

    public void setData(EastMoneyData data) {
        this.data = data;
    }
}
