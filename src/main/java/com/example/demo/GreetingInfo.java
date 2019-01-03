package com.example.demo;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;

public class GreetingInfo {

    private final long id;
    private final String content;
    private final Timestamp timestamp;
    private final String ip;

    private static final String INCREMENT = "increment";
    private static final String USER_IP = "user_ip";
    private static final String TIMESTAMP = "timestamp";
    private static final String CONTENT = "content";

    public GreetingInfo(long id, String content, Timestamp timestamp, String ip) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getIp() {
        return ip;
    }

    static GreetingInfo fromEntity(Entity entity) {
        return new GreetingInfo(
                getLong(entity, INCREMENT),
                getString(entity, CONTENT),
                getTimestamp(entity, TIMESTAMP),
                getString(entity, USER_IP)
        );
    }


    private static long getLong(Entity entity, String name) {
        if (entity.getNames().contains(name)) {
            return entity.getLong(name);
        }
        return 0;
    }

    private static String getString(Entity entity, String name) {
        if (entity.getNames().contains(name)) {
            return entity.getString(name);
        }
        return "";
    }

    private static Timestamp getTimestamp(Entity entity, String name) {
        if (entity.getNames().contains(name)) {
            return entity.getTimestamp(name);
        }
        return Timestamp.MIN_VALUE;
    }
}