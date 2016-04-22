package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

/** */
public class DefectState implements Parcelable, Serializable, Comparable<DefectState> {



    private String part;

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public Integer getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(Integer groupPosition) {
        this.groupPosition = groupPosition;
    }

    public Integer getCheckboxPosition() {
        return checkboxPosition;
    }

    public void setCheckboxPosition(Integer checkboxPosition) {
        this.checkboxPosition = checkboxPosition;
    }

    private Integer groupPosition;
    private Integer checkboxPosition;

    public DefectState (String part, Integer groupPosition, Integer checkboxPosition){
        this.checkboxPosition = checkboxPosition;
        this.groupPosition = groupPosition;
        this.part = part;
    }

    protected DefectState(Parcel in) {
        part = in.readString();
    }

    public static final Creator<DefectState> CREATOR = new Creator<DefectState>() {
        @Override
        public DefectState createFromParcel(Parcel in) {
            return new DefectState(in);
        }

        @Override
        public DefectState[] newArray(int size) {
            return new DefectState[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefectState)) return false;
        DefectState that = (DefectState) o;
        return Objects.equals(getPart(), that.getPart()) &&
                Objects.equals(getGroupPosition(), that.getGroupPosition()) &&
                Objects.equals(getCheckboxPosition(), that.getCheckboxPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPart(), getGroupPosition(), getCheckboxPosition());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(part);
    }

    @Override
    public int compareTo(DefectState another) {
        if (!part.equals(another.getPart())) return -1;
        if (!groupPosition.equals(another.getGroupPosition())) return -1;
        else if (checkboxPosition.equals(another.getCheckboxPosition())) return 0;
        return 1;
    }
}
