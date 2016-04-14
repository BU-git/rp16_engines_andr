package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class Relation  extends RealmObject {
    private int number; //nummer
    private String name; //Naam
    private String town; //Plaats
    private String contactPerson; //ContactPersoon
    private String telephone; //Telefoon

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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
