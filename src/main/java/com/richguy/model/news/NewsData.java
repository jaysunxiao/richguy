package com.richguy.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsData {

    private String uniquekey;
    private String title;
    private String date;
    private String category;
    private String author_name;
    private String url;

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
