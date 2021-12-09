package com.richguy.model.stock;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class StockGroup {

    private List<Stock> stocks;

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }
}
