package com.richguy.model.level;

import com.zfoo.event.model.event.IEvent;

/**
 * @author godotg
 * @version 3.0
 */
public class NewsPushEvent implements IEvent {

    private NewsLevelEnum newsLevel;

    public static NewsPushEvent valueOf(NewsLevelEnum newsLevel) {
        var event = new NewsPushEvent();
        event.newsLevel = newsLevel;
        return event;
    }

    public NewsLevelEnum getNewsLevel() {
        return newsLevel;
    }

    public void setNewsLevel(NewsLevelEnum newsLevel) {
        this.newsLevel = newsLevel;
    }
}
