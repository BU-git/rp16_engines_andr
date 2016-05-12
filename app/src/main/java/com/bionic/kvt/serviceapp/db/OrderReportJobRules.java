package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderReportJobRules extends RealmObject {
    @PrimaryKey
    private long number;

    private boolean fuelAdded;
    private boolean waterSeparatorsDrained;
    private boolean leaveOperational;
    private boolean useCustomerMaterial;
    private boolean useMaterialFromBus;
    private boolean repairAdvice;
    private boolean remainingWork;
    private String internalRemarksText;
    private String externalRemarksText;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isFuelAdded() {
        return fuelAdded;
    }

    public void setFuelAdded(boolean fuelAdded) {
        this.fuelAdded = fuelAdded;
    }

    public boolean isWaterSeparatorsDrained() {
        return waterSeparatorsDrained;
    }

    public void setWaterSeparatorsDrained(boolean waterSeparatorsDrained) {
        this.waterSeparatorsDrained = waterSeparatorsDrained;
    }

    public boolean isLeaveOperational() {
        return leaveOperational;
    }

    public void setLeaveOperational(boolean leaveOperational) {
        this.leaveOperational = leaveOperational;
    }

    public boolean isUseCustomerMaterial() {
        return useCustomerMaterial;
    }

    public void setUseCustomerMaterial(boolean useCustomerMaterial) {
        this.useCustomerMaterial = useCustomerMaterial;
    }

    public boolean isUseMaterialFromBus() {
        return useMaterialFromBus;
    }

    public void setUseMaterialFromBus(boolean useMaterialFromBus) {
        this.useMaterialFromBus = useMaterialFromBus;
    }

    public boolean isRepairAdvice() {
        return repairAdvice;
    }

    public void setRepairAdvice(boolean repairAdvice) {
        this.repairAdvice = repairAdvice;
    }

    public String getInternalRemarksText() {
        return internalRemarksText;
    }

    public void setInternalRemarksText(String internalRemarksText) {
        this.internalRemarksText = internalRemarksText;
    }

    public String getExternalRemarksText() {
        return externalRemarksText;
    }

    public void setExternalRemarksText(String externalRemarksText) {
        this.externalRemarksText = externalRemarksText;
    }

    public boolean isRemainingWork() {
        return remainingWork;
    }

    public void setRemainingWork(boolean remainingWork) {
        this.remainingWork = remainingWork;
    }
}
