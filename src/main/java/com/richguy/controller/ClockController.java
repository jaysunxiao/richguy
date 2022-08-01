package com.richguy.controller;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.model.command.CommandEnum;
import com.richguy.service.DatabaseService;
import com.richguy.service.QqBotService;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * 群闹铃相关处理逻辑
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class ClockController {

    private static final Logger logger = LoggerFactory.getLogger(ClockController.class);


    @Autowired
    private QqBotService qqBotService;

    @Autowired
    private DatabaseService databaseService;

    // 群消息处理
    @EventReceiver
    public void onQQGroupMessageEvent(QQGroupMessageEvent event) {
        var message = event.getMessage();
        if (!message.startsWith(CommandEnum.clock.getCommand())) {
            return;
        }

        try {
            doClock(message);
        } catch (Exception e) {
            qqBotService.pushGroupMessageNow(errorMessage());
        }
    }

    // 轮询闹钟定时器
    @Scheduler(cron = "0 * * * * ?")
    public void cronPushQQGroupMessage() {
        var databaseClock = databaseService.databaseClock;
        var clocks = databaseClock.getClocks();

        for (var clock : clocks) {
            if (clock.getKey() > TimeUtils.now()) {
                continue;
            }
            qqBotService.pushGroupMessageNow(StringUtils.format("\uD83D\uDCA3\uD83D\uDCA3\uD83D\uDCA3{}{}{}\uD83C\uDF6D\uD83C\uDF6D\uD83C\uDF6D"
                    , FileUtils.LS, clock.getValue(), FileUtils.LS));
        }

        // 如果有移除的闹钟，则重新写入数据库
        if (databaseClock.refreshClock()) {
            databaseService.save();
            logger.info("闹钟剩余[{}]", clocks.size());
        }
    }

    public void doClock(String message) throws ParseException {
        var splits = message.split(StringUtils.SPACE_REGEX);

        if (ArrayUtils.length(splits) != 4) {
            qqBotService.pushGroupMessageNow(errorMessage());
            return;
        }

        var dateStr = StringUtils.format("{} {}", splits[1], splits[2]);

        var clockTime = TimeUtils.stringToDate(dateStr).getTime();
        var content = StringUtils.trim(splits[3]);

        if (clockTime <= TimeUtils.now()) {
            qqBotService.pushGroupMessageNow("\uD83C\uDE32定时器时间已经过期");
            return;
        }

        SchedulerBus.schedule(new Runnable() {
            @Override
            public void run() {
                qqBotService.pushGroupMessageNow(StringUtils.format("\uD83D\uDE80定时器设置成功：{}时间->{}{}[{}]", FileUtils.LS, dateStr, FileUtils.LS, content));

                var databaseClock = databaseService.databaseClock;
                databaseClock.addClock(clockTime, content);
                databaseService.save();

                logger.info("定时器设置成功[{}][{}]", dateStr, content);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }


    public String errorMessage() {
        var dateStr = TimeUtils.dateFormatForDayTimeString(TimeUtils.getZeroTimeOfDay(TimeUtils.now()) + TimeUtils.MILLIS_PER_HOUR * 9);
        var errorMessage = StringUtils.format("\uD83C\uDE32请输入定时器正确的语法格式：{}------------------{}clock{}{}{}这里是要提示的内容{}------------------"
                , FileUtils.LS, FileUtils.LS, FileUtils.LS, dateStr, FileUtils.LS, FileUtils.LS);
        return errorMessage;
    }

}
