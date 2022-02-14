package com.richguy.model.common;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class StockPriceAndRise {

    private float price;
    private float rise;

    public static StockPriceAndRise valueOf(float price, float rise) {
        var stock = new StockPriceAndRise();
        stock.price = price;
        stock.rise = rise;
        return stock;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRise() {
        return rise;
    }

    public void setRise(float rise) {
        this.rise = rise;
    }
}
