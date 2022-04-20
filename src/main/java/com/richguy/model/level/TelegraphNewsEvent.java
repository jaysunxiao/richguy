package com.richguy.model.level;

import com.richguy.model.OneNews;
import com.zfoo.event.model.event.IEvent;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class TelegraphNewsEvent implements IEvent {

    private List<OneNews> telegraphNews;

    public static TelegraphNewsEvent valueOf(List<OneNews> telegraphNews) {
        var event = new TelegraphNewsEvent();
        event.telegraphNews = telegraphNews;
        return event;
    }

    public List<OneNews> getTelegraphNews() {
        return telegraphNews;
    }

    public void setTelegraphNews(List<OneNews> telegraphNews) {
        this.telegraphNews = telegraphNews;
    }
}
