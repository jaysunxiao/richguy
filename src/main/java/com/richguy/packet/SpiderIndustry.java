package com.richguy.packet;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class SpiderIndustry implements IPacket {

    public static final transient short PROTOCOL_ID = 2;

    private int index;
    private int count;

    public static SpiderIndustry valueOf(int index, int count) {
        var packet = new SpiderIndustry();
        packet.index = index;
        packet.count = count;
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
