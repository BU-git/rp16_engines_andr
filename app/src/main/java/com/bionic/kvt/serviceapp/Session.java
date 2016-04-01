package com.bionic.kvt.serviceapp;

import android.app.Application;

/**
 * Implements session handling
 */
public class Session extends Application {
    private String engineerName;
    private String engineerId;

    public void clearSession() {
        engineerName = null;
        engineerId = null;
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
