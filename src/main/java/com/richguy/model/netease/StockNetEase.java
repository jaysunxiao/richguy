package com.richguy.model.netease;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockNetEase {

    private String code;

    private String percent;

    private String high;

    private String low;

    private String price;

    private String turnover;

    private String time;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
