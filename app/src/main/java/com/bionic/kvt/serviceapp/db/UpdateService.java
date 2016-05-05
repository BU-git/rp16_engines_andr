package com.bionic.kvt.serviceapp.db;


import android.app.IntentService;
import android.content.Intent;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.GlobalConstants.ServiceMessage;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

import static com.bionic.kvt.serviceapp.GlobalConstants.CUSTOM_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.DEFAULT_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.MEASUREMENTS_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.PREPARE_FILES;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_ORDERS;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_SERVICE_MSG;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPLOAD_FILES;


public class UpdateService extends IntentService {
    private String currentTask = "";

    public UpdateService() {
        super("KVT Service: Update service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        @ServiceMessage int serviceMessage = intent.getIntExtra(UPDATE_SERVICE_MSG, 0);
        currentTask = "";
        switch (serviceMessage) {
            case UPDATE_ORDERS:
                currentTask = "UPDATE_ORDERS";
                updateOrdersFromServer();
                break;
            case PREPARE_FILES:
                currentTask = "PREPARE_FILES";
                prepareOrderFilesToUpload();
                break;
            case UPLOAD_FILES:
                currentTask = "UPLOAD_FILES";
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceLog("Service stopped.");
    }

    private void serviceLog(final String message) {
        Session.addToSessionLog("UPDATE SERVICE [" + currentTask + "]: " + message);
    }

    private void updateOrdersFromServer() {
        serviceLog("Service started.");

        if (!Utils.isNetworkConnected(getApplicationContext())) {
            serviceLog("No connection to network. Canceling update.");
            return;
        }

        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getServiceConnection().getOrdersBrief(Session.getEngineerEmail());

        serviceLog("Getting orders brief list from: " + orderBriefListRequest.request());

        final Response<List<OrderBrief>> orderBriefListResponse;
        try {
            orderBriefListResponse = orderBriefListRequest.execute();
        } catch (IOException e) {
            serviceLog("Orders brief list request fail: " + e.toString());
            return;
        }

        if (!orderBriefListResponse.isSuccessful()) {
            serviceLog("Orders brief list request error: " + orderBriefListResponse.code());
            return;
        }

        serviceLog("Request successful. Get " + orderBriefListResponse.body().size() + " brief orders.");

        final List<Long> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(orderBriefListResponse.body());

        if (ordersToBeUpdated.isEmpty()) {
            serviceLog("Nothing to update.");
            return;
        }

        for (Long orderNumber : ordersToBeUpdated) {
            final Call<com.bionic.kvt.serviceapp.api.Order> orderRequest =
                    Session.getServiceConnection().getOrder(orderNumber, Session.getEngineerEmail());

            serviceLog("Getting order from: " + orderRequest.request());

            final Response<com.bionic.kvt.serviceapp.api.Order> orderResponse;
            try {
                orderResponse = orderRequest.execute();
            } catch (IOException e) {
                serviceLog("Order request fail: " + e.toString());
                return;
            }
            if (!orderResponse.isSuccessful()) {
                serviceLog("Order request error: " + orderResponse.code());
                return;
            }

            serviceLog("Request successful!");

            DbUtils.updateOrderFromServer(orderResponse.body());
        }

        serviceLog("Update " + ordersToBeUpdated.size() + " orders.");
    }

    private void prepareOrderFilesToUpload() {
        serviceLog("Service started.");
        final List<Long> orderNumberToPrepare = DbUtils.getOrdersToBeUploaded();
        final Realm realm = Realm.getDefaultInstance();

        for (Long orderNumber : orderNumberToPrepare) {
            serviceLog("Preparing files to upload for order: " + orderNumber);

            OrderSynchronisation currentOrderSync =
                    realm.where(OrderSynchronisation.class).equalTo("number", orderNumber).findFirst();
            if (currentOrderSync != null) {
                if (!currentOrderSync.isReadyForSync()) { // Task preparation is uncompleted
                    realm.beginTransaction();
                    currentOrderSync.removeFromRealm();
                    realm.commitTransaction(); // No logic if transaction fail!!!
                } else { // Task preparation is completed. Skipping.
                    serviceLog("Files to upload already prepared: " + orderNumber);
                    continue;
                }
            }

            final OrderSynchronisation orderSync = new OrderSynchronisation();
            orderSync.setNumber(orderNumber);

            // Setting zipFileWithXMLs
            orderSync.setOrderDefaultXMLReportFile(Utils.generateXMLReport(orderNumber, DEFAULT_XML));
            orderSync.setOrderCustomXMLReportFile(Utils.generateXMLReport(orderNumber, CUSTOM_XML));
            orderSync.setOrderMeasurementsXMLReportFile(Utils.generateXMLReport(orderNumber, MEASUREMENTS_XML));
            orderSync.setOrderJobRulesXMLReportFile(Utils.generateXMLReport(orderNumber, GlobalConstants.JOB_RULES_XML));

            // TODO zipFileWithXMLs

            orderSync.setZipFileWithXMLsSynced(false);

            // Setting defaultPDFReportFile
            orderSync.setDefaultPDFReportFile(Utils.getPDFReportFileName(orderNumber, false).toString());
            orderSync.setDefaultPDFReportFileSynced(false);

            // Setting listLMRAPhotos
            // TODO LMRA LIST


            orderSync.setReadyForSync(true);

            realm.beginTransaction();
            realm.copyToRealm(orderSync);
            realm.commitTransaction(); // No logic if transaction fail!!!

            serviceLog("Preparing files to upload done: " + orderSync.toString());
        }

        realm.close();
    }


}


//            final List<Long> ordersToBeUploaded = DbUtils.getOrdersToBeUploaded();
//            for (Long orderNumberToBeUpload : ordersToBeUploaded) {
//                File fileName = Utils.getPDFReportFileName(orderNumberToBeUpload, false);
//                uploadFile( FILE_TYPE_ORDER_PDF_REPORT, fileName);
//            }


//    private static OrderUpdateResult uploadFile(@UploadFileType final int fileType, final File filename) {
//
//        // create RequestBody instance from file
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filename);
//
//        String fileTypeString = "UNSET";
//        switch (fileType) {
//            case FILE_TYPE_ORDER_PDF_REPORT:
//                fileTypeString = "ORDER_PDF_REPORT";
//                break;
//            case FILE_TYPE_ORDER_XML_CUSTOM_REPORT:
//                fileTypeString = "ORDER_XML_CUSTOM_REPORT";
//                break;
//            case FILE_TYPE_ORDER_XML_DEFAULT_REPORT:
//                fileTypeString = "ORDER_XML_DEFAULT_REPORT";
//                break;
//            case FILE_TYPE_ORDER_XML_JOB_RULES:
//                fileTypeString = "ORDER_XML_JOB_RULES";
//                break;
//            case FILE_TYPE_ORDER_XML_MEASUREMENTS:
//                fileTypeString = "ORDER_XML_MEASUREMENTS";
//                break;
//        }
//
//        // MultipartBody.Part is used to send the actual file name and file type
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData(fileTypeString, filename.getName(), requestFile);
//
//        // add another part within the multipart request
//        String checksum = Utils.getFileMD5Sum(filename);
//        RequestBody checksumBody = RequestBody.create(MediaType.parse("multipart/form-data"), checksum);
//
//        // finally, execute the request
//        final Call<ResponseBody> call = Session.getServiceConnection().uploadFile(Session.getCurrentOrder(), checksumBody, body);
//
//        final Response<ResponseBody> uploadFileResponse;
//        try {
//            uploadFileResponse = call.execute();
//        } catch (IOException e) {
//            return new OrderUpdateResult(1, "Upload fail: " + e.toString());
//        }
//
//        if (!uploadFileResponse.isSuccessful())
//            return new OrderUpdateResult(1, "Upload fail: " + uploadFileResponse.code());
//
//        return new OrderUpdateResult(0, "Upload successful: " + uploadFileResponse.code());
//
//    }