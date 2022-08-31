package com.richguy.controller;

import com.richguy.model.east.EastMoneyIndustry;
import com.richguy.model.east.EastMoneyResult;
import com.richguy.resource.GaiNianResource;
import com.richguy.service.QqBotService;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 * @version 3.0
 */
@Component
public class NewGnEastMoneyController {

    private Set<String> eastMoneyIndustries = new HashSet<>();

    @Autowired
    private QqBotService qqBotService;

    @ResInjection
    private Storage<String, GaiNianResource> gnResources;

    @Scheduler(cron = "0 0/3 * * * ?")
    public void cronNewEastMoneyGn() throws IOException, InterruptedException {
        var list = getEastMoneyIndustries();

        // 第一次先初始化
        if (CollectionUtils.isEmpty(eastMoneyIndustries)) {
            for (var industry : list) {
                eastMoneyIndustries.add(StringUtils.trim(industry.getGn()));
            }
            return;
        }

        for (var industry : list) {
            var gn = StringUtils.trim(industry.getGn());

            if (eastMoneyIndustries.contains(gn)) {
                continue;
            }

            var date = TimeUtils.dateFormatForDayString(TimeUtils.now());
            notifyNewGn(industry, date);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 5 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 30 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 60 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 2 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 6 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 12 * TimeUtils.MILLIS_PER_HOUR, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 1 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 2 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 3 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 4 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 5 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 6 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry, date), 7 * TimeUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);

            eastMoneyIndustries.add(gn);
        }
    }

    public void notifyNewGn(EastMoneyIndustry industry, String date) {
        var gn = StringUtils.trim(industry.getGn());

        var builder = new StringBuilder();
        builder.append("\uD83D\uDCA5紧急广播-东方CaiFu新概念：");

        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("{} {} ", gn, industry.getF12()));
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        var url = StringUtils.format("http://quote.eastmoney.com/bk/{}.{}.html", industry.getF13(), industry.getF12());
        builder.append(url).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(StringUtils.format("出现时间[{}]", date)).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(RandomUtils.randomEle(new ArrayList<>(gnResources.getAll()))).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(date).append(FileUtils.LS).append(FileUtils.LS);

        builder.append(RandomUtils.randomString(16)).append(FileUtils.LS);

        qqBotService.pushGroupMessage(builder.toString());
    }


    public EastMoneyResult requestForEastMoneyResult() throws IOException, InterruptedException {
        var url = "https://53.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=1000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50";
        var responseBody = HttpUtils.get(url);

        var result = JsonUtils.string2Object(responseBody, EastMoneyResult.class);
        return result;
    }

    public List<EastMoneyIndustry> getEastMoneyIndustries() throws IOException, InterruptedException {
        var response = requestForEastMoneyResult();
        var data = response.getData();
        if (data == null) {
            return Collections.emptyList();
        }

        var diff = data.getDiff();
        if (CollectionUtils.isEmpty(diff)) {
            return Collections.emptyList();
        }

        return diff;
    }

}
