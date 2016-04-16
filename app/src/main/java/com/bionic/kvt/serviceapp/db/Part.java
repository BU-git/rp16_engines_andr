package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class Part extends RealmObject {
    private String bdmng; //BDMNG
    private String matnr; //MATNR
    private String mattx; //MATTX

    public String getBdmng() {
        return bdmng;
    }

    public void setBdmng(String bdmng) {
        this.bdmng = bdmng;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getMattx() {
        return mattx;
    }

    public void setMattx(String mattx) {
        this.mattx = mattx;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Part{");
        sb.append("bdmng='").append(bdmng).append('\'');
        sb.append(", matnr='").append(matnr).append('\'');
        sb.append(", mattx='").append(mattx).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
