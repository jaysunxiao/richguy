package com.richguy.service;

import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class QqBotService {

    public static final Logger logger = LoggerFactory.getLogger(QqBotService.class);


    private LinkedList<String> newsStack = new LinkedList<>();
    private AtomicLong eventTimestamp = new AtomicLong(0);


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
    }


}
