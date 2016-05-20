package com.bionic.kvt.serviceapp.db.Components;

import com.bionic.kvt.serviceapp.GlobalConstants;

import io.realm.RealmObject;

import static com.bionic.kvt.serviceapp.utils.Utils.nullStringToEmpty;

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
        this.elementText = nullStringToEmpty(elementText);
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = nullStringToEmpty(elementValue);
    }
}
