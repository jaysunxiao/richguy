package com.richguy.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsDetail {

    private String uniquekey;

    private String content;

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
