package com.richguy.entity;

import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;

import java.util.*;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class DatabasePacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    public static final int LIST_SIZE_LIMIT = 1000;

    // 已经推送过的电报ID
    private List<Long> pushTelegraphIds;


    // 热点板块变化
    private Map<Integer, Integer> oldTopIndustryMap;
    private Map<Integer, Integer> topIndustryMap;

    // 热点词语
    private Map<String, Integer> oldTopWordMap;
    private Map<String, Integer> topWordMap;

    // 热点新闻
    private List<Long> topNewIds;


    private String newHotGn;
    private long newHotGnTime;
    private long newHotGnCount;

    private List<PairLS> clocks;

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        packet.pushTelegraphIds = new ArrayList<>();
        packet.topNewIds = new ArrayList<>();
        packet.oldTopIndustryMap = new HashMap<>();
        packet.topIndustryMap = new HashMap<>();
        packet.oldTopWordMap = new HashMap<>();
        packet.topWordMap = new HashMap<>();
        packet.newHotGn = StringUtils.EMPTY;
        packet.clocks = new ArrayList<>();
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

    public void clearTopIndustryMap() {
        oldTopIndustryMap = topIndustryMap;
        topIndustryMap = Collections.emptyMap();
    }

    public void clearTopWorldMap() {
        oldTopWordMap = topWordMap;
        topWordMap = Collections.emptyMap();
    }

    public void clearNewHotGn() {
        newHotGn = StringUtils.EMPTY;
        newHotGnTime = 0L;
        newHotGnCount = 0;
    }

    public void updateNewHotGn(String newHotGnContent) {
        newHotGn = newHotGnContent;
        newHotGnTime = TimeUtils.now() + TimeUtils.MILLIS_PER_MINUTE * 3 * (long) Math.pow(2, newHotGnCount++);
    }

    public void addClock(long clockTime, String content) {
        if (CollectionUtils.isEmpty(clocks)) {
            clocks = new ArrayList<>();
        }

        clocks.add(PairLS.valueOf(clockTime, content));
    }

    public void refreshClock() {
        if (CollectionUtils.isEmpty(clocks)) {
            return;
        }
        clocks.removeIf(it -> it.getKey() <= TimeUtils.now());
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

    public String getNewHotGn() {
        return newHotGn;
    }

    public void setNewHotGn(String newHotGn) {
        this.newHotGn = newHotGn;
    }

    public long getNewHotGnTime() {
        return newHotGnTime;
    }

    public void setNewHotGnTime(long newHotGnTime) {
        this.newHotGnTime = newHotGnTime;
    }

    public long getNewHotGnCount() {
        return newHotGnCount;
    }

    public void setNewHotGnCount(long newHotGnCount) {
        this.newHotGnCount = newHotGnCount;
    }

    public Map<Integer, Integer> getOldTopIndustryMap() {
        return oldTopIndustryMap;
    }

    public void setOldTopIndustryMap(Map<Integer, Integer> oldTopIndustryMap) {
        this.oldTopIndustryMap = oldTopIndustryMap;
    }

    public Map<String, Integer> getOldTopWordMap() {
        return oldTopWordMap;
    }

    public void setOldTopWordMap(Map<String, Integer> oldTopWordMap) {
        this.oldTopWordMap = oldTopWordMap;
    }

    public List<PairLS> getClocks() {
        return clocks;
    }

    public void setClocks(List<PairLS> clocks) {
        this.clocks = clocks;
    }
}
