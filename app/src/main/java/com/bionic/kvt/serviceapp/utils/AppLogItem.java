package com.bionic.kvt.serviceapp.utils;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppLogItem extends RealmObject {
    @PrimaryKey
    private long logItemID;
    private Date dateTime;
    private int level;
    private boolean notify;
    private long orderNumber;
    private String message;

    public AppLogItem() {
    }

    public AppLogItem(long logItemID, Date dateTime, int level, boolean notify, long orderNumber, String message) {
        this.logItemID = logItemID;
        this.dateTime = dateTime;
        this.level = level;
        this.notify = notify;
        this.orderNumber = orderNumber;
        this.message = message;
    }

    public long getLogItemID() {
        return logItemID;
    }

    public void setLogItemID(long logItemID) {
        this.logItemID = logItemID;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
