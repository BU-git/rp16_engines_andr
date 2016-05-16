package com.bionic.kvt.serviceapp.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.bionic.kvt.serviceapp.utils.Utils.nullStringToEmpty;

public class OrderReportMeasurements extends RealmObject {
    @PrimaryKey
    private long number;

    private String motorCompressionPressure = ""; // Motor: Compression test [bar]

    private String motorOilPressure; // Motor: Oil [bar]
    private String motorOilTemperature; // Motor: Oil [C]
    private String motorOilType; // Motor: Oil
    private String motorOilManufacture; // Motor: Oil

    private String motorCoolantTemperature; // Motor: Coolant [C]
    private String motorCoolantAcidity; // Motor: Coolant [pH]
    private String motorCoolantFrost; // Motor: Coolant [C]

    private String installationEnvironmentTemperature; // Installation: Environment [C]

    private String installationTestVoltage; // Installation: Test run charge [Volt]
    private String installationTestAmperePhase1; // Installation: Test run charge [Ampere]
    private String installationTestAmperePhase2; // Installation: Test run charge [Ampere]
    private String installationTestAmperePhase3; // Installation: Test run charge [Ampere]
    private String installationTestPower; // Installation: Test run charge [kW]
    private String installationTestFrequency; // Installation: Test run charge [Hz]

    private String installationTestNoLoadFrequency; // Installation: Test run without load [Hz]

    private String exhaustGasesTemperature; // Exhaust: Exhaust gases [C]
    private String exhaustGasesPressure; // Exhaust: Exhaust gases [Mbar]

    private String runningHours; // Running hours [Hours]

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getMotorCompressionPressure() {
        return motorCompressionPressure;
    }

    public void setMotorCompressionPressure(String motorCompressionPressure) {
        this.motorCompressionPressure = nullStringToEmpty(motorCompressionPressure);
    }

    public String getMotorOilPressure() {
        return motorOilPressure;
    }

    public void setMotorOilPressure(String motorOilPressure) {
        this.motorOilPressure = nullStringToEmpty(motorOilPressure);
    }

    public String getMotorOilTemperature() {
        return motorOilTemperature;
    }

    public void setMotorOilTemperature(String motorOilTemperature) {
        this.motorOilTemperature = nullStringToEmpty(motorOilTemperature);
    }

    public String getMotorOilType() {
        return motorOilType;
    }

    public void setMotorOilType(String motorOilType) {
        this.motorOilType = nullStringToEmpty(motorOilType);
    }

    public String getMotorOilManufacture() {
        return motorOilManufacture;
    }

    public void setMotorOilManufacture(String motorOilManufacture) {
        this.motorOilManufacture = nullStringToEmpty(motorOilManufacture);
    }

    public String getMotorCoolantTemperature() {
        return motorCoolantTemperature;
    }

    public void setMotorCoolantTemperature(String motorCoolantTemperature) {
        this.motorCoolantTemperature = nullStringToEmpty(motorCoolantTemperature);
    }

    public String getMotorCoolantAcidity() {
        return motorCoolantAcidity;
    }

    public void setMotorCoolantAcidity(String motorCoolantAcidity) {
        this.motorCoolantAcidity = nullStringToEmpty(motorCoolantAcidity);
    }

    public String getMotorCoolantFrost() {
        return motorCoolantFrost;
    }

    public void setMotorCoolantFrost(String motorCoolantFrost) {
        this.motorCoolantFrost = nullStringToEmpty(motorCoolantFrost);
    }

    public String getInstallationEnvironmentTemperature() {
        return installationEnvironmentTemperature;
    }

    public void setInstallationEnvironmentTemperature(String installationEnvironmentTemperature) {
        this.installationEnvironmentTemperature = nullStringToEmpty(installationEnvironmentTemperature);
    }

    public String getInstallationTestVoltage() {
        return installationTestVoltage;
    }

    public void setInstallationTestVoltage(String installationTestVoltage) {
        this.installationTestVoltage = nullStringToEmpty(installationTestVoltage);
    }

    public String getInstallationTestAmperePhase1() {
        return installationTestAmperePhase1;
    }

    public void setInstallationTestAmperePhase1(String installationTestAmperePhase1) {
        this.installationTestAmperePhase1 = nullStringToEmpty(installationTestAmperePhase1);
    }

    public String getInstallationTestAmperePhase2() {
        return installationTestAmperePhase2;
    }

    public void setInstallationTestAmperePhase2(String installationTestAmperePhase2) {
        this.installationTestAmperePhase2 = nullStringToEmpty(installationTestAmperePhase2);
    }

    public String getInstallationTestAmperePhase3() {
        return installationTestAmperePhase3;
    }

    public void setInstallationTestAmperePhase3(String installationTestAmperePhase3) {
        this.installationTestAmperePhase3 = nullStringToEmpty(installationTestAmperePhase3);
    }

    public String getInstallationTestPower() {
        return installationTestPower;
    }

    public void setInstallationTestPower(String installationTestPower) {
        this.installationTestPower = nullStringToEmpty(installationTestPower);
    }

    public String getInstallationTestFrequency() {
        return installationTestFrequency;
    }

    public void setInstallationTestFrequency(String installationTestFrequency) {
        this.installationTestFrequency = nullStringToEmpty(installationTestFrequency);
    }

    public String getInstallationTestNoLoadFrequency() {
        return installationTestNoLoadFrequency;
    }

    public void setInstallationTestNoLoadFrequency(String installationTestNoLoadFrequency) {
        this.installationTestNoLoadFrequency = nullStringToEmpty(installationTestNoLoadFrequency);
    }

    public String getExhaustGasesTemperature() {
        return exhaustGasesTemperature;
    }

    public void setExhaustGasesTemperature(String exhaustGasesTemperature) {
        this.exhaustGasesTemperature = nullStringToEmpty(exhaustGasesTemperature);
    }

    public String getExhaustGasesPressure() {
        return exhaustGasesPressure;
    }

    public void setExhaustGasesPressure(String exhaustGasesPressure) {
        this.exhaustGasesPressure = nullStringToEmpty(exhaustGasesPressure);
    }

    public String getRunningHours() {
        return runningHours;
    }

    public void setRunningHours(String runningHours) {
        this.runningHours = nullStringToEmpty(runningHours);
    }
}
