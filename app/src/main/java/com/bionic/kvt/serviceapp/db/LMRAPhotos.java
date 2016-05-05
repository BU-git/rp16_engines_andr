package com.bionic.kvt.serviceapp.db;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LMRAPhotos extends RealmObject {

    @PrimaryKey
    private long number;

    private String listLMRAPhoto;
    private boolean listLMRAPhotoSynced;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getListLMRAPhoto() {
        return listLMRAPhoto;
    }

    public void setListLMRAPhoto(String listLMRAPhoto) {
        this.listLMRAPhoto = listLMRAPhoto;
    }

    public boolean isListLMRAPhotoSynced() {
        return listLMRAPhotoSynced;
    }

    public void setListLMRAPhotoSynced(boolean listLMRAPhotoSynced) {
        this.listLMRAPhotoSynced = listLMRAPhotoSynced;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LMRAPhotos{");
        sb.append("listLMRAPhotoSynced=").append(listLMRAPhotoSynced);
        sb.append(", listLMRAPhoto='").append(listLMRAPhoto).append('\'');
        sb.append(", number=").append(number);
        sb.append('}');
        return sb.toString();
    }
}
