package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class Task extends RealmObject {
    private String vornr; //VORNR
    private String ltxa1; //LTXA1
    private String steus; //STEUS
    private String ktsch; //KTSCH

    public String getVornr() {
        return vornr;
    }

    public void setVornr(String vornr) {
        this.vornr = vornr;
    }

    public String getLtxa1() {
        return ltxa1;
    }

    public void setLtxa1(String ltxa1) {
        this.ltxa1 = ltxa1;
    }

    public String getSteus() {
        return steus;
    }

    public void setSteus(String steus) {
        this.steus = steus;
    }

    public String getKtsch() {
        return ktsch;
    }

    public void setKtsch(String ktsch) {
        this.ktsch = ktsch;
    }
}
