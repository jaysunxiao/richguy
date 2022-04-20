package com.richguy.controller;

import com.richguy.model.level.NewsLevelEnum;
import com.richguy.model.level.NewsPushEvent;
import com.richguy.model.level.TelegraphNewsEvent;
import com.richguy.service.RichGuyService;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class StaticsController {

    @Autowired
    private RichGuyService richGuyService;

    private Map<NewsLevelEnum, Integer> newsLevelMap = new EnumMap<NewsLevelEnum, Integer>(NewsLevelEnum.class);
    private Set<Long> newsIds = new HashSet<>();

    @EventReceiver
    public void onNewsPushEvent(NewsPushEvent event) {
        var newsLevel = event.getNewsLevel();
        var count = newsLevelMap.computeIfAbsent(newsLevel, it -> 0);
        newsLevelMap.put(newsLevel, count + 1);
    }

    @EventReceiver
    public void onTelegraphNewsEvent(TelegraphNewsEvent event) {
        var telegraphNews = event.getTelegraphNews();
        if (CollectionUtils.isEmpty(telegraphNews)) {
            return;
        }
        telegraphNews.stream().forEach(it -> newsIds.add(it.getId()));
    }


    @Scheduler(cron = "0 0 0 * * ?")
    public void cronStatics() {
        var s = newsLevelMap.computeIfAbsent(NewsLevelEnum.S, it -> 0);
        var a = newsLevelMap.computeIfAbsent(NewsLevelEnum.A, it -> 0);
        var b = newsLevelMap.computeIfAbsent(NewsLevelEnum.B, it -> 0);
        var c = newsLevelMap.computeIfAbsent(NewsLevelEnum.C, it -> 0);
        var d = newsLevelMap.computeIfAbsent(NewsLevelEnum.D, it -> 0);
        var count = s + a + b + c + d;
        var newsMessage = StringUtils.format("S[{}] A[{}] B[{}] C[{}] D[{}] = {}", s, a, b, c, d, count);

        var builder = new StringBuilder();
        builder.append("消息等级统计：");
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);
        builder.append(newsMessage);
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);
        builder.append(StringUtils.format("total news {}", newsIds.size()));

        richGuyService.pushGroupMessage(builder.toString());

        newsIds.clear();
        newsLevelMap.clear();
    }


}
