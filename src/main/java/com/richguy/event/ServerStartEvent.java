package com.richguy.event;

import com.zfoo.event.model.event.IEvent;

/**
 * @author godotg
 * @version 3.0
 */
public class ServerStartEvent implements IEvent {

    public static ServerStartEvent valueOf() {
        var event = new ServerStartEvent();
        return event;
    }

    @Override
    public int threadId() {
        return 0;
    }

}
