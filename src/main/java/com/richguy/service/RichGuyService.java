package com.richguy.service;

import com.richguy.model.wechat.WeChatTextVO;
import com.richguy.model.wechat.WeChatWebhookRequest;
import com.richguy.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RichGuyService {

    public static final Logger logger = LoggerFactory.getLogger(RichGuyService.class);

    @Value("${weChat.webhook}")
    private String weChatWebhook;

    public void pushGroupMessage(String message) {
        var weChatWebhookRequest = WeChatWebhookRequest.valueOfText(WeChatTextVO.valueOf(message, null));
        try {
            HttpUtils.post(weChatWebhook, weChatWebhookRequest);
        } catch (Exception e) {
            logger.error("wechat webhook 异常", e);
        }
    }

}
