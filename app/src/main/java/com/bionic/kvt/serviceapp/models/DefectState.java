package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

/** */
public class DefectState implements Parcelable, Serializable, Comparable<DefectState> {



    private String part;

    @Override
    public String toString() {
        return "DefectState{" +
                "part='" + part + '\'' +
                ", element='" + element + '\'' +
                ", groupPosition=" + groupPosition +
                ", checkboxPosition=" + checkboxPosition +
                ", extent='" + extent + '\'' +
                ", intensity='" + intensity + '\'' +
                ", fixed=" + fixed +
                ", action='" + action + '\'' +
                ", extentId=" + extentId +
                ", intensityId=" + intensityId +
                ", actionId=" + actionId +
                '}';
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    private String element;
    private Integer groupPosition;
    private Integer checkboxPosition;

    //Saving data for future logic
    private String extent; // Omvang
    private String intensity; // Intensiteit
    private boolean fixed = false; // Opgelost
    private String action; // Acties

    //Saving data for state
    private Integer extentId = 0;
    private Integer intensityId = 0;
    private Integer actionId = 0;

    public Integer getExtentId() {
        return extentId;
    }

    public void setExtentId(Integer extentId) {
        this.extentId = extentId;
    }

    public Integer getIntensityId() {
        return intensityId;
    }

    public void setIntensityId(Integer intensityId) {
        this.intensityId = intensityId;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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
