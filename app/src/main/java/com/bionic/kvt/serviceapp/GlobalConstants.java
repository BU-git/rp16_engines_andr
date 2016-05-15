package com.bionic.kvt.serviceapp;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface GlobalConstants {
    int ORDER_STATUS_NOT_FOUND = -1;
    int ORDER_STATUS_NOT_STARTED = 0;
    int ORDER_STATUS_IN_PROGRESS = 1;
    int ORDER_STATUS_COMPLETE = 2;
    int ORDER_STATUS_COMPLETE_UPLOADED = 3;
    int CUSTOM_ELEMENT_TEXT_FIELD = 1;
    int CUSTOM_ELEMENT_CHECK_BOX = 2;
    int CUSTOM_ELEMENT_TEXT_AREA = 3;
    int CUSTOM_ELEMENT_LABEL = 4;
    int ORDER_MAINTENANCE_START_TIME = 1;
    int ORDER_MAINTENANCE_END_TIME = 2;
    String UPDATE_SERVICE_MSG = "UPDATE_SERVICE_MSG";

//    @IntDef({FILE_TYPE_ORDER_PDF_REPORT, FILE_TYPE_ORDER_XML_DEFAULT_REPORT, FILE_TYPE_ORDER_XML_CUSTOM_REPORT, FILE_TYPE_ORDER_XML_MEASUREMENTS, FILE_TYPE_ORDER_XML_JOB_RULES})
//    @Retention(RetentionPolicy.SOURCE)
//    @interface UploadFileType {
//    }
//
//    int FILE_TYPE_ORDER_PDF_REPORT = 1;
//    int FILE_TYPE_ORDER_XML_DEFAULT_REPORT = 2;
//    int FILE_TYPE_ORDER_XML_CUSTOM_REPORT = 3;
//    int FILE_TYPE_ORDER_XML_MEASUREMENTS = 4;
//    int FILE_TYPE_ORDER_XML_JOB_RULES = 5;
int UPDATE_ORDERS = 1;
    int PREPARE_FILES = 2;
    int UPLOAD_FILES = 3;
    int DEFAULT_XML = 1;
    int CUSTOM_XML = 2;
    int MEASUREMENTS_XML = 3;
    int JOB_RULES_XML = 4;
    int PASSWORD_HASH_ITERATIONS = 3;
    int ORDER_OVERVIEW_COLUMN_COUNT = 7;
    double DRAWING_VIEW_PROPORTION = 2.6;
    String PDF_TEMPLATE_FILENAME_EN = "pdfTemplate_en.pdf";
    String PDF_REPORT_FILE_NAME = "Report_";
    String PDF_REPORT_PREVIEW_FILE_NAME = "Report_preview_";
    String LMRA_PHOTO_FILE_NAME = "LMRAPhoto_";
    int DEFAULT_SCORE = 1;
    double ONE_CONDITION = 0.01;
    double TWO_CONDITION = 0.04;
    double THREE_CONDITION = 0.15;
    double FOUR_CONDITION = 0.4;
    double FIVE_CONDITION = 0.78;
    int DEFAULT_RAW_SCORE = 550;

    @IntDef({ORDER_STATUS_NOT_FOUND, ORDER_STATUS_NOT_STARTED, ORDER_STATUS_IN_PROGRESS, ORDER_STATUS_COMPLETE, ORDER_STATUS_COMPLETE_UPLOADED})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrderStatus {
    }

    @IntDef({ORDER_MAINTENANCE_START_TIME, ORDER_MAINTENANCE_END_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrderMaintenanceType {
    }

    @IntDef({CUSTOM_ELEMENT_LABEL, CUSTOM_ELEMENT_TEXT_FIELD, CUSTOM_ELEMENT_TEXT_AREA, CUSTOM_ELEMENT_CHECK_BOX})
    @Retention(RetentionPolicy.SOURCE)
    @interface CustomElement {
    }
    @IntDef({UPDATE_ORDERS, PREPARE_FILES, UPLOAD_FILES})
    @Retention(RetentionPolicy.SOURCE)
    @interface ServiceMessage {
    }
    @IntDef({DEFAULT_XML, CUSTOM_XML, MEASUREMENTS_XML, JOB_RULES_XML})
    @Retention(RetentionPolicy.SOURCE)
    @interface XMLReportType {
    }
}
