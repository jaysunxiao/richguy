package com.richguy.packet;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class SpiderPacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    private int count;

    public static SpiderPacket valueOf(int count) {
        var packet = new SpiderPacket();
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
