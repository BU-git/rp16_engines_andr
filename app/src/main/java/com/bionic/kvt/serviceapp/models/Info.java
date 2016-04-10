package com.bionic.kvt.serviceapp.models;

public class Info { //Info
    private String kindOfLine; //SoortRegel
    private String prePost; //PrePost
    private String key; //Sleutel
    private Byte line; //Regel
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Byte getLine() {
        return line;
    }

    public void setLine(Byte line) {
        this.line = line;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
