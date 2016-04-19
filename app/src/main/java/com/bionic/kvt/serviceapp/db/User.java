package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class User extends RealmObject {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("email='").append(email).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", isOnServer=").append(isOnServer);
        sb.append('}');
        return sb.toString();
    }
}