package com.bionic.kvt.serviceapp.models;


public class Part { //Onderdeel
    private Byte bdmng; //BDMNG
    private String matnr; //MATNR
    private String mattx; //MATTX

    public Part() {
    }

    public Byte getBdmng() {
        return bdmng;
    }

    public void setBdmng(Byte bdmng) {
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
}