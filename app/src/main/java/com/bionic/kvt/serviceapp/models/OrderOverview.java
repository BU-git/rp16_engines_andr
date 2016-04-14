package com.bionic.kvt.serviceapp.models;

import java.util.Date;

public class OrderOverview {
    private long number; // Order -> number
    private Date date;  // Order -> date
    private String installationName; // Order -> installation -> name
    private String taskLtxa1; // Order -> Task -> ltxa1
    private String installationAddress; // Order -> installation -> address
    private int orderStatus; // Order -> orderStatus
    private String pdfString; // Just "PDF" string

    public Long getNumber() { // Need Long for easy toString() in OrderAdapter
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInstallationName() {
        return installationName;
    }

    public void setInstallationName(String installationName) {
        this.installationName = installationName;
    }

    public String getTaskLtxa1() {
        return taskLtxa1;
    }

    public void setTaskLtxa1(String taskLtxa1) {
        this.taskLtxa1 = taskLtxa1;
    }

    public String getInstallationAddress() {
        return installationAddress;
    }

    public void setInstallationAddress(String installationAddress) {
        this.installationAddress = installationAddress;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPdfString() {
        return pdfString;
    }

    public void setPdfString(String pdfString) {
        this.pdfString = pdfString;
    }
}
