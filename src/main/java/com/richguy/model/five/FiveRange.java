package com.richguy.model.five;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiveRange {

    @JsonProperty("6")
    private String prePrice;

    @JsonProperty("24")
    private String buy1;
    @JsonProperty("25")
    private String buy1Price;
    @JsonProperty("26")
    private String buy2;
    @JsonProperty("27")
    private String buy2Price;
    @JsonProperty("28")
    private String buy3;
    @JsonProperty("29")
    private String buy3Price;
    @JsonProperty("150")
    private String buy4;
    @JsonProperty("151")
    private String buy4Price;
    @JsonProperty("154")
    private String buy5;
    @JsonProperty("155")
    private String buy5Price;

    @JsonProperty("30")
    private String sell1;
    @JsonProperty("31")
    private String sell1Price;
    @JsonProperty("32")
    private String sell2;
    @JsonProperty("33")
    private String sell2Price;
    @JsonProperty("34")
    private String sell3;
    @JsonProperty("35")
    private String sell3Price;
    @JsonProperty("152")
    private String sell4;
    @JsonProperty("153")
    private String sell4Price;
    @JsonProperty("156")
    private String sell5;
    @JsonProperty("157")
    private String sell5Price;

    public String increaseRatio() {
        var preIndex = Float.parseFloat(prePrice);
        var nowIndex = Float.parseFloat(buy1);
        var increaseRatio = (nowIndex - preIndex) / preIndex * 100;
        var decimal = new BigDecimal(increaseRatio);
        return decimal.setScale(2, RoundingMode.HALF_UP).toString();
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

    public String getBuy1Price() {
        return buy1Price;
    }

    public void setBuy1Price(String buy1Price) {
        this.buy1Price = buy1Price;
    }

    public String getBuy2() {
        return buy2;
    }

    public void setBuy2(String buy2) {
        this.buy2 = buy2;
    }

    public String getBuy2Price() {
        return buy2Price;
    }

    public void setBuy2Price(String buy2Price) {
        this.buy2Price = buy2Price;
    }

    public String getBuy3() {
        return buy3;
    }

    public void setBuy3(String buy3) {
        this.buy3 = buy3;
    }

    public String getBuy3Price() {
        return buy3Price;
    }

    public void setBuy3Price(String buy3Price) {
        this.buy3Price = buy3Price;
    }

    public String getBuy4() {
        return buy4;
    }

    public void setBuy4(String buy4) {
        this.buy4 = buy4;
    }

    public String getBuy4Price() {
        return buy4Price;
    }

    public void setBuy4Price(String buy4Price) {
        this.buy4Price = buy4Price;
    }

    public String getBuy5() {
        return buy5;
    }

    public void setBuy5(String buy5) {
        this.buy5 = buy5;
    }

    public String getBuy5Price() {
        return buy5Price;
    }

    public void setBuy5Price(String buy5Price) {
        this.buy5Price = buy5Price;
    }

    public String getSell1() {
        return sell1;
    }

    public void setSell1(String sell1) {
        this.sell1 = sell1;
    }

    public String getSell1Price() {
        return sell1Price;
    }

    public void setSell1Price(String sell1Price) {
        this.sell1Price = sell1Price;
    }

    public String getSell2() {
        return sell2;
    }

    public void setSell2(String sell2) {
        this.sell2 = sell2;
    }

    public String getSell2Price() {
        return sell2Price;
    }

    public void setSell2Price(String sell2Price) {
        this.sell2Price = sell2Price;
    }

    public String getSell3() {
        return sell3;
    }

    public void setSell3(String sell3) {
        this.sell3 = sell3;
    }

    public String getSell3Price() {
        return sell3Price;
    }

    public void setSell3Price(String sell3Price) {
        this.sell3Price = sell3Price;
    }

    public String getSell4() {
        return sell4;
    }

    public void setSell4(String sell4) {
        this.sell4 = sell4;
    }

    public String getSell4Price() {
        return sell4Price;
    }

    public void setSell4Price(String sell4Price) {
        this.sell4Price = sell4Price;
    }

    public String getSell5() {
        return sell5;
    }

    public void setSell5(String sell5) {
        this.sell5 = sell5;
    }

    public String getSell5Price() {
        return sell5Price;
    }

    public void setSell5Price(String sell5Price) {
        this.sell5Price = sell5Price;
    }
}
