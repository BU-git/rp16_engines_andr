package com.bionic.kvt.serviceapp;

/**
 Implements session handling as a field of a singleton class
 */
public class Session {
    private static Session instance = null;
    private String mUser;

    private Session(){}

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public String getmUser() {
        return mUser;
    }

    public void setmUser(String mUser) {
        this.mUser = mUser;
    }
}
