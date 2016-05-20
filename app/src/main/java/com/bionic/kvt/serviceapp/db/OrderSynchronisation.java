package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderSynchronisation extends RealmObject {

    @PrimaryKey
    private long number;

    private boolean isReadyForSync;
    private boolean isSyncComplete;
    private boolean isError;

    private String zipFileWithXMLs;
    private boolean zipFileWithXMLsSynced;

    private String defaultPDFReportFile;
    private boolean defaultPDFReportFileSynced;

    //    private RealmList<LMRAPhoto> listLMRAPhotos;
    private boolean isLMRAPhotosSynced;

    // Service fields
    private String orderLMRAXMLReportFile;
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

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
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

    public boolean isLMRAPhotosSynced() {
        return isLMRAPhotosSynced;
    }

    public void setLMRAPhotosSynced(boolean LMRAPhotosSynced) {
        isLMRAPhotosSynced = LMRAPhotosSynced;
    }

    public String getOrderLMRAXMLReportFile() {
        return orderLMRAXMLReportFile;
    }

    public void setOrderLMRAXMLReportFile(String orderLMRAXMLReportFile) {
        this.orderLMRAXMLReportFile = orderLMRAXMLReportFile;
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
}