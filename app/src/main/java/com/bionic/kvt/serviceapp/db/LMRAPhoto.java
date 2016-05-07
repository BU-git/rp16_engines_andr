package com.bionic.kvt.serviceapp.db;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LMRAPhoto extends RealmObject {

    @PrimaryKey
    private long lmraId; // Copy of LMRAItem -> lmraID

    private long number; // Order number

    private String lmraPhotoFile;
    private boolean lmraPhotoFileSynced;

    public long getLmraId() {
        return lmraId;
    }

    public void setLmraId(long lmraId) {
        this.lmraId = lmraId;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getLmraPhotoFile() {
        return lmraPhotoFile;
    }

    public void setLmraPhotoFile(String lmraPhotoFile) {
        this.lmraPhotoFile = lmraPhotoFile;
    }

    public boolean isLmraPhotoFileSynced() {
        return lmraPhotoFileSynced;
    }

    public void setLmraPhotoFileSynced(boolean lmraPhotoFileSynced) {
        this.lmraPhotoFileSynced = lmraPhotoFileSynced;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LMRAPhoto{");
        sb.append("lmraId=").append(lmraId);
        sb.append(", number=").append(number);
        sb.append(", lmraPhotoFile='").append(lmraPhotoFile).append('\'');
        sb.append(", lmraPhotoFileSynced=").append(lmraPhotoFileSynced);
        sb.append('}');
        return sb.toString();
    }
}
