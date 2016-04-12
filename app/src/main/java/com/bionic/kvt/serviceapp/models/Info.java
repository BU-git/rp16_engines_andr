package com.bionic.kvt.serviceapp.models;

public class Info { //Info
    private String kindOfLine; //SoortRegel
    private String prePost; //PrePost
    private String sleutel; //Sleutel
    private String line; //Regel
    private String description; //Omschrijving

    public Info() {
    }

    public String getKindOfLine() {
        return kindOfLine;
    }

    public void setKindOfLine(String kindOfLine) {
        this.kindOfLine = kindOfLine;
    }

    public String getPrePost() {
        return prePost;
    }

    public void setPrePost(String prePost) {
        this.prePost = prePost;
    }

    public String getSleutel() {
        return sleutel;
    }

    public void setSleutel(String sleutel) {
        this.sleutel = sleutel;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
