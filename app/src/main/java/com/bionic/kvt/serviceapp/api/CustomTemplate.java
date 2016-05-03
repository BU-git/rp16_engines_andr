package com.bionic.kvt.serviceapp.api;

import java.util.ArrayList;

public class CustomTemplate {
    private long ID;
    private long orderNumber;
    private ArrayList<CustomTemplateElement> customTemplateElements; //Order of elements is important

    public CustomTemplate() {
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ArrayList<CustomTemplateElement> getCustomTemplateElements() {
        return customTemplateElements;
    }

    public void setCustomTemplateElements(ArrayList<CustomTemplateElement> customTemplateElements) {
        this.customTemplateElements = customTemplateElements;
    }
}
