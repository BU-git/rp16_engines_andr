package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String email;
    private String name;
    private String password;
    private boolean isOnServer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnServer() {
        return isOnServer;
    }

    public void setOnServer(boolean onServer) {
        isOnServer = onServer;
    }
}