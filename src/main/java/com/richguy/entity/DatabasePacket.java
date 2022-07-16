package com.richguy.entity;

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
    private List<Long> pushTelegraphIds;

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        packet.pushTelegraphIds = new ArrayList<>();
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public void addPushTelegraphId(long id) {
        if (CollectionUtils.isEmpty(pushTelegraphIds)) {
            pushTelegraphIds = new ArrayList<>();
        }

        if (pushTelegraphIds.size() >= LIST_SIZE_LIMIT) {
            pushTelegraphIds.remove(0);
        }

        pushTelegraphIds.add(id);
    }

    public List<Long> getPushTelegraphIds() {
        return pushTelegraphIds;
    }

    public void setPushTelegraphIds(List<Long> pushTelegraphIds) {
        this.pushTelegraphIds = pushTelegraphIds;
    }
}
