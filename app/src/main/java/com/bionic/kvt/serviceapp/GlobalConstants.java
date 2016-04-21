package com.bionic.kvt.serviceapp;

public interface GlobalConstants {
    int ORDER_STATUS_NOT_STARTED = 0;
    int ORDER_STATUS_IN_PROGRESS = 1;
    int ORDER_STATUS_COMPLETE = 2;

    int ORDER_OVERVIEW_COLUMN_COUNT = 7;

    double DRAWING_VIEW_PROPORTION = 2.6;

    String PDF_TEMPLATE_FILENAME_EN = "pdfTemplate_en.pdf";
    String PDF_REPORT_FILE_NAME = "Report_";

    String SIGNATURE_FILE_CLIENT = "signature_client.png";
    String SIGNATURE_FILE_ENGINEER = "signature_engineer.png";
}
