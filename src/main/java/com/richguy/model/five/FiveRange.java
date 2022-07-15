package com.richguy.model.five;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author godotg
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiveRange {

    @JsonProperty("6")
    private String prePrice;

    @JsonProperty("24")
    private String buy1;
    @JsonProperty("25")
    private String buy1Num;
    @JsonProperty("26")
    private String buy2;
    @JsonProperty("27")
    private String buy2Num;
    @JsonProperty("28")
    private String buy3;
    @JsonProperty("29")
    private String buy3Num;
    @JsonProperty("150")
    private String buy4;
    @JsonProperty("151")
    private String buy4Num;
    @JsonProperty("154")
    private String buy5;
    @JsonProperty("155")
    private String buy5Num;

    @JsonProperty("30")
    private String sell1;
    @JsonProperty("31")
    private String sell1Num;
    @JsonProperty("32")
    private String sell2;
    @JsonProperty("33")
    private String sell2Num;
    @JsonProperty("34")
    private String sell3;
    @JsonProperty("35")
    private String sell3Num;
    @JsonProperty("152")
    private String sell4;
    @JsonProperty("153")
    private String sell4Num;
    @JsonProperty("156")
    private String sell5;
    @JsonProperty("157")
    private String sell5Num;

    public float increaseRatioFloat() {
        var preIndex = Float.parseFloat(prePrice);
        var nowIndex = Float.parseFloat(buy1);
        var increaseRatio = (nowIndex - preIndex) / preIndex * 100;
        return increaseRatio;
    }

    public String increaseRatio() {
        var decimal = new BigDecimal(increaseRatioFloat());
        return decimal.setScale(1, RoundingMode.HALF_UP).toString();
    }

    public String getPrePrice() {
        return prePrice;
    }

    public void setPrePrice(String prePrice) {
        this.prePrice = prePrice;
    }

    public String getBuy1() {
        return buy1;
    }

    public void setBuy1(String buy1) {
        this.buy1 = buy1;
    }

    public String getBuy1Num() {
        return buy1Num;
    }

    public void setBuy1Num(String buy1Num) {
        this.buy1Num = buy1Num;
    }

    public String getBuy2() {
        return buy2;
    }

    public void setBuy2(String buy2) {
        this.buy2 = buy2;
    }

    public String getBuy2Num() {
        return buy2Num;
    }

    public void setBuy2Num(String buy2Num) {
        this.buy2Num = buy2Num;
    }

    public String getBuy3() {
        return buy3;
    }

    public void setBuy3(String buy3) {
        this.buy3 = buy3;
    }

    public String getBuy3Num() {
        return buy3Num;
    }

    public void setBuy3Num(String buy3Num) {
        this.buy3Num = buy3Num;
    }

    public String getBuy4() {
        return buy4;
    }

    public void setBuy4(String buy4) {
        this.buy4 = buy4;
    }

    public String getBuy4Num() {
        return buy4Num;
    }

    public void setBuy4Num(String buy4Num) {
        this.buy4Num = buy4Num;
    }

    public String getBuy5() {
        return buy5;
    }

    public void setBuy5(String buy5) {
        this.buy5 = buy5;
    }

    public String getBuy5Num() {
        return buy5Num;
    }

    public void setBuy5Num(String buy5Num) {
        this.buy5Num = buy5Num;
    }

    public String getSell1() {
        return sell1;
    }

    public void setSell1(String sell1) {
        this.sell1 = sell1;
    }

    public String getSell1Num() {
        return sell1Num;
    }

    public void setSell1Num(String sell1Num) {
        this.sell1Num = sell1Num;
    }

    public String getSell2() {
        return sell2;
    }

    public void setSell2(String sell2) {
        this.sell2 = sell2;
    }

    public String getSell2Num() {
        return sell2Num;
    }

    public void setSell2Num(String sell2Num) {
        this.sell2Num = sell2Num;
    }

    public String getSell3() {
        return sell3;
    }

    public void setSell3(String sell3) {
        this.sell3 = sell3;
    }

    public String getSell3Num() {
        return sell3Num;
    }

    public void setSell3Num(String sell3Num) {
        this.sell3Num = sell3Num;
    }

    public String getSell4() {
        return sell4;
    }

    public void setSell4(String sell4) {
        this.sell4 = sell4;
    }

    public String getSell4Num() {
        return sell4Num;
    }

    public void setSell4Num(String sell4Num) {
        this.sell4Num = sell4Num;
    }

    public String getSell5() {
        return sell5;
    }

    public void setSell5(String sell5) {
        this.sell5 = sell5;
    }

    public String getSell5Num() {
        return sell5Num;
    }

    public void setSell5Num(String sell5Num) {
        this.sell5Num = sell5Num;
    }
}
