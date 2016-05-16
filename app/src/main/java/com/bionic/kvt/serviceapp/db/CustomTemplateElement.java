package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class CustomTemplateElement extends RealmObject {
    int elementType;
    String elementText;
    String elementValue;

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getElementText() {
        return elementText;
    }

    public void setElementText(String elementText) {
        this.elementText = elementText;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }
}
