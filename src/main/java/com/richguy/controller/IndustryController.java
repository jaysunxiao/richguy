package com.richguy.controller;

import com.richguy.resource.IndustryResource;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

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

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @ResInjection
    private Storage<Integer, IndustryResource> industryResources;

    private String newIndustry = StringUtils.EMPTY;
    private long newIndustryTime = 0L;
    private long newIndustryCount = 0;

    @Scheduler(cron = "30 1 0 * * ?")
    public void cronPushTop() {
        var topIndustry = industryService.topIndustryToday();
        var topWord = topWordService.topWordToday();

        var bot = richGuyService.bot;

        for (var pushGroupId : pushGroupIds) {
            var group = bot.getGroup(pushGroupId);
            group.sendMessage(topIndustry);
        }

        for (var pushGroupId : pushGroupIds) {
            var group = bot.getGroup(pushGroupId);
            group.sendMessage(topWord);
        }
    }

    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronNewIndustry() throws IOException, InterruptedException {
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

        var newIndustryContent = builder.toString();
        if (newIndustryContent.equals(newIndustry)) {
            if (newIndustryTime > TimeUtils.now()) {
                return;
            }
        } else {
            newIndustry = StringUtils.EMPTY;
            newIndustryTime = 0L;
            newIndustryCount = 0;
        }

        newIndustry = newIndustryContent;
        newIndustryTime = TimeUtils.now() + TimeUtils.MILLIS_PER_MINUTE * 3 * (long) Math.pow(2, newIndustryCount++);

        var bot = richGuyService.bot;


        for (var pushGroupId : pushGroupIds) {
            var group = bot.getGroup(pushGroupId);
            group.sendMessage(newIndustryContent);
        }
    }

}
