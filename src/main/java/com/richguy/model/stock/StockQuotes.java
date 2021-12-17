
package com.richguy.model.stock;


import com.fasterxml.jackson.annotation.JsonProperty;

public class StockQuotes {

    @JsonProperty("dapandata")
    private StockBaseData baseData;
    @JsonProperty("data")
    private StockData stockData;
    @JsonProperty("gopicture")
    private StockPicture stockPicture;

    public StockBaseData getBaseData() {
        return baseData;
    }

    public StockData getStockData() {
        return stockData;
    }

    public StockPicture getStockPicture() {
        return stockPicture;
    }
}
