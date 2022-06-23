package com.richguy.controller;

import com.richguy.resource.IndustryResource;
import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class IndustryController {

    private static final Logger logger = LoggerFactory.getLogger(IndustryController.class);

    @Autowired
    private IndustryService industryService;
    @Autowired
    private RichGuyService richGuyService;
    @Autowired
    private DatabaseService databaseService;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    /**
     * 新概念发现功能
     */
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronNewHotGn() throws IOException, InterruptedException {
        var allIndustry = IndustryUtils.allIndustryList();
        var newIndustrySet = new HashSet<Pair<Integer, String>>();

        var database = databaseService.database;

        // 判断是不是新概念，即判断id又判断名称
        for (var industryPair : allIndustry) {
            var flagResource = industryResources.getAll()
                    .stream()
                    .noneMatch(it -> it.getName().equals(StringUtils.trim(industryPair.getValue())));

            var flagDatabase = database.getNewHotGns()
                    .stream()
                    .noneMatch(it -> it.getRight().equals(StringUtils.trim(industryPair.getValue())));

            if (flagResource && flagDatabase) {
                newIndustrySet.add(industryPair);
            }

            var flagDatabaseKey = database.getNewHotGns()
                    .stream()
                    .noneMatch(it -> it.getLeft() == industryPair.getKey());

            if (!industryResources.contain(industryPair.getKey()) && flagDatabaseKey) {
                newIndustrySet.add(industryPair);
            }
        }

        if (CollectionUtils.isEmpty(newIndustrySet)) {
            return;
        }

        for (var industryPair : newIndustrySet) {
            database.addNewGn(industryPair.getKey(), industryPair.getValue());
        }
        databaseService.save();
    }

    @Scheduler(cron = "0 0/3 * * * ?")
    public void cronPushGn() throws IOException, InterruptedException {
        for (var newHotGn : databaseService.database.getNewHotGns()) {
            var count = newHotGn.getMiddle();
            if (count <= 8) {
                var builder = new StringBuilder();
                builder.append("\uD83D\uDCA5紧急通知-同花顺新概念：");
                builder.append(FileUtils.LS);
                builder.append(FileUtils.LS);

                builder.append(StringUtils.format("{} {} ", newHotGn.getLeft(), newHotGn.getRight()));
                builder.append(FileUtils.LS);
                var url = IndustryUtils.industryHtmlUrl((int) newHotGn.getLeft());
                builder.append(url);
                builder.append(FileUtils.LS);

                builder.append(RandomUtils.randomString(16));
                builder.append(FileUtils.LS);

                richGuyService.pushGroupMessage(builder.toString());

                newHotGn.setMiddle(count + 1);
                databaseService.save();
            }
        }
    }

}
