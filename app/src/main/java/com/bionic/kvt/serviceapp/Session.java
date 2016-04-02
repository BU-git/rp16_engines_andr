package com.bionic.kvt.serviceapp;

import android.app.Application;

/**
 * Implements session handling
 */
public class Session extends Application {
    private String engineerName;
    private String engineerId;
    private String orderNumber;

    public void clearSession() {
        engineerName = null;
        engineerId = null;
        orderNumber = null;
    }

    public void clearOrderNumber() {
        orderNumber = null;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }
}
