package com.richguy;

import com.zfoo.util.ThreadUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;

@Ignore
@SpringBootApplication(exclude = {
        // 排除MongoDB自动配置
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class,
        MongoReactiveDataAutoConfiguration.class,
        MongoReactiveRepositoriesAutoConfiguration.class,

        TaskExecutionAutoConfiguration.class,
        TaskSchedulingAutoConfiguration.class
})
public class ApplicationTest {

    static {
        var profile = "spring.profiles.active";
        if (System.getProperty(profile) == null) {
            //设置系统变量
            System.setProperty(profile, "dev");
        }
    }

    // unity编辑器运行游戏
    @Test
    public void startApplication() {
        Application.main(new String[0]);
        ThreadUtils.sleep(Long.MAX_VALUE);
    }


    // 设置jvm启动参数
    // -Dmirai.slider.captcha.supported
    @Test
    public void deviceJson() {
        Bot bot = BotFactory.INSTANCE.newBot(123456789, "xxxxxxxxxxx", new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setHeartbeatStrategy(BotConfiguration.HeartbeatStrategy.REGISTER);
        }});

        bot.login();

        System.out.println("bot启动成功[{}]");
    }

}
