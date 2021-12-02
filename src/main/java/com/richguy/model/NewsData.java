package com.richguy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsData {

    @JsonProperty("roll_data")
    private List<OneNews> rollData;

    @JsonProperty("update_num")
    private int updateNum;

    public List<OneNews> getRollData() {
        return rollData;
    }

    public void setRollData(List<OneNews> rollData) {
        this.rollData = rollData;
    }

    public int getUpdateNum() {
        return updateNum;
    }

    public void setUpdateNum(int updateNum) {
        this.updateNum = updateNum;
    }
}
