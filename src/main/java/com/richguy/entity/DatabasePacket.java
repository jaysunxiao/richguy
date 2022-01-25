package com.richguy.entity;

import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class DatabasePacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    public static final int LIST_SIZE_LIMIT = 1000;

    // 已经推送过的电报ID
    private List<Long> pushTelegraphIds;


    // 热点新闻
    private List<Long> topNewIds;

    private Map<Integer, Integer> topIndustryMap;

    private Map<String, Integer> topWordMap;

    private String newIndustry;
    private long newIndustryTime;
    private long newIndustryCount;

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        packet.pushTelegraphIds = new ArrayList<>();
        packet.topNewIds = new ArrayList<>();
        packet.topIndustryMap = new HashMap<>();
        packet.topWordMap = new HashMap<>();
        packet.newIndustry = StringUtils.EMPTY;
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

    public void addTopNewsId(long id) {
        if (CollectionUtils.isEmpty(topNewIds)) {
            topNewIds = new ArrayList<>();
        }

        if (topNewIds.size() >= LIST_SIZE_LIMIT) {
            topNewIds.remove(0);
        }

        topNewIds.add(id);
    }


    public void addTopIndustry(int code) {
        if (CollectionUtils.isEmpty(topIndustryMap)) {
            topIndustryMap = new HashMap<>();
        }
        var count = topIndustryMap.computeIfAbsent(code, key -> 0);
        topIndustryMap.put(code, count + 1);
    }


    public void addTopWordMap(String word) {
        if (CollectionUtils.isEmpty(topWordMap)) {
            topWordMap = new HashMap<>();
        }
        var count = topWordMap.computeIfAbsent(word, key -> 0);
        topWordMap.put(word, count + 1);
    }

    public void clearIndustry() {
        newIndustry = StringUtils.EMPTY;
        newIndustryTime = 0L;
        newIndustryCount = 0;
    }

    public void updateNewIndustry(String newIndustryContent) {
        newIndustry = newIndustryContent;
        newIndustryTime = TimeUtils.now() + TimeUtils.MILLIS_PER_MINUTE * 3 * (long) Math.pow(2, newIndustryCount++);
    }

    public List<Long> getPushTelegraphIds() {
        return pushTelegraphIds;
    }

    public void setPushTelegraphIds(List<Long> pushTelegraphIds) {
        this.pushTelegraphIds = pushTelegraphIds;
    }

    public List<Long> getTopNewIds() {
        return topNewIds;
    }

    public void setTopNewIds(List<Long> topNewIds) {
        this.topNewIds = topNewIds;
    }

    public Map<Integer, Integer> getTopIndustryMap() {
        return topIndustryMap;
    }

    public void setTopIndustryMap(Map<Integer, Integer> topIndustryMap) {
        this.topIndustryMap = topIndustryMap;
    }

    public Map<String, Integer> getTopWordMap() {
        return topWordMap;
    }

    public void setTopWordMap(Map<String, Integer> topWordMap) {
        this.topWordMap = topWordMap;
    }

    public String getNewIndustry() {
        return newIndustry;
    }

    public void setNewIndustry(String newIndustry) {
        this.newIndustry = newIndustry;
    }

    public long getNewIndustryTime() {
        return newIndustryTime;
    }

    public void setNewIndustryTime(long newIndustryTime) {
        this.newIndustryTime = newIndustryTime;
    }

    public long getNewIndustryCount() {
        return newIndustryCount;
    }

    public void setNewIndustryCount(long newIndustryCount) {
        this.newIndustryCount = newIndustryCount;
    }
}
