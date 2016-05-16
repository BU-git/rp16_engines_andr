package com.bionic.kvt.serviceapp.db;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CustomTemplate extends RealmObject {

    private long number; // Order number

    @PrimaryKey
    private long customTemplateID;
    private String customTemplateName;
    private RealmList<CustomTemplateElement> customTemplateElements; //Order of elements is important

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getCustomTemplateID() {
        return customTemplateID;
    }

    public void setCustomTemplateID(long customTemplateID) {
        this.customTemplateID = customTemplateID;
    }

    public String getCustomTemplateName() {
        return customTemplateName;
    }

    public void setCustomTemplateName(String customTemplateName) {
        this.customTemplateName = customTemplateName;
    }

    public RealmList<CustomTemplateElement> getCustomTemplateElements() {
        return customTemplateElements;
    }

    public void setCustomTemplateElements(RealmList<CustomTemplateElement> customTemplateElements) {
        this.customTemplateElements = customTemplateElements;
    }
}
