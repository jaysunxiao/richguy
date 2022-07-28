package com.richguy.service;

import com.richguy.event.QQGroupMessageEvent;
import com.richguy.model.command.CommandEnum;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class QqBotService implements ApplicationListener<AppStartEvent> {

    public static final Logger logger = LoggerFactory.getLogger(QqBotService.class);

    private List<Pair<Bot, AtomicBoolean>> bots = new ArrayList<>();

    @Value("${qq.qqIds}")
    private List<Long> qqIds;

    @Value("${qq.password}")
    private String qqPassword;

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @Value("${weChat.webhook}")
    private String weChatWebhook;

    private LinkedList<String> newsStack = new LinkedList<>();
    private AtomicLong eventTimestamp = new AtomicLong(0);

    @Override
    public void onApplicationEvent(AppStartEvent appStartEvent) {
        for (var qqId : qqIds) {
            Bot bot = BotFactory.INSTANCE.newBot(qqId, qqPassword, new BotConfiguration() {{
                fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
                setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
            }});

            bot.login();

            bots.add(new Pair<>(bot, new AtomicBoolean(false)));

            logger.info("bot启动成功[{}]", bot);
        }

        // 创建监听
        var listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            // 可获取到消息内容等, 详细查阅 `GroupMessageEvent`
            var messageChain = event.getMessage();

            for (var message : messageChain) {
                var content = message.contentToString();
                if (StringUtils.isBlank(content)) {
                    continue;
                }
                if (CommandEnum.allCommands().stream().anyMatch(it -> content.startsWith(it))) {
                    if (TimeUtils.now() - eventTimestamp.get() >= 5 * TimeUtils.MILLIS_PER_SECOND) {
                        eventTimestamp.set(TimeUtils.now());
                        EventBus.asyncSubmit(QQGroupMessageEvent.valueOf(StringUtils.trim(content)));
                    }
                }
            }
        });
    }

    public void pushGroupMessage(String message) {
//        var weChatWebhookRequest = WeChatWebhookRequest.valueOfText(WeChatTextVO.valueOf(message, null));
//        try {
//            HttpUtils.post(weChatWebhook, weChatWebhookRequest);
//        } catch (Exception e) {
//            logger.error("wechat webhook 异常", e);
//        }

        newsStack.addFirst(message);
    }


    private int count = 0;

    @Scheduler(cron = "30 * * * * ?")
    public void cronNewsQQ() {
        var message = newsStack.pollFirst();

        if (StringUtils.isEmpty(message)) {
            return;
        }
        pushGroupMessageNow(message);
    }

    public void pushGroupMessageNow(String message) {
        var pair = bots.get(Math.abs(++count % bots.size()));

        var bot = pair.getKey();
        var refreshFlag = pair.getValue();

        if (refreshFlag.compareAndSet(false, true)) {
            EventBus.asyncExecute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (var pushGroupId : pushGroupIds) {
                            var group = bot.getGroup(pushGroupId);
                            group.sendMessage(message);
                        }
                    } catch (Throwable t) {
                        errorCallback(bot, message);
                        logger.error("机器人未知错误[{}]", t.getMessage());
                    } finally {
                        refreshFlag.lazySet(false);
                    }
                }
            });
        } else {
            errorCallback(bot, message);
        }
    }


    private void errorCallback(Bot bot, String message) {
        var errorMessage = StringUtils.format("机器人[id:{}][name:{}]可能出现异常，无法推送消息", bot.getId(), bot.getNick());
        message = message + FileUtils.LS + FileUtils.LS + errorMessage;
        newsStack.add(message);
        if (newsStack.size() >= 100) {
            newsStack.pollLast();
        }
        logger.error(errorMessage);
    }

}
