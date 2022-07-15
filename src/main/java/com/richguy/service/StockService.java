package com.richguy.service;

import com.richguy.model.OneNews;
import com.richguy.resource.IndustryResource;
import com.richguy.resource.IndustryStockResource;
import com.richguy.resource.StockResource;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author godotg
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

    public String toFullContent(OneNews news) {
        var builder = new StringBuilder();

        if (StringUtils.isNotEmpty(news.getTitle())) {
            builder.append(StringUtils.trim(news.getTitle())).append(FileUtils.LS);
        }

        if (StringUtils.isNotEmpty(news.getContent())) {
            builder.append(StringUtils.trim(news.getContent())).append(FileUtils.LS);
        }

        if (CollectionUtils.isNotEmpty(news.getStocks())) {
            var stockNameList = news.getStocks().stream().map(it -> it.getName()).collect(Collectors.toList());
            builder.append(StringUtils.joinWith(StringUtils.COMMA, stockNameList.toArray())).append(FileUtils.LS);
        }

        if (CollectionUtils.isNotEmpty(news.getSubjects())) {
            for (var subject : news.getSubjects()) {
                builder.append(StringUtils.trim(subject.getSubjectName())).append(FileUtils.LS);
            }
        }

        return builder.toString();
    }

    public List<StockResource> selectStocks(OneNews news) {
        var content = toFullContent(news);

        var stockList = new ArrayList<StockResource>();
        for (var stockResource : stockResources.getAll()) {
            if (content.contains(stockResource.getName())) {
                stockList.add(stockResource);
            }
        }

        // 重仓扫描
        if (CollectionUtils.isEmpty(stockList)) {

        }

        return stockList;
    }

    public List<IndustryResource> selectIndustry(OneNews news, List<StockResource> stocks) {
        var content = toFullContent(news);

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
