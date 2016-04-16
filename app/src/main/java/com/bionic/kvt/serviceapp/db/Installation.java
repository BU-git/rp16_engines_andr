package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class Installation extends RealmObject {
    private String name; //Naam
    private String address; //Adres
    private String postCode; //PostCode
    private String town; //Plaats

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Installation{");
        sb.append("name='").append(name).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", postCode='").append(postCode).append('\'');
        sb.append(", town='").append(town).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
