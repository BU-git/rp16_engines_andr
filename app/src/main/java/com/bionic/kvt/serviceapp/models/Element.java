package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**

 */
public class Element implements Parcelable, Serializable {

    private String elementName;
    private List<Problem> problemList;

    public Element() {
    }

    public String getElementName() {
        return elementName;
    }

    public List<Problem> getProblemList() {
        return problemList;
    }

    protected Element(Parcel in) {
        elementName = in.readString();
        problemList = in.createTypedArrayList(Problem.CREATOR);
    }

    public static final Creator<Element> CREATOR = new Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel in) {
            return new Element(in);
        }

        @Override
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(elementName);
        dest.writeTypedList(problemList);
    }
}
