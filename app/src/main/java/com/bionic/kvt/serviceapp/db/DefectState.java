package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;

public class DefectState extends RealmObject {
    private long number;

    private String part;
    private String element;
    private String problem;

    //Saving data for future logic
    private String extent; // Omvang
    private String intensity; // Intensiteit
    private boolean fixed = false; // Opgelost
    private String action; // Acties

    // Calculation Score
    private Integer condition;
    private Integer initialScore;
    private Double correlation;
    private Double correlatedScore;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
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
}
