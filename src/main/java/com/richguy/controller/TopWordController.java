package com.richguy.controller;

import com.richguy.service.DatabaseService;
import com.richguy.service.IndustryService;
import com.richguy.service.RichGuyService;
import com.richguy.service.TopWordService;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class TopWordController {

    @Autowired
    private IndustryService industryService;
    @Autowired
    private RichGuyService richGuyService;
    @Autowired
    private TopWordService topWordService;
    @Autowired
    private DatabaseService databaseService;


    /**
     * 热点词语次数统计
     */
    @Scheduler(cron = "30 1 0 * * ?")
    public void cronTopHotWord() {
        var topIndustry = topWordService.topIndustryToday();
        var topWord = topWordService.topWordToday();

        richGuyService.pushGroupMessage(topIndustry);
        richGuyService.pushGroupMessage(topWord);

        databaseService.save();
    }

}
