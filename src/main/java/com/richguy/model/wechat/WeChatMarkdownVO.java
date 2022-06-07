package com.richguy.model.wechat;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-06 16:37
 */
public class WeChatMarkdownVO {

    private String content;

    public static WeChatMarkdownVO valueOf(String content) {
        var vo = new WeChatMarkdownVO();
        vo.content = content.length() >= 3800 ? content.substring(0, 3800) : content;
        return vo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
