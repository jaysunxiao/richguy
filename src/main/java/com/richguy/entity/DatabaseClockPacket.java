package com.richguy.entity;

import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author godotg
 * @version 3.0
 */
public class DatabaseClockPacket implements IPacket {

    public static final transient short PROTOCOL_ID = 2;

    private List<PairLS> clocks = new CopyOnWriteArrayList<>();

    public static DatabaseClockPacket valueOf() {
        var packet = new DatabaseClockPacket();
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public void addClock(long clockTime, String content) {
        if (CollectionUtils.isEmpty(clocks)) {
            clocks = new ArrayList<>();
        }

        clocks.add(PairLS.valueOf(clockTime, content));
    }

    public boolean refreshClock() {
        if (CollectionUtils.isEmpty(clocks)) {
            return false;
        }
        return clocks.removeIf(it -> it.getKey() <= TimeUtils.now());
    }


    public List<PairLS> getClocks() {
        return clocks;
    }

    public void setClocks(List<PairLS> clocks) {
        this.clocks = clocks;
    }
}
