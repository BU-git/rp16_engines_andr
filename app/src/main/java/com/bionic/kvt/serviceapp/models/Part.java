package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**

 */
public class Part implements Parcelable, Serializable {

    private String partName;
    private List<Element> elementList;
    private Integer score;

    protected Part(Parcel in) {
        partName = in.readString();
        elementList = in.createTypedArrayList(Element.CREATOR);
    }

    public static final Creator<Part> CREATOR = new Creator<Part>() {
        @Override
        public Part createFromParcel(Parcel in) {
            return new Part(in);
        }

        @Override
        public Part[] newArray(int size) {
            return new Part[size];
        }
    };

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public List<Element> getElementList() {
        return elementList;
    }

    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partName);
        dest.writeTypedList(elementList);
    }
}
