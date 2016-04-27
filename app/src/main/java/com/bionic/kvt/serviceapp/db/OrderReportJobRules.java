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
    private String operationsText;
    private String remarksText;

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

    public String getOperationsText() {
        return operationsText;
    }

    public void setOperationsText(String operationsText) {
        this.operationsText = operationsText;
    }

    public String getRemarksText() {
        return remarksText;
    }

    public void setRemarksText(String remarksText) {
        this.remarksText = remarksText;
    }
}
