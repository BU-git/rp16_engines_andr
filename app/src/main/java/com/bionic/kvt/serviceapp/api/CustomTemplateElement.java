package com.bionic.kvt.serviceapp.api;

import static com.bionic.kvt.serviceapp.GlobalConstants.CustomElement;

public class CustomTemplateElement {
    int elementType;
    String elementText;
    String elementValue;

    public CustomTemplateElement() {
    }

    @CustomElement
    public int getElementType() {
        return elementType;
    }

    public void setElementType(@CustomElement final int elementType) {
        this.elementType = elementType;
    }

    public String getElementText() {
        return elementText;
    }

    public void setElementText(final String elementText) {
        this.elementText = elementText;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(final String elementValue) {
        this.elementValue = elementValue;
    }
}

