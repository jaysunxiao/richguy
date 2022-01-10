package com.richguy.controller;

import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @Scheduler(cron = "30 1 0 * * ?")
    public void cronPushQQ() {
        var content = industryService.topIndustryToday();

        var bot = richGuyService.bot;

        for (var pushGroupId : pushGroupIds) {
            var group = bot.getGroup(pushGroupId);
            group.sendMessage(content);
        }
    }

}
