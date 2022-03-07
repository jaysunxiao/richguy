package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.service.DatabaseService;
import com.richguy.service.RichGuyService;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.event.model.event.AppStartAfterEvent;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * 逆回购相关处理逻辑
 *
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class ClockController implements ApplicationListener<AppStartAfterEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ClockController.class);


    @Autowired
    private RichGuyService richGuyService;

    @Autowired
    private DatabaseService databaseService;

    @Override
    public void onApplicationEvent(AppStartAfterEvent appStartAfterEvent) {
        // 创建监听
        var listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            var messageChain = event.getMessage(); // 可获取到消息内容等, 详细查阅 `GroupMessageEvent`

            for (var message : messageChain) {
                var content = message.contentToString();
                EventBus.asyncSubmit(QQGroupMessageEvent.valueOf(content));
            }
        });
    }

    // 轮询闹钟定时器
    @Scheduler(cron = "0 * * * * ?")
    public void cronPushQQGroupMessage() {
        var database = databaseService.database;
        var clocks = database.getClocks();

        for (var clock : clocks) {
            if (clock.getKey() > TimeUtils.now()) {
                continue;
            }
            richGuyService.pushGroupMessage(StringUtils.format("\uD83D\uDCA3\uD83D\uDCA3\uD83D\uDCA3{}\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", clock.getValue()));
        }

        database.refreshClock();
        databaseService.richDB.save();
    }

    // 群消息处理
    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        var message = event.getMessage();
        if (StringUtils.isBlank(message)) {
            return;
        }
        message = StringUtils.trim(message).toLowerCase();
        if (!message.startsWith("clock")) {
            return;
        }
        try {
            doClock(message);
        } catch (ParseException e) {
            richGuyService.pushGroupMessage(StringUtils.format("定时器-->{}{}{}-->{}语法格式错误，请重新检查语法格式]", FileUtils.LS, message, FileUtils.LS, FileUtils.LS));
        }
    }

    public void doClock(String message) throws ParseException {
        var splits = message.split(FileUtils.LS);

        if (ArrayUtils.length(splits) != 3) {
            richGuyService.pushGroupMessage(StringUtils.format("定时器语法-->{}{}{}-->{}长度不匹配，请重新检查语法格式", FileUtils.LS, message, FileUtils.LS, FileUtils.LS));
            return;
        }

        var dateStr = splits[1];

        var clockTime = TimeUtils.stringToDate(dateStr).getTime();
        var content = StringUtils.trim(splits[2]);

        if (clockTime <= TimeUtils.now()) {
            richGuyService.pushGroupMessage("定时器时间已经过期");
            return;
        }

        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                var database = databaseService.database;
                database.addClock(clockTime, content);
                databaseService.richDB.save();
                logger.info("定时器设置成功[{}][{}]", dateStr, content);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


}
