package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bionic.kvt.serviceapp.helpers.CalculationHelper;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Model represents the saved state of a defect in the Default template
 */
public class DefectState implements Parcelable, Serializable, Comparable<DefectState> {


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
    private String part;
    private String element;
    private String problem;
    private Integer groupPosition;
    private Integer checkboxPosition;
    //Saving data for future logic
    private String extent; // Omvang
    private String intensity; // Intensiteit
    private boolean fixed; // Opgelost
    private String action; // Acties
    //Saving data for state
    private Integer extentId = 0;
    private Integer intensityId = 0;
    private Integer actionId = 0;
    //Calculation Score
    private Integer condition = 1;
    private Integer initialScore = 0;
    private Double correlation = 0d;
    private Double correlatedScore = 0d;
    public DefectState(String part, Integer groupPosition, Integer checkboxPosition) {
        this.checkboxPosition = checkboxPosition;
        this.groupPosition = groupPosition;
        this.part = part;

        this.extentId = 0;
        this.intensityId = 0;
        this.actionId = 0;
        this.fixed = false;
        //Calculation Score
        this.condition = 1;
        this.initialScore = 0;
        this.correlation = 0d;
        this.correlatedScore = 0d;
    }
    protected DefectState(Parcel in) {
        part = in.readString();
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    @Override
    public String toString() {
        return "DefectState{" +
                "part='" + part + '\'' +
                ", element='" + element + '\'' +
                ", problem='" + problem + '\'' +
                ", groupPosition=" + groupPosition +
                ", checkboxPosition=" + checkboxPosition +
                ", extent='" + extent + '\'' +
                ", intensity='" + intensity + '\'' +
                ", fixed=" + fixed +
                ", action='" + action + '\'' +
                ", extentId=" + extentId +
                ", intensityId=" + intensityId +
                ", actionId=" + actionId +
                ", condition=" + condition +
                ", initialScore=" + initialScore +
                ", correlation=" + correlation +
                ", correlatedScore=" + correlatedScore +
                '}';
    }

    public Integer getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(Integer initialScore) {
        this.initialScore = initialScore;
    }

    public Double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(Double correlation) {
        this.correlation = correlation;
    }

    public Double getCorrelatedScore() {
        return correlatedScore;
    }

    public void setCorrelatedScore(Double correlatedScore) {
        this.correlatedScore = correlatedScore;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

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

    //Method to adjust the score, once the parameter was being modified
    public void performScoreAdjustments(Map.Entry<String, JsonElement> child) {
        Integer tempCondition = CalculationHelper.INSTANCE.getCondition(
                extentId,
                intensityId,
                fixed,
                child.getValue().getAsJsonArray().get(0).getAsString()
        );
        if (tempCondition != null) condition = tempCondition;

        initialScore = child.getValue().getAsJsonArray().get(1).getAsInt();
        if (condition != null) {
            correlation = CalculationHelper.INSTANCE.getConditionFactor(getCondition());
            correlatedScore = correlation * initialScore;
        }
    }
}
