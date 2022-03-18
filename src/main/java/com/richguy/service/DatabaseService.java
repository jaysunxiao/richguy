package com.richguy.service;

import com.richguy.entity.DatabaseClockPacket;
import com.richguy.entity.DatabasePacket;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.net.packet.common.*;
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

    // 主要db
    public DatabasePacket database;
    public FileHeapMap<DatabasePacket> db;

    // 闹钟db
    public DatabaseClockPacket databaseClock;
    public FileHeapMap<DatabaseClockPacket> dbClock;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof AppStartEvent) {
            ProtocolManager.initProtocol(Set.of(DatabasePacket.class, DatabaseClockPacket.class
                    , Message.class, PairLS.class, PairLong.class
                    , PairString.class, TripleString.class, TripleLong.class, TripleLSS.class, TripleLLS.class));

            // 初始化主要的db
            db = new FileHeapMap<>("richDb", DatabasePacket.class);
            if (db.get(DB_KEY) == null) {
                db.put(DB_KEY, DatabasePacket.valueOf());
            }
            database = db.get(DB_KEY);

            // 初始化闹钟db
            dbClock = new FileHeapMap<>("richDb", DatabaseClockPacket.class);
            if (dbClock.get(DB_KEY) == null) {
                dbClock.put(DB_KEY, DatabaseClockPacket.valueOf());
            }
            databaseClock = dbClock.get(DB_KEY);
        } else if (event instanceof ContextClosedEvent) {
            save();
        }
    }


    public void save() {
        db.put(DB_KEY, database);
        db.save();

        dbClock.put(DB_KEY, databaseClock);
        dbClock.save();
    }

}
