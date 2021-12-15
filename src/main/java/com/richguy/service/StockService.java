package com.richguy.service;

import com.richguy.resource.IndustryResource;
import com.richguy.resource.IndustryStockResource;
import com.richguy.resource.StockResource;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class StockService {

    @ResInjection
    private Storage<Integer, StockResource> stockResources;
    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;
    @ResInjection
    private Storage<Integer, IndustryStockResource> industryStockResources;

    public List<StockResource> selectStocks(String content) {
        var stockList = new ArrayList<StockResource>();
        for (var stockResource : stockResources.getAll()) {
            if (content.contains(stockResource.getName())) {
                stockList.add(stockResource);
            }
        }
        return stockList;
    }

    public List<IndustryResource> selectIndustry(String content, List<StockResource> stocks) {
        var industrySet = new HashSet<IndustryResource>();

        for (var industryResource : industryResources.getAll()) {
            if (content.contains(industryResource.getName())) {
                industrySet.add(industryResource);
            }
        }

        for (var stock : stocks) {
            for (var industryStockResource : industryStockResources.getAll()) {
                if (stock.getCode() == industryStockResource.getStock() && industryResources.contain(industryStockResource.getIndustry())) {

                    industrySet.add(industryResources.get(industryStockResource.getIndustry()));
                }
            }
        }

        return new ArrayList<>(industrySet);

    }

}
