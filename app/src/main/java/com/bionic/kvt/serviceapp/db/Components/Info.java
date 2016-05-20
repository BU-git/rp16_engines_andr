package com.bionic.kvt.serviceapp.db.Components;

import io.realm.RealmObject;

public class Info extends RealmObject {
    private String kindOfLine; //SoortRegel
    private String prePost; //PrePost
    private String sleutel; //Sleutel
    private String line; //Regel
    private String description; //Omschrijving

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Info{");
        sb.append("kindOfLine='").append(kindOfLine).append('\'');
        sb.append(", prePost='").append(prePost).append('\'');
        sb.append(", sleutel='").append(sleutel).append('\'');
        sb.append(", line='").append(line).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
