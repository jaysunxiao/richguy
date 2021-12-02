package com.richguy.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Telegraph {

    private int error;

    private NewsData data;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public NewsData getData() {
        return data;
    }

    public void setData(NewsData data) {
        this.data = data;
    }
}
