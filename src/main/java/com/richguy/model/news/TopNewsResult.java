package com.richguy.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopNewsResult {

    @JsonProperty("error_code")
    private int errorCode;

    private String reason;

    private SimpleNews result;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SimpleNews getResult() {
        return result;
    }

    public void setResult(SimpleNews result) {
        this.result = result;
    }
}
