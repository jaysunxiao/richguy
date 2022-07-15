package com.richguy.model.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author godotg
 * @version 1.0
 * @since 2020-08-05 17:29
 */
public class WeChatTextVO {

    private String content;

    @JsonProperty("mentioned_list")
    private List<String> mentionedList;

    public static WeChatTextVO valueOf(String content, List<String> mentionedList) {
        var vo = new WeChatTextVO();
        vo.content = content;
        vo.mentionedList = mentionedList;
        return vo;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMentionedList() {
        return mentionedList;
    }

    public void setMentionedList(List<String> mentionedList) {
        this.mentionedList = mentionedList;
    }
}
