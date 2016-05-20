package com.bionic.kvt.serviceapp.db.Components;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Component{");
        sb.append("eqart='").append(eqart).append('\'');
        sb.append(", equnr=").append(equnr);
        sb.append(", herst='").append(herst).append('\'');
        sb.append(", typbz='").append(typbz).append('\'');
        sb.append(", sernr='").append(sernr).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
