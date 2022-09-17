package com.richguy.controller;

import com.richguy.resource.HotNoticeResource;
import com.richguy.service.QqBotService;
import com.richguy.util.IndustryUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 * @version 3.0
 */
@Component
public class NewGnThsController {

    private static final Logger logger = LoggerFactory.getLogger(NewGnThsController.class);

    @Autowired
    private QqBotService qqBotService;

    @ResInjection
    private Storage<Integer, HotNoticeResource> hotNoticeResources;

    private Set<String> thsIndustries = new HashSet<>();

    @Scheduler(cron = "0 0/3 * * * ?")
    public void cronNewThsGn() throws IOException, InterruptedException {
        var list = IndustryUtils.allIndustryList();

        // 第一次先初始化
        if (CollectionUtils.isEmpty(thsIndustries)) {
            for (var industry : list) {
                thsIndustries.add(StringUtils.trim(industry.getValue()));
            }
            return;
        }

        for (var industry : list) {
            var gn = StringUtils.trim(industry.getValue());

            if (thsIndustries.contains(gn)) {
                continue;
            }

            var diffTime = TimeUtils.getZeroTimeOfDay(TimeUtils.now()) + 7 * TimeUtils.MILLIS_PER_HOUR - TimeUtils.now();

            var date = TimeUtils.dateFormatForDayString(TimeUtils.now());
            notifyNewGn(industry.getKey(), industry.getValue(), date);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 5 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 30 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 60 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 2 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 4 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 8 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 16 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 1 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 2 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 3 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 4 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 5 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 6 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry.getKey(), industry.getValue(), date), 7 * TimeUtils.MILLIS_PER_DAY + diffTime, TimeUnit.MILLISECONDS);

            thsIndustries.add(gn);
        }
    }


    public void notifyNewGn(int industryId, String name, String date) {
        var builder = new StringBuilder();
        builder.append("\uD83D\uDCA5紧急通知-同HuaShun新概念：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("{} {} ", industryId, name));
        builder.append(FileUtils.LS);
        var url = IndustryUtils.industryHtmlUrl(industryId);
        builder.append(url);
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("出现时间[{}]", date)).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(RandomUtils.randomEle(new ArrayList<>(hotNoticeResources.getIndex("type", 1)))).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(RandomUtils.randomString(24)).append(FileUtils.LS).append(FileUtils.LS);

        qqBotService.pushGroupMessage(builder.toString());
    }

}
