package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderReportMeasurements extends RealmObject {
    @PrimaryKey
    private long number;

    private float motorCompressionPressure; // Motor: Compression test [bar]

    private float motorOilPressure; // Motor: Oil [bar]
    private float motorOilTemperature; // Motor: Oil [C]
    private String motorOilType; // Motor: Oil
    private String motorOilManufacture; // Motor: Oil

    private float motorCoolantTemperature; // Motor: Coolant [C]
    private float motorCoolantAcidity; // Motor: Coolant [pH]
    private float motorCoolantFrost; // Motor: Coolant [C]

    private float installationEnvironmentTemperature; // Installation: Environment [C]

    private float installationTestVoltage; // Installation: Test run charge [Volt]
    private float installationTestAmperePhase1; // Installation: Test run charge [Ampere]
    private float installationTestAmperePhase2; // Installation: Test run charge [Ampere]
    private float installationTestAmperePhase3; // Installation: Test run charge [Ampere]
    private float installationTestPower; // Installation: Test run charge [kW]
    private float installationTestFrequency; // Installation: Test run charge [Hz]

    private float installationTestNoLoadFrequency; // Installation: Test run without load [Hz]

    private float exhaustGasesTemperature; // Exhaust: Exhaust gases [C]
    private float exhaustGasesPressure; // Exhaust: Exhaust gases [Mbar]

    private float workingHours; // Working hours [Hours]

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public float getMotorCompressionPressure() {
        return motorCompressionPressure;
    }

    public void setMotorCompressionPressure(float motorCompressionPressure) {
        this.motorCompressionPressure = motorCompressionPressure;
    }

    public float getMotorOilPressure() {
        return motorOilPressure;
    }

    public void setMotorOilPressure(float motorOilPressure) {
        this.motorOilPressure = motorOilPressure;
    }

    public float getMotorOilTemperature() {
        return motorOilTemperature;
    }

    public void setMotorOilTemperature(float motorOilTemperature) {
        this.motorOilTemperature = motorOilTemperature;
    }

    public String getMotorOilType() {
        return motorOilType;
    }

    public void setMotorOilType(String motorOilType) {
        this.motorOilType = motorOilType;
    }

    public String getMotorOilManufacture() {
        return motorOilManufacture;
    }

    public void setMotorOilManufacture(String motorOilManufacture) {
        this.motorOilManufacture = motorOilManufacture;
    }

    public float getMotorCoolantTemperature() {
        return motorCoolantTemperature;
    }

    public void setMotorCoolantTemperature(float motorCoolantTemperature) {
        this.motorCoolantTemperature = motorCoolantTemperature;
    }

    public float getMotorCoolantAcidity() {
        return motorCoolantAcidity;
    }

    public void setMotorCoolantAcidity(float motorCoolantAcidity) {
        this.motorCoolantAcidity = motorCoolantAcidity;
    }

    public float getMotorCoolantFrost() {
        return motorCoolantFrost;
    }

    public void setMotorCoolantFrost(float motorCoolantFrost) {
        this.motorCoolantFrost = motorCoolantFrost;
    }

    public float getInstallationEnvironmentTemperature() {
        return installationEnvironmentTemperature;
    }

    public void setInstallationEnvironmentTemperature(float installationEnvironmentTemperature) {
        this.installationEnvironmentTemperature = installationEnvironmentTemperature;
    }

    public float getInstallationTestVoltage() {
        return installationTestVoltage;
    }

    public void setInstallationTestVoltage(float installationTestVoltage) {
        this.installationTestVoltage = installationTestVoltage;
    }

    public float getInstallationTestAmperePhase1() {
        return installationTestAmperePhase1;
    }

    public void setInstallationTestAmperePhase1(float installationTestAmperePhase1) {
        this.installationTestAmperePhase1 = installationTestAmperePhase1;
    }

    public float getInstallationTestAmperePhase2() {
        return installationTestAmperePhase2;
    }

    public void setInstallationTestAmperePhase2(float installationTestAmperePhase2) {
        this.installationTestAmperePhase2 = installationTestAmperePhase2;
    }

    public float getInstallationTestAmperePhase3() {
        return installationTestAmperePhase3;
    }

    public void setInstallationTestAmperePhase3(float installationTestAmperePhase3) {
        this.installationTestAmperePhase3 = installationTestAmperePhase3;
    }

    public float getInstallationTestPower() {
        return installationTestPower;
    }

    public void setInstallationTestPower(float installationTestPower) {
        this.installationTestPower = installationTestPower;
    }

    public float getInstallationTestFrequency() {
        return installationTestFrequency;
    }

    public void setInstallationTestFrequency(float installationTestFrequency) {
        this.installationTestFrequency = installationTestFrequency;
    }

    public float getInstallationTestNoLoadFrequency() {
        return installationTestNoLoadFrequency;
    }

    public void setInstallationTestNoLoadFrequency(float installationTestNoLoadFrequency) {
        this.installationTestNoLoadFrequency = installationTestNoLoadFrequency;
    }

    public float getExhaustGasesTemperature() {
        return exhaustGasesTemperature;
    }

    public void setExhaustGasesTemperature(float exhaustGasesTemperature) {
        this.exhaustGasesTemperature = exhaustGasesTemperature;
    }

    public float getExhaustGasesPressure() {
        return exhaustGasesPressure;
    }

    public void setExhaustGasesPressure(float exhaustGasesPressure) {
        this.exhaustGasesPressure = exhaustGasesPressure;
    }

    public float getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(float workingHours) {
        this.workingHours = workingHours;
    }
}
