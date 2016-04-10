package com.bionic.kvt.serviceapp.models;

import java.sql.Timestamp;

public class OrderBrief {

    /**
     * Order number [nummer in XML}
     * NotNull
     */
    private long number;

    /**
     * This is time when this order was imported to BO Server.
     * After import it never changed.
     * NotNull
     */
    private Timestamp importTimestamp;

    /**
     * This is time when this order was changed in BO Server.
     * If order changed in BO this field has to be updated.
     * This field will be used by Android App to check if order has to be updated from server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeTimestamp
     * This field will NOT changed in Android App.
     * NotNull
     */
    private Timestamp lastServerChangeTimestamp;

    /**
     * This is time when this order was changed in Android.
     * If order changed in Android App this field has to be updated.
     * This field will be used by Android App to check if order need to be updated to server.
     * This field will NOT changed in BO Server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeTimestamp
     * NotNull
     */
    private Timestamp lastAndroidChangeTimestamp;


    public OrderBrief() {
    }

    public Timestamp getLastAndroidChangeTimestamp() {
        return lastAndroidChangeTimestamp;
    }

    public void setLastAndroidChangeTimestamp(Timestamp lastAndroidChangeTimestamp) {
        this.lastAndroidChangeTimestamp = lastAndroidChangeTimestamp;
    }

    public Timestamp getImportTimestamp() {
        return importTimestamp;
    }

    public void setImportTimestamp(Timestamp importTimestamp) {
        this.importTimestamp = importTimestamp;
    }

    public Timestamp getLastServerChangeTimestamp() {
        return lastServerChangeTimestamp;
    }

    public void setLastServerChangeTimestamp(Timestamp lastServerChangeTimestamp) {
        this.lastServerChangeTimestamp = lastServerChangeTimestamp;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
