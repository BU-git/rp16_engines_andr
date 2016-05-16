package com.bionic.kvt.serviceapp.utils;

import android.support.annotation.Nullable;
import android.util.Xml;

import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.OrderReportJobRules;
import com.bionic.kvt.serviceapp.db.OrderReportMeasurements;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import io.realm.Realm;

public class XMLGenerator {

    @Nullable
    public static String getXMLFromDefaultTemplate(final long orderNumber) {
        return null;
    }

    @Nullable
    public static String getXMLFromCustomTemplate(final long orderNumber) {
        return null;
    }

    @Nullable
    public static String getXMLFromMeasurements(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final OrderReportMeasurements orderMeasurements =
                    realm.where(OrderReportMeasurements.class).equalTo("number", orderNumber).findFirst();
            if (orderMeasurements == null) return null;

            Session.addToSessionLog("Generating XML from Order [" + orderNumber + "] measurements.");

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);

                serializer.startTag("", "Report");
                serializer.attribute("", "Type", "ORDER_XML_MEASUREMENTS");

                serializer.startTag("", "Order");
                serializer.attribute("", "Number", String.valueOf(orderNumber));
                serializer.endTag("", "Order");

                serializer.startTag("", "MotorCompressionPressure"); // Motor: Compression test [bar]
                serializer.text(orderMeasurements.getMotorCompressionPressure());
                serializer.endTag("", "MotorCompressionPressure");

                serializer.startTag("", "MotorOilPressure"); // Motor: Oil [bar]
                serializer.text(orderMeasurements.getMotorOilPressure());
                serializer.endTag("", "MotorOilPressure");

                serializer.startTag("", "MotorOilTemperature"); // Motor: Oil [C]
                serializer.text(orderMeasurements.getMotorOilTemperature());
                serializer.endTag("", "MotorOilTemperature");

                serializer.startTag("", "MotorOilType"); // Motor: Oil
                serializer.text(orderMeasurements.getMotorOilType());
                serializer.endTag("", "MotorOilType");

                serializer.startTag("", "MotorOilManufacture"); // Motor: Oil
                serializer.text(orderMeasurements.getMotorOilManufacture());
                serializer.endTag("", "MotorOilManufacture");

                serializer.startTag("", "MotorCoolantTemperature"); // Motor: Coolant [C]
                serializer.text(orderMeasurements.getMotorCoolantTemperature());
                serializer.endTag("", "MotorCoolantTemperature");

                serializer.startTag("", "MotorCoolantAcidity"); // Motor: Coolant [pH]
                serializer.text(orderMeasurements.getMotorCoolantAcidity());
                serializer.endTag("", "MotorCoolantAcidity");

                serializer.startTag("", "MotorCoolantFrost"); // Motor: Coolant [C]
                serializer.text(orderMeasurements.getMotorCoolantFrost());
                serializer.endTag("", "MotorCoolantFrost");

                serializer.startTag("", "InstallationEnvironmentTemperature"); // Installation: Environment [C]
                serializer.text(orderMeasurements.getInstallationEnvironmentTemperature());
                serializer.endTag("", "InstallationEnvironmentTemperature");

                serializer.startTag("", "InstallationTestVoltage"); // Installation: Test run charge [Volt]
                serializer.text(orderMeasurements.getInstallationTestVoltage());
                serializer.endTag("", "InstallationTestVoltage");

                serializer.startTag("", "InstallationTestAmperePhase1"); // Installation: Test run charge [Ampere]
                serializer.text(orderMeasurements.getInstallationTestAmperePhase1());
                serializer.endTag("", "InstallationTestAmperePhase1");

                serializer.startTag("", "InstallationTestAmperePhase2"); // Installation: Test run charge [Ampere]
                serializer.text(orderMeasurements.getInstallationTestAmperePhase2());
                serializer.endTag("", "InstallationTestAmperePhase2");

                serializer.startTag("", "InstallationTestAmperePhase3"); // Installation: Test run charge [Ampere]
                serializer.text(orderMeasurements.getInstallationTestAmperePhase3());
                serializer.endTag("", "InstallationTestAmperePhase3");

                serializer.startTag("", "InstallationTestPower"); // Installation: Test run charge [kW]
                serializer.text(orderMeasurements.getInstallationTestPower());
                serializer.endTag("", "InstallationTestPower");

                serializer.startTag("", "InstallationTestFrequency"); // Installation: Test run charge [Hz]
                serializer.text(orderMeasurements.getInstallationTestFrequency());
                serializer.endTag("", "InstallationTestFrequency");

                serializer.startTag("", "InstallationTestNoLoadFrequency"); // Installation: Test run without load [Hz]
                serializer.text(orderMeasurements.getInstallationTestNoLoadFrequency());
                serializer.endTag("", "InstallationTestNoLoadFrequency");

                serializer.startTag("", "ExhaustGasesTemperature"); // Exhaust: Exhaust gases [C]
                serializer.text(orderMeasurements.getExhaustGasesTemperature());
                serializer.endTag("", "ExhaustGasesTemperature");

                serializer.startTag("", "ExhaustGasesPressure"); // Exhaust: Exhaust gases [Mbar]
                serializer.text(orderMeasurements.getExhaustGasesPressure());
                serializer.endTag("", "ExhaustGasesPressure");

                serializer.startTag("", "RunningHours"); //  Running hours [Hours]
                serializer.text(orderMeasurements.getRunningHours());
                serializer.endTag("", "RunningHours");

                serializer.endTag("", "Report");

                serializer.endDocument();
                return writer.toString();
            } catch (IOException e) {
                Session.addToSessionLog("**** ERROR **** generating XML: " + e.toString());
            }
        }
        return null;
    }

    @Nullable
    public static String getXMLFromJobRules(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final OrderReportJobRules orderReportJobRules =
                    realm.where(OrderReportJobRules.class).equalTo("number", orderNumber).findFirst();
            if (orderReportJobRules == null) return null;

            Session.addToSessionLog("Generating XML from Order [" + orderNumber + "] job rules.");

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);

                serializer.startTag("", "Report");
                serializer.attribute("", "Type", "ORDER_XML_JOB_RULES");

                serializer.startTag("", "Order");
                serializer.attribute("", "Number", String.valueOf(orderNumber));
                serializer.endTag("", "Order");

                serializer.startTag("", "FuelAdded");
                serializer.text(orderReportJobRules.isFuelAdded() ? "true" : "false");
                serializer.endTag("", "FuelAdded");

                serializer.startTag("", "WaterSeparatorsDrained");
                serializer.text(orderReportJobRules.isWaterSeparatorsDrained() ? "true" : "false");
                serializer.endTag("", "WaterSeparatorsDrained");

                serializer.startTag("", "LeaveOperational");
                serializer.text(orderReportJobRules.isLeaveOperational() ? "true" : "false");
                serializer.endTag("", "LeaveOperational");

                serializer.startTag("", "UseCustomerMaterial");
                serializer.text(orderReportJobRules.isUseCustomerMaterial() ? "true" : "false");
                serializer.endTag("", "UseCustomerMaterial");

                serializer.startTag("", "UseMaterialFromBus");
                serializer.text(orderReportJobRules.isUseMaterialFromBus() ? "true" : "false");
                serializer.endTag("", "UseMaterialFromBus");

                serializer.startTag("", "RepairAdvice");
                serializer.text(orderReportJobRules.isRepairAdvice() ? "true" : "false");
                serializer.endTag("", "RepairAdvice");

                serializer.startTag("", "RemainingWork");
                serializer.text(orderReportJobRules.isRemainingWork() ? "true" : "false");
                serializer.endTag("", "RemainingWork");

                serializer.startTag("", "InternalRemarksText");
                serializer.text(orderReportJobRules.getInternalRemarksText());
                serializer.endTag("", "InternalRemarksText");

                serializer.startTag("", "ExternalRemarksText");
                serializer.text(orderReportJobRules.getExternalRemarksText());
                serializer.endTag("", "ExternalRemarksText");

                serializer.endTag("", "Report");

                serializer.endDocument();
                return writer.toString();
            } catch (IOException e) {
                Session.addToSessionLog("**** ERROR **** generating XML: " + e.toString());
            }
        }
        return null;
    }

}
