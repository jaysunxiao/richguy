package com.richguy.model.wencai;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author godotg
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


    public static WenCaiRequest valueOf(String question, int perpage, int page, String source, String secondaryIntent, String version) {
        var request = new WenCaiRequest();
        request.question = question;
        request.perpage = perpage;
        request.page = page;
        request.source = source;
        request.secondaryIntent = secondaryIntent;
        request.version = version;
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

}
