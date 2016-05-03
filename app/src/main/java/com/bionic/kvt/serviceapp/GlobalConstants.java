package com.bionic.kvt.serviceapp;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface GlobalConstants {
    @IntDef({ORDER_STATUS_NOT_FOUND, ORDER_STATUS_NOT_STARTED, ORDER_STATUS_IN_PROGRESS, ORDER_STATUS_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrderStatus {}

    int ORDER_STATUS_NOT_FOUND = -1;
    int ORDER_STATUS_NOT_STARTED = 0;
    int ORDER_STATUS_IN_PROGRESS = 1;
    int ORDER_STATUS_COMPLETE = 2;


    @IntDef({ORDER_MAINTENANCE_START_TIME, ORDER_MAINTENANCE_END_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrderMaintenanceType {}

    int ORDER_MAINTENANCE_START_TIME = 1;
    int ORDER_MAINTENANCE_END_TIME = 2;



    int ORDER_OVERVIEW_COLUMN_COUNT = 7;

    double DRAWING_VIEW_PROPORTION = 2.6;

    String PDF_TEMPLATE_FILENAME_EN = "pdfTemplate_en.pdf";
    String PDF_REPORT_FILE_NAME = "Report_";
    String PDF_REPORT_PREVIEW_FILE_NAME = "Report_preview_";
}
