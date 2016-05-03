package com.bionic.kvt.serviceapp.api;

import java.util.ArrayList;

public class CustomTemplate {
    private long customTemplateID;
    private String customTemplateName;
    private ArrayList<CustomTemplateElement> customTemplateElements; //Order of elements is important

    public CustomTemplate() {
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

    public ArrayList<CustomTemplateElement> getCustomTemplateElements() {
        return customTemplateElements;
    }

    public void setCustomTemplateElements(ArrayList<CustomTemplateElement> customTemplateElements) {
        this.customTemplateElements = customTemplateElements;
    }
}
