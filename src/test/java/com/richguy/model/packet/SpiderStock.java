package com.richguy.model.packet;

import com.zfoo.protocol.IPacket;

/**
 * @author godotg
 * @version 3.0
 */
public class SpiderStock implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    private int count;

    public static SpiderStock valueOf(int count) {
        var packet = new SpiderStock();
        packet.count = count;
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
