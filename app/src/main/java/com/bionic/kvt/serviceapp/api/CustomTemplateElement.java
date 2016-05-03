package com.bionic.kvt.serviceapp.api;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomTemplateElement {
    @IntDef({CUSTOM_ELEMENT_LABEL, CUSTOM_ELEMENT_TEXT_FIELD, CUSTOM_ELEMENT_TEXT_AREA, CUSTOM_ELEMENT_CHECK_BOX})
    @Retention(RetentionPolicy.SOURCE)
    @interface CustomElement {}

    public static final int CUSTOM_ELEMENT_LABEL = 1;
    public static final int CUSTOM_ELEMENT_TEXT_FIELD = 2;
    public static final int CUSTOM_ELEMENT_TEXT_AREA = 3;
    public static final int CUSTOM_ELEMENT_CHECK_BOX = 4;

    int elementType;
    String elementText;
    String elementValue;

    public CustomTemplateElement() {
    }

    public @CustomElement int getElementType() {
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

