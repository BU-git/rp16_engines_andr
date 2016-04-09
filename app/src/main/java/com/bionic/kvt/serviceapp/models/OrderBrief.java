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
     * This field will be used by Android App to check if order has to updated from server.
     * This field will NOT changed in Android App.
     * NotNull
     */
    private Timestamp lastServerChangeTimestamp;

    /**
     * This is time when this order was changed in Android.
     * If order changed in Android App this field has to be updated.
     * This field will be used by Android App to check if order need to be updated to server.
     * This field will NOT changed in BO Server.
     * When order is imported to BO Server this time is set the same as lastServerChangeTimestamp
     * NotNull
     */
    private Timestamp lastAndroidChangeTimestamp;
}
