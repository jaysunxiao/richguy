package com.richguy.model.wencai;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class WenCaiRequest {

    private String question;

    private int perpage;

    private int page;

    private String source;

    @JsonProperty("secondary_intent")
    private String secondaryIntent;

    private String version;

    @JsonProperty("add_info")
    private String addInfo;

    @JsonProperty("log_info")
    private String logInfo;

    public static WenCaiRequest valueOf(String question, int perpage, int page, String source, String secondaryIntent, String version, String addInfo, String logInfo) {
        var request = new WenCaiRequest();
        request.question = question;
        request.perpage = perpage;
        request.page = page;
        request.source = source;
        request.secondaryIntent = secondaryIntent;
        request.version = version;
        request.addInfo = addInfo;
        request.logInfo = logInfo;
        return request;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSecondaryIntent() {
        return secondaryIntent;
    }

    public void setSecondaryIntent(String secondaryIntent) {
        this.secondaryIntent = secondaryIntent;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }
}
