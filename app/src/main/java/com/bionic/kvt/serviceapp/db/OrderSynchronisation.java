package com.bionic.kvt.serviceapp.db;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderSynchronisation extends RealmObject {

    @PrimaryKey
    private long number;

    private boolean isReadyForSync;
    private boolean isSyncComplete;

    private String zipFileWithXMLs;
    private boolean zipFileWithXMLsSynced;

    private String defaultPDFReportFile;
    private boolean defaultPDFReportFileSynced;

    private RealmList<LMRAPhotos> listLMRAPhotos;

    // Service fields
    private String orderDefaultXMLReportFile;
    private String orderCustomXMLReportFile;
    private String orderMeasurementsXMLReportFile;
    private String orderJobRulesXMLReportFile;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isReadyForSync() {
        return isReadyForSync;
    }

    public void setReadyForSync(boolean readyForSync) {
        isReadyForSync = readyForSync;
    }

    public boolean isSyncComplete() {
        return isSyncComplete;
    }

    public void setSyncComplete(boolean syncComplete) {
        isSyncComplete = syncComplete;
    }

    public String getZipFileWithXMLs() {
        return zipFileWithXMLs;
    }

    public void setZipFileWithXMLs(String zipFileWithXMLs) {
        this.zipFileWithXMLs = zipFileWithXMLs;
    }

    public boolean isZipFileWithXMLsSynced() {
        return zipFileWithXMLsSynced;
    }

    public void setZipFileWithXMLsSynced(boolean zipFileWithXMLsSynced) {
        this.zipFileWithXMLsSynced = zipFileWithXMLsSynced;
    }

    public String getDefaultPDFReportFile() {
        return defaultPDFReportFile;
    }

    public void setDefaultPDFReportFile(String defaultPDFReportFile) {
        this.defaultPDFReportFile = defaultPDFReportFile;
    }

    public boolean isDefaultPDFReportFileSynced() {
        return defaultPDFReportFileSynced;
    }

    public void setDefaultPDFReportFileSynced(boolean defaultPDFReportFileSynced) {
        this.defaultPDFReportFileSynced = defaultPDFReportFileSynced;
    }

    public RealmList<LMRAPhotos> getListLMRAPhotos() {
        return listLMRAPhotos;
    }

    public void setListLMRAPhotos(RealmList<LMRAPhotos> listLMRAPhotos) {
        this.listLMRAPhotos = listLMRAPhotos;
    }

    public String getOrderDefaultXMLReportFile() {
        return orderDefaultXMLReportFile;
    }

    public void setOrderDefaultXMLReportFile(String orderDefaultXMLReportFile) {
        this.orderDefaultXMLReportFile = orderDefaultXMLReportFile;
    }

    public String getOrderCustomXMLReportFile() {
        return orderCustomXMLReportFile;
    }

    public void setOrderCustomXMLReportFile(String orderCustomXMLReportFile) {
        this.orderCustomXMLReportFile = orderCustomXMLReportFile;
    }

    public String getOrderMeasurementsXMLReportFile() {
        return orderMeasurementsXMLReportFile;
    }

    public void setOrderMeasurementsXMLReportFile(String orderMeasurementsXMLReportFile) {
        this.orderMeasurementsXMLReportFile = orderMeasurementsXMLReportFile;
    }

    public String getOrderJobRulesXMLReportFile() {
        return orderJobRulesXMLReportFile;
    }

    public void setOrderJobRulesXMLReportFile(String orderJobRulesXMLReportFile) {
        this.orderJobRulesXMLReportFile = orderJobRulesXMLReportFile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OrderSynchronisation{");
        sb.append("number=").append(number);
        sb.append(", isReadyForSync=").append(isReadyForSync);
        sb.append(", isSyncComplete=").append(isSyncComplete);
        sb.append(", zipFileWithXMLs='").append(zipFileWithXMLs).append('\'');
        sb.append(", zipFileWithXMLsSynced=").append(zipFileWithXMLsSynced);
        sb.append(", defaultPDFReportFile='").append(defaultPDFReportFile).append('\'');
        sb.append(", defaultPDFReportFileSynced=").append(defaultPDFReportFileSynced);
        sb.append(", listLMRAPhotos=").append(listLMRAPhotos);
        sb.append(", orderDefaultXMLReportFile='").append(orderDefaultXMLReportFile).append('\'');
        sb.append(", orderCustomXMLReportFile='").append(orderCustomXMLReportFile).append('\'');
        sb.append(", orderMeasurementsXMLReportFile='").append(orderMeasurementsXMLReportFile).append('\'');
        sb.append(", orderJobRulesXMLReportFile='").append(orderJobRulesXMLReportFile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}