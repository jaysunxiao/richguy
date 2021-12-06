package com.richguy.service;

import com.zfoo.event.model.event.AppStartEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RichGuyService implements ApplicationListener<AppStartEvent> {

    public Bot bot;

    @Value("${qq.qqId}")
    private long qqId;

    @Value("${qq.password}")
    private String qqPassword;

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        Bot bot = BotFactory.INSTANCE.newBot(qqId, qqPassword, new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
        }});

        bot.login();

        this.bot = bot;
    }

}
