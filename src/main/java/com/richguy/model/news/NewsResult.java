package com.richguy.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsResult {

    private String reason;

    @JsonProperty("error_code")
    private int errorCode;

    private NewsDetail result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public NewsDetail getResult() {
        return result;
    }

    public void setResult(NewsDetail result) {
        this.result = result;
    }
}
