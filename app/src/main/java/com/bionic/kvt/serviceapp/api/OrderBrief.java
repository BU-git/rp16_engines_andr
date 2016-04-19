package com.bionic.kvt.serviceapp.api;

import android.support.annotation.Nullable;

import java.util.Date;

public class OrderBrief {

    /**
     * Order number [nummer in XML}
     */
    private long number;

    /**
     * This is time when this order was imported to BO Server.
     * After import it never changed.
     */
    private long importDate;

    /**
     * This is time when this order was changed in BO Server.
     * If order changed in BO this field has to be updated.
     * This field will be used by Android App to check if order has to be updated from server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeDate
     * This field will NOT changed in Android App.
     */
    private long lastServerChangeDate;

    /**
     * This is time when this order was changed in Android.
     * If order changed in Android App this field has to be updated.
     * This field will be used by Android App to check if order need to be updated to server.
     * This field will NOT changed in BO Server.
     * When order is imported to BO Server this time is set to NULL
     */
    private long lastAndroidChangeDate;


    public OrderBrief() {
    }

    public long getImportDate() {
        return importDate;
    }

    public void setImportDate(long importDate) {
        this.importDate = importDate;
    }

    public long getLastServerChangeDate() {
        return lastServerChangeDate;
    }

    public void setLastServerChangeDate(long lastServerChangeDate) {
        this.lastServerChangeDate = lastServerChangeDate;
    }

    public long getLastAndroidChangeDate() {
        return lastAndroidChangeDate;
    }

    public void setLastAndroidChangeDate(long lastAndroidChangeDate) {
        this.lastAndroidChangeDate = lastAndroidChangeDate;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OrderBrief{");
        sb.append("number=").append(number);
        sb.append(", importDate=").append(importDate);
        sb.append(", lastServerChangeDate=").append(lastServerChangeDate);
        sb.append(", lastAndroidChangeDate=").append(lastAndroidChangeDate);
        sb.append('}');
        return sb.toString();
    }
}
