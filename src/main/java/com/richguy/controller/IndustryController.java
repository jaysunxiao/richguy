package com.richguy.controller;

import com.richguy.resource.IndustryResource;
import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.richguy.service.TopWordService;
import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
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
    private TopWordService topWordService;
    @Autowired
    private DatabaseService databaseService;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;


    /**
     * 热点词语次数统计
     */
    @Scheduler(cron = "30 1 0 * * ?")
    public void cronTopHotWord() {
        var topIndustry = industryService.topIndustryToday();
        var topWord = topWordService.topWordToday();

        richGuyService.pushGroupMessage(topIndustry);
        richGuyService.pushGroupMessage(topWord);

        databaseService.richDB.save();
    }

    /**
     * 新概念发现功能
     */
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronNewHotGn() throws IOException, InterruptedException {
        var allIndustry = IndustryUtils.allIndustryList();
        var newIndustrySet = new HashSet<Pair<Integer, String>>();
        for (var industry : allIndustry) {
            var flag = industryResources.getAll()
                    .stream()
                    .noneMatch(it -> it.getName().equals(StringUtils.trim(industry.getValue())));
            if (flag) {
                newIndustrySet.add(industry);
            }

            if (!industryResources.contain(industry.getKey())) {
                newIndustrySet.add(industry);
            }
        }

        if (CollectionUtils.isEmpty(newIndustrySet)) {
            return;
        }


        var builder = new StringBuilder();
        builder.append("\uD83D\uDCA5紧急通知-新概念出现：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);
        for (var industry : newIndustrySet) {
            builder.append(StringUtils.format("{} {} ", industry.getKey(), industry.getValue()));
            builder.append(FileUtils.LS);
            var url = IndustryUtils.industryHtmlUrl(industry.getKey());
            builder.append(url);
            builder.append(FileUtils.LS);
        }

        var database = databaseService.database;
        var newHotGnContent = builder.toString();
        if (newHotGnContent.equals(database.getNewHotGn())) {
            if (database.getNewHotGnTime() > TimeUtils.now()) {
                return;
            }
        } else {
            database.clearNewHotGn();
        }

        database.updateNewHotGn(newHotGnContent);

        richGuyService.pushGroupMessage(newHotGnContent);

        databaseService.richDB.save();
    }

}
