package com.richguy.event;

import com.zfoo.event.model.event.IEvent;

/**
 * @author godotg
 * @version 3.0
 */
public class QQGroupMessageEvent implements IEvent {

    private String message;

    public static QQGroupMessageEvent valueOf(String message) {
        var event = new QQGroupMessageEvent();
        event.message = message;
        return event;
    }

    @Override
    public int threadId() {
        return 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
