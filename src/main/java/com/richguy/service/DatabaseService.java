package com.richguy.service;

import com.richguy.entity.DatabasePacket;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.net.packet.common.PairLS;
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
    public FileHeapMap<DatabasePacket> richDB;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof AppStartEvent) {
            ProtocolManager.initProtocol(Set.of(DatabasePacket.class, PairLS.class));

            richDB = new FileHeapMap<>("richDb", DatabasePacket.class);

            if (richDB.get(DB_KEY) == null) {
                richDB.put(DB_KEY, DatabasePacket.valueOf());
            }

            database = richDB.get(DB_KEY);
        } else if (event instanceof ContextClosedEvent) {
            richDB.put(DB_KEY, database);
            richDB.save();
        }
    }


}
