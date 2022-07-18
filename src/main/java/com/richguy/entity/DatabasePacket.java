package com.richguy.entity;

import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 * @version 3.0
 */
public class DatabasePacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    public static final int LIST_SIZE_LIMIT = 1000;

    // 已经推送过的电报ID
    private List<PairLS> telegraphs;

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        packet.telegraphs = new ArrayList<>();
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public void addTelegraph(long id, String telegraph) {
        if (CollectionUtils.isEmpty(telegraphs)) {
            telegraphs = new ArrayList<>();
        }

        if (telegraphs.size() >= LIST_SIZE_LIMIT) {
            telegraphs.remove(0);
        }
        telegraphs.add(PairLS.valueOf(id, telegraph));
    }

    public List<PairLS> getTelegraphs() {
        return telegraphs;
    }

    public void setTelegraphs(List<PairLS> telegraphs) {
        this.telegraphs = telegraphs;
    }
}
