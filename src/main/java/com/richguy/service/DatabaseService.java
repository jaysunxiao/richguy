package com.richguy.service;

import com.richguy.entity.DatabasePacket;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.orm.lpmap.FileHeapMap;
import com.zfoo.protocol.ProtocolManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class DatabaseService implements ApplicationListener<ApplicationContextEvent> {

    public static final long DB_KEY = 1;
    public DatabasePacket database;
    private FileHeapMap<DatabasePacket> dbMap;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof AppStartEvent) {
            ProtocolManager.initProtocol(Set.of(DatabasePacket.class));

            dbMap = new FileHeapMap<>("richDb", DatabasePacket.class);

            if (dbMap.get(DB_KEY) == null) {
                dbMap.put(DB_KEY, DatabasePacket.valueOf());
            }

            database = dbMap.get(DB_KEY);
        } else if (event instanceof ContextClosedEvent) {
            dbMap.put(DB_KEY, database);
            dbMap.save();
        }
    }


}
