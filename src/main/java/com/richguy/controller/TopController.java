package com.richguy.controller;

import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.richguy.service.TopIndustryService;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 * @version 3.0
 */
@Component
public class TopController {

    @Autowired
    private IndustryService industryService;
    @Autowired
    private RichGuyService richGuyService;
    @Autowired
    private TopIndustryService topIndustryService;
    @Autowired
    private DatabaseService databaseService;


    /**
     * 热点词语次数统计
     */
    @Scheduler(cron = "30 1 0 * * ?")
    public void cronTopHotWord() {
        var topIndustry = topIndustryService.topIndustryToday();
        richGuyService.pushGroupMessage(topIndustry);

//        var topWord = topNewsService.topWordToday();
//        richGuyService.pushGroupMessage(topWord);

        databaseService.database.clearTopWordMap();
        databaseService.save();
    }

}
