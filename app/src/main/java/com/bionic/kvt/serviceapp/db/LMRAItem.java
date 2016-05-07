package com.bionic.kvt.serviceapp.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LMRAItem extends RealmObject {

    @PrimaryKey
    private long lmraId; // System.currentTimeMillis()

    private long number; // Order number

    private String lmraName;
    private String lmraDescription;
    private RealmList<LMRAPhoto> listLMRAPhotos;

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

    public String getLmraName() {
        return lmraName;
    }

    public void setLmraName(String lmraName) {
        this.lmraName = lmraName;
    }

    public String getLmraDescription() {
        return lmraDescription;
    }

    public void setLmraDescription(String lmraDescription) {
        this.lmraDescription = lmraDescription;
    }

    public RealmList<LMRAPhoto> getListLMRAPhotos() {
        return listLMRAPhotos;
    }

    public void setListLMRAPhotos(RealmList<LMRAPhoto> listLMRAPhotos) {
        this.listLMRAPhotos = listLMRAPhotos;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LMRAItem{");
        sb.append("lmraId=").append(lmraId);
        sb.append(", number=").append(number);
        sb.append(", lmraName='").append(lmraName).append('\'');
        sb.append(", lmraDescription='").append(lmraDescription).append('\'');
        sb.append(", listLMRAPhotos=").append(listLMRAPhotos);
        sb.append('}');
        return sb.toString();
    }
}