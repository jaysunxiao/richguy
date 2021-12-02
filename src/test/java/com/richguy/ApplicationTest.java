package com.richguy;

import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.util.ThreadUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
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
        var context = SpringApplication.run(ApplicationTest.class);

        context.registerShutdownHook();
        context.publishEvent(new AppStartEvent(context));

        ThreadUtils.sleep(Long.MAX_VALUE);
    }

}
