package com.bionic.kvt.serviceapp;

import android.app.Application;

/**
 Implements session handling
 */
public class Session extends Application{
    private static Session instance = null;
    private String mUser;

    public String getmUser() {
        return mUser;
    }

    public void setmUser(String mUser) {
        this.mUser = mUser;
    }
}
