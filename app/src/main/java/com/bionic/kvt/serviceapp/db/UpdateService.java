package com.bionic.kvt.serviceapp.db;


import android.app.IntentService;
import android.content.Intent;

import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class UpdateService extends IntentService {
    public UpdateService() {
        super("KVT Service: Update service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceLog("Update service started.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateOrdersFromServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceLog("Update service stopped.");
    }

    private void serviceLog(final String message) {
        Session.addToSessionLog("UPDATE_SERVICE: " + message);
    }

    private void updateOrdersFromServer() {
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