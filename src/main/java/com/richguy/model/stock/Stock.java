package com.richguy.model.stock;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class Stock {

    // 代码
    private String code;

    // 股票名称
    private String name;

    // 现价
    private String nowPrice;

    // 涨跌幅
    private String riseRatio;

    // 涨跌
    private String riseNum;

    // 涨速
    private String increaseRatio;

    // 换手率
    private String turnoverRatio;

    // 量比
    private String volumeRatio;

    // 振幅
    private String vibration;

    // 成交额
    private String turnover;

    // 流通股
    private String floatingStock;

    // 流通市值
    private String marketValue;

    // 市盈率
    private String pe;

    public static Stock valueOf(String code, String name, String nowPrice, String riseRatio, String riseNum, String increaseRatio, String turnoverRatio, String volumeRatio, String vibration, String turnover, String floatingStock, String marketValue, String pe) {
        var stock = new Stock();
        stock.code = code;
        stock.name = name;
        stock.nowPrice = nowPrice;
        stock.riseRatio = riseRatio;
        stock.riseNum = riseNum;
        stock.increaseRatio = increaseRatio;
        stock.turnoverRatio = turnoverRatio;
        stock.volumeRatio = volumeRatio;
        stock.vibration = vibration;
        stock.turnover = turnover;
        stock.floatingStock = floatingStock;
        stock.marketValue = marketValue;
        stock.pe = pe;
        return stock;
    }

    @Override
    public String toString() {
        return StringUtils.format("{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}"
                , code, StringUtils.TAB_ASCII
                , name, StringUtils.TAB_ASCII
                , nowPrice, StringUtils.TAB_ASCII
                , riseRatio, StringUtils.TAB_ASCII
                , riseNum, StringUtils.TAB_ASCII
                , increaseRatio, StringUtils.TAB_ASCII
                , turnoverRatio, StringUtils.TAB_ASCII
                , volumeRatio, StringUtils.TAB_ASCII
                , vibration, StringUtils.TAB_ASCII
                , turnover, StringUtils.TAB_ASCII
                , floatingStock, StringUtils.TAB_ASCII
                , marketValue, StringUtils.TAB_ASCII
                , pe, StringUtils.TAB_ASCII);
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

    public String getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(String nowPrice) {
        this.nowPrice = nowPrice;
    }

    public String getRiseRatio() {
        return riseRatio;
    }

    public void setRiseRatio(String riseRatio) {
        this.riseRatio = riseRatio;
    }

    public String getRiseNum() {
        return riseNum;
    }

    public void setRiseNum(String riseNum) {
        this.riseNum = riseNum;
    }

    public String getIncreaseRatio() {
        return increaseRatio;
    }

    public void setIncreaseRatio(String increaseRatio) {
        this.increaseRatio = increaseRatio;
    }

    public String getTurnoverRatio() {
        return turnoverRatio;
    }

    public void setTurnoverRatio(String turnoverRatio) {
        this.turnoverRatio = turnoverRatio;
    }

    public String getVolumeRatio() {
        return volumeRatio;
    }

    public void setVolumeRatio(String volumeRatio) {
        this.volumeRatio = volumeRatio;
    }

    public String getVibration() {
        return vibration;
    }

    public void setVibration(String vibration) {
        this.vibration = vibration;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getFloatingStock() {
        return floatingStock;
    }

    public void setFloatingStock(String floatingStock) {
        this.floatingStock = floatingStock;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }
}
