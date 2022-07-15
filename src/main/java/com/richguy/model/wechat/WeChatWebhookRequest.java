package com.richguy.model.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author godotg
 * @version 1.0
 * @since 2020-08-05 17:28
 */
public class WeChatWebhookRequest {

    @JsonProperty("msgtype")
    private String msgType;

    private WeChatTextVO text;

    private WeChatMarkdownVO markdown;

    public static WeChatWebhookRequest valueOfText(WeChatTextVO text) {
        var request = new WeChatWebhookRequest();
        request.msgType = "text";
        request.text = text;
        return request;
    }

    public static WeChatWebhookRequest valueOfMarkdown(WeChatMarkdownVO markdown) {
        var request = new WeChatWebhookRequest();
        request.msgType = "markdown";
        request.markdown = markdown;
        return request;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public WeChatTextVO getText() {
        return text;
    }

    public void setText(WeChatTextVO text) {
        this.text = text;
    }

    public WeChatMarkdownVO getMarkdown() {
        return markdown;
    }

    public void setMarkdown(WeChatMarkdownVO markdown) {
        this.markdown = markdown;
    }
}
