package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class Component extends RealmObject {
    private String eqart; //EQART
    private int equnr; //EQUNR
    private String herst; //HERST
    private String typbz; //TYPBZ
    private String sernr; //SERNR

    public String getEqart() {
        return eqart;
    }

    public void setEqart(String eqart) {
        this.eqart = eqart;
    }

    public int getEqunr() {
        return equnr;
    }

    public void setEqunr(int equnr) {
        this.equnr = equnr;
    }

    public String getHerst() {
        return herst;
    }

    public void setHerst(String herst) {
        this.herst = herst;
    }

    public String getTypbz() {
        return typbz;
    }

    public void setTypbz(String typbz) {
        this.typbz = typbz;
    }

    public String getSernr() {
        return sernr;
    }

    public void setSernr(String sernr) {
        this.sernr = sernr;
    }
}
