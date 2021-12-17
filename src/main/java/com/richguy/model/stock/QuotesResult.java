
package com.richguy.model.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QuotesResult {

    @JsonProperty("error_code")
    private int errorCode;
    private String reason;
    private List<StockQuotes> result;
    private String resultcode;


    public int getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }

    public List<StockQuotes> getResult() {
        return result;
    }

    public String getResultcode() {
        return resultcode;
    }
}
