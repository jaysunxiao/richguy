package com.richguy.entity;

import com.zfoo.net.packet.common.TripleLLS;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author godotg
 * @version 3.0
 */
public class DatabasePacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    public static final int LIST_SIZE_LIMIT = 1000;

    // 已经推送过的电报ID
    private List<TripleLLS> telegraphs = new CopyOnWriteArrayList<>();

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public void addTelegraph(long id, long time, String telegraph) {
        if (CollectionUtils.isEmpty(telegraphs)) {
            telegraphs = new CopyOnWriteArrayList<>();
        }

        if (telegraphs.size() >= LIST_SIZE_LIMIT) {
            telegraphs.remove(0);
        }
        telegraphs.add(TripleLLS.valueOf(id, time, telegraph));
    }

    public List<TripleLLS> getTelegraphs() {
        return telegraphs;
    }

    public void setTelegraphs(List<TripleLLS> telegraphs) {
        this.telegraphs = telegraphs;
    }
}
