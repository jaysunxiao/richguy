package com.richguy.model.yidong;

import java.math.BigDecimal;

/**
 * @author godotg
 * @version 3.0
 */
public class StockHistory {

    // 日期
    private String date;

    // 股票代码
    private String code;

    // 名称
    private String name;

    // 收盘价
    private BigDecimal endPrice;

    // 两日偏离
    private BigDecimal twoPianLi;
    // 三日偏离
    private BigDecimal threePianLi;

    // 换手率
    private String huanShou;

    // 成交金额
    private String chengJiao;

    public static StockHistory valueOf(String date, String code, String name, BigDecimal endPrice, String huanShou, String chengJiao) {
        var stock = new StockHistory();
        stock.date = date;
        stock.code = code;
        stock.name = name;
        stock.endPrice = endPrice;
        stock.huanShou = huanShou;
        stock.chengJiao = chengJiao;
        return stock;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public String getHuanShou() {
        return huanShou;
    }

    public void setHuanShou(String huanShou) {
        this.huanShou = huanShou;
    }

    public String getChengJiao() {
        return chengJiao;
    }

    public void setChengJiao(String chengJiao) {
        this.chengJiao = chengJiao;
    }

    public BigDecimal getTwoPianLi() {
        return twoPianLi;
    }

    public void setTwoPianLi(BigDecimal twoPianLi) {
        this.twoPianLi = twoPianLi;
    }

    public BigDecimal getThreePianLi() {
        return threePianLi;
    }

    public void setThreePianLi(BigDecimal threePianLi) {
        this.threePianLi = threePianLi;
    }
}
