package com.richguy.service;

import com.richguy.model.OneNews;
import com.richguy.resource.IndustryResource;
import com.richguy.util.StockUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author godotg
 * @version 3.0
 */
@Component
public class TopIndustryService {

    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private StockService stockService;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    public void topIndustry(OneNews news) {
        var database = databaseService.database;

        if (database.getTopNewIds().contains(news.getId())) {
            return;
        }

        var stockList = stockService.selectStocks(news);
        var industryList = stockService.selectIndustry(news, stockList);

        for (var industry : industryList) {
            var code = industry.getCode();
            database.addTopIndustry(code);
        }

        database.addTopNewsId(news.getId());
    }


    // -----------------------------------------------------------------------
    public String topIndustryToday() {
        var ignoreIndustry = Set.of(300900, 308594, 301636, 303944, 301490);

        var builder = new StringBuilder();

        builder.append("\uD83C\uDF20电报热点板块次数统计：");

        var database = databaseService.database;

        var topList = database.getTopIndustryMap().entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(30)
                .collect(Collectors.toList());

        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        var count = 1;
        for (var pair : topList) {
            var code = pair.getKey();
            if (ignoreIndustry.contains(code)) {
                continue;
            }

            var industryResource = industryResources.get(code);
            var quote = industryService.bkQuote(code);
            var quoteSimpleRatio = StockUtils.toSimpleRatio(quote);
            var name = industryResource.getName();

            if (database.getOldTopIndustryMap().containsKey(code)) {
                var oldRank = topIndustryRank(code, database.getOldTopIndustryMap());
                var newRank = topIndustryRank(code, database.getTopIndustryMap());
                var changeRank = oldRank - newRank;
                if (changeRank >= 0) {
                    builder.append(StringUtils.format("{}. {}({}) ({}) +{}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                } else {
                    builder.append(StringUtils.format("{}. {}({}) ({}) {}", count, name, quoteSimpleRatio, pair.getValue(), changeRank));
                }
            } else {
                builder.append(StringUtils.format("{}. {}({}) ({}) +0", count, name, quoteSimpleRatio, pair.getValue()));
            }
            builder.append(FileUtils.LS);

            count++;

            if (count == 10 || count == 20 || count == 30) {
                builder.append(FileUtils.LS);
            }
        }

        database.clearTopIndustryMap();
        return builder.toString();
    }


    private int topIndustryRank(int code, Map<Integer, Integer> topIndustryMap) {
        if (!topIndustryMap.containsKey(code)) {
            return 0;
        }

        var topList = topIndustryMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        var rank = 0;
        for (int i = 0; i < topList.size(); i++) {
            var topIndustry = topList.get(i);
            if (topIndustry.getKey() == code) {
                rank = i + 1;
                break;
            }
        }

        return rank;
    }

}
