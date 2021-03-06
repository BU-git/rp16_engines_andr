package com.bionic.kvt.serviceapp.db.Components;


import io.realm.RealmObject;

public class Employee extends RealmObject {
    private int number; //nummer
    private String name; //Naam
    private String email; //Email

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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
}
