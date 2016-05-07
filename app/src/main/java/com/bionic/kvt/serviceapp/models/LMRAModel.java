package com.bionic.kvt.serviceapp.models;


import java.io.File;
import java.util.List;

public class LMRAModel {
    private long lmraId; // TimeStamp

    private String lmraName;
    private String lmraDescription;
    private List<File> listLMRAPhotos;

    public long getLmraId() {
        return lmraId;
    }

    public void setLmraId(long lmraId) {
        this.lmraId = lmraId;
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

    public List<File> getListLMRAPhotos() {
        return listLMRAPhotos;
    }

    public void setListLMRAPhotos(List<File> listLMRAPhotos) {
        this.listLMRAPhotos = listLMRAPhotos;
    }
}
