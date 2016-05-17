package com.bionic.kvt.serviceapp.utils;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.CustomTemplate;
import com.bionic.kvt.serviceapp.db.CustomTemplateElement;
import com.bionic.kvt.serviceapp.db.LMRAItem;
import com.bionic.kvt.serviceapp.db.LMRAPhoto;
import com.bionic.kvt.serviceapp.db.OrderReportJobRules;
import com.bionic.kvt.serviceapp.db.OrderReportMeasurements;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

public class XMLGenerator {

    @Nullable
    public static String getXMLFromLMRA(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {

            final RealmResults<LMRAItem> allLMRAItemsInDb =
                    realm.where(LMRAItem.class).equalTo("number", orderNumber).findAll();
            if (allLMRAItemsInDb.size() == 0) return null;
            final RealmResults<LMRAItem> allLMRAItemsInDbSorted = allLMRAItemsInDb.sort("lmraId");

            Session.addToSessionLog("Generating XML from Order [" + orderNumber + "] LMRA data.");

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);

                serializer.startTag("", "Report");
                serializer.attribute("", "Type", "ORDER_XML_LMRA");

                serializer.startTag("", "Order");
                serializer.attribute("", "Number", String.valueOf(orderNumber));
                serializer.endTag("", "Order");

                serializer.startTag("", "LMRAItems");
                for (LMRAItem lmraItem : allLMRAItemsInDbSorted) {
                    serializer.startTag("", "LMRAItem");
                    serializer.attribute("", "ID", String.valueOf(lmraItem.getLmraId()));

                    serializer.startTag("", "Name");
                    serializer.text(String.valueOf(lmraItem.getLmraName()));
                    serializer.endTag("", "Name");

                    serializer.startTag("", "Description");
                    serializer.text(lmraItem.getLmraDescription());
                    serializer.endTag("", "Description");

                    final RealmResults<LMRAPhoto> listLMRAPhotosInBD =
                            realm.where(LMRAPhoto.class)
                                    .equalTo("number", orderNumber)
                                    .equalTo("lmraId", lmraItem.getLmraId())
                                    .findAll();

                    if (listLMRAPhotosInBD.size() > 0) {
                        serializer.startTag("", "LMRAPhotoItems");
                        for (LMRAPhoto lmraPhoto : listLMRAPhotosInBD) {
                            serializer.startTag("", "LMRAPhotoItem");
                            serializer.text((new File(lmraPhoto.getLmraPhotoFile())).getName());
                            serializer.endTag("", "LMRAPhotoItem");
                        }
                        serializer.endTag("", "LMRAPhotoItems");
                    }

                    serializer.endTag("", "LMRAItem");
                }

                serializer.endTag("", "LMRAItems");
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
    public static String getXMLFromDefaultTemplate(final long orderNumber) {
        Session.addToSessionLog("Generating XML from Order [" + orderNumber + "] Default template.");

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            serializer.startTag("", "Report");
            serializer.attribute("", "Type", "ORDER_XML_DEFAULT_TEMPLATE");

            serializer.startTag("", "Order");
            serializer.attribute("", "Number", String.valueOf(orderNumber));
            serializer.endTag("", "Order");

            serializer.startTag("", "Parts");

            for (String part : Session.getPartMap().keySet()) {
                serializer.startTag("", "Part");
                serializer.attribute("", "Name", part);
                Set<Map.Entry<String, JsonObject>> entrySet = Session.getPartMap().get(part).entrySet();
                //top level
                for (Map.Entry<String, JsonObject> element : entrySet) {
                    serializer.startTag("", "Element");
                    serializer.attribute("", "Name", element.getKey());
                    //Installation general
                    Set<Map.Entry<String, JsonElement>> thirdLevel = element.getValue().entrySet();
                    for (Map.Entry<String, JsonElement> problem : thirdLevel) {
                        //Defecten
                        serializer.startTag("", "Problem");
                        serializer.attribute("", "Name", problem.getKey());

                        serializer.endTag("", "Problem");
                    }
                    serializer.endTag("", "Element");
                }
                serializer.endTag("", "Part");
            }

            serializer.endTag("", "Parts");
            serializer.endTag("", "Report");

            serializer.endDocument();
            Log.e("XXX", writer.toString());
            return writer.toString();
        } catch (IOException e) {
            Session.addToSessionLog("**** ERROR **** generating XML: " + e.toString());
        }


        return null;
    }

    @Nullable
    public static String getXMLFromCustomTemplate(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final CustomTemplate customTemplate =
                    realm.where(CustomTemplate.class).equalTo("number", Session.getCurrentOrder()).findFirst();
            if (customTemplate == null) return null;

            Session.addToSessionLog("Generating XML from Order [" + orderNumber + "] custom template.");

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);

                serializer.startTag("", "Report");
                serializer.attribute("", "Type", "ORDER_XML_CUSTOM_TEMPLATE");

                serializer.startTag("", "Order");
                serializer.attribute("", "Number", String.valueOf(orderNumber));
                serializer.endTag("", "Order");

                serializer.startTag("", "CustomTemplateName");
                serializer.text(customTemplate.getCustomTemplateName());
                serializer.endTag("", "CustomTemplateName");

                serializer.startTag("", "CustomTemplateElements");

                for (CustomTemplateElement customTemplateElement : customTemplate.getCustomTemplateElements()) {
                    switch (customTemplateElement.getElementType()) {
                        case GlobalConstants.CUSTOM_ELEMENT_TEXT_FIELD:
                            serializer.startTag("", "CustomTemplateElement");
                            serializer.attribute("", "Type", "CUSTOM_ELEMENT_TEXT_FIELD");
                            serializer.attribute("", "Text", customTemplateElement.getElementText());
                            serializer.text(customTemplateElement.getElementValue());
                            serializer.endTag("", "CustomTemplateElement");
                            break;

                        case GlobalConstants.CUSTOM_ELEMENT_CHECK_BOX:
                            serializer.startTag("", "CustomTemplateElement");
                            serializer.attribute("", "Type", "CUSTOM_ELEMENT_CHECK_BOX");
                            serializer.attribute("", "Text", customTemplateElement.getElementText());
                            serializer.text(customTemplateElement.getElementValue());
                            serializer.endTag("", "CustomTemplateElement");
                            break;

                        case GlobalConstants.CUSTOM_ELEMENT_TEXT_AREA:
                            serializer.startTag("", "CustomTemplateElement");
                            serializer.attribute("", "Type", "CUSTOM_ELEMENT_TEXT_AREA");
                            serializer.attribute("", "Text", customTemplateElement.getElementText());
                            serializer.text(customTemplateElement.getElementValue());
                            serializer.endTag("", "CustomTemplateElement");
                            break;

                        case GlobalConstants.CUSTOM_ELEMENT_LABEL:
                            //NOOP
                            break;
                    }
                }
                serializer.endTag("", "CustomTemplateElements");
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
