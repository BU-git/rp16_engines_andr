package com.bionic.kvt.serviceapp.db;

import com.bionic.kvt.serviceapp.GlobalConstants;

import io.realm.RealmObject;

public class CustomTemplateElement extends RealmObject {

    @GlobalConstants.CustomElement
    int elementType;
    String elementText;
    String elementValue;

    @GlobalConstants.CustomElement
    public int getElementType() {
        return elementType;
    }

    public void setElementType(@GlobalConstants.CustomElement int elementType) {
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
