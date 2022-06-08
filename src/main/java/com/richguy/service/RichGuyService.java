package com.richguy.service;

import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RichGuyService implements ApplicationListener<AppStartEvent> {

    public static final Logger logger = LoggerFactory.getLogger(RichGuyService.class);

    private Bot bot;

    @Value("${qq.qqId}")
    private long qqId;

    @Value("${qq.password}")
    private String qqPassword;

    @Value("${qq.pushGroupIds}")
    private List<Long> pushGroupIds;

    @Value("${weChat.webhook}")
    private String weChatWebhook;

    private LinkedList<String> newsStack = new LinkedList<>();

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        Bot bot = BotFactory.INSTANCE.newBot(qqId, qqPassword, new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
        }});

        bot.login();

        this.bot = bot;

        logger.info("bot启动成功[{}]", bot);
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

    @Scheduler(cron = "30 * * * * ?")
    public void cronNewsQQ() {
        var message = newsStack.pollFirst();

        if (StringUtils.isEmpty(message)) {
            return;
        }

        for (var pushGroupId : pushGroupIds) {
            var group = bot.getGroup(pushGroupId);
            group.sendMessage(message);
        }
    }

}
