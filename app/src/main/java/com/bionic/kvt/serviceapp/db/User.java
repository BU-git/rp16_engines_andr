package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class User extends RealmObject {
    private String email;
    private String name;
    private String passwordHash;
    private String salt;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("email='").append(email).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", passwordHash='").append(passwordHash).append('\'');
        sb.append(", salt='").append(salt).append('\'');
        sb.append('}');
        return sb.toString();
    }
}