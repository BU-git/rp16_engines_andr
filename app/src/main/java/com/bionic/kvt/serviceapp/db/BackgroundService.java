package com.bionic.kvt.serviceapp.db;


import android.app.IntentService;
import android.content.Intent;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.GlobalConstants.ServiceMessage;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.CustomTemplate;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.helpers.JSONHelper;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.bionic.kvt.serviceapp.GlobalConstants.CUSTOM_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.DEFAULT_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.GENERATE_PART_MAP;
import static com.bionic.kvt.serviceapp.GlobalConstants.JOB_RULES_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.LMRA_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.MEASUREMENTS_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.PREPARE_FILES;
import static com.bionic.kvt.serviceapp.GlobalConstants.REPORTS_XML_ZIP_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_ORDERS;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_SERVICE_MSG;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPLOAD_FILES;


public class BackgroundService extends IntentService {
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_PDF = MediaType.parse("application/pdf");
    private static final MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");

    private String currentTask = "";

    public BackgroundService() {
        super("KVT Service: Update service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        @ServiceMessage final int serviceMessage = intent.getIntExtra(UPDATE_SERVICE_MSG, 0);
        currentTask = "";
        switch (serviceMessage) {
            case UPDATE_ORDERS:
                currentTask = "SERVICE [UPDATE ORDERS]: ";
                updateOrdersFromServer();
                break;
            case PREPARE_FILES:
                currentTask = "SERVICE [PREPARE FILES]: ";
                prepareOrderFilesToUpload();
                break;
            case UPLOAD_FILES:
                currentTask = "SERVICE [UPLOAD FILES]: ";
                uploadOrderFiles();
                break;
            case GENERATE_PART_MAP:
                currentTask = "SERVICE [GENERATE PART MAP]: ";
                generatePartMap();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.serviceI(currentTask + "Service stopped.");
    }

    private void updateOrdersFromServer() {
        AppLog.serviceI(currentTask + "Service started.");

        if (!Utils.isNetworkConnected(getApplicationContext())) {
            AppLog.serviceI(true, -1, currentTask + "No connection to network. Canceling update.");
            return;
        }

        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getServiceConnection().getOrdersBrief(Session.getEngineerEmail());

        AppLog.serviceI(currentTask + "Getting orders brief list from: " + orderBriefListRequest.request());

        final Response<List<OrderBrief>> orderBriefListResponse;
        try {
            orderBriefListResponse = orderBriefListRequest.execute();
        } catch (IOException e) {
            AppLog.serviceW(true, -1, currentTask + "Orders brief list request fail: " + e.toString());
            return;
        }

        if (!orderBriefListResponse.isSuccessful()) {
            AppLog.serviceW(true, -1, currentTask + "Orders brief list request error: " + orderBriefListResponse.code());
            return;
        }

        AppLog.serviceI(currentTask + "Request successful. Get " + orderBriefListResponse.body().size() + " brief orders.");

        final List<Long> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(orderBriefListResponse.body());

        if (ordersToBeUpdated.isEmpty()) {
            AppLog.serviceI(currentTask + "Nothing to update.");
            return;
        }

        for (Long orderNumber : ordersToBeUpdated) {
            final Call<com.bionic.kvt.serviceapp.api.Order> orderRequest =
                    Session.getServiceConnection().getOrder(orderNumber, Session.getEngineerEmail());

            AppLog.serviceI(currentTask + "Getting order from: " + orderRequest.request());

            final Response<com.bionic.kvt.serviceapp.api.Order> orderResponse;
            try {
                orderResponse = orderRequest.execute();
            } catch (IOException e) {
                AppLog.serviceW(true, -1, currentTask + "Order request fail: " + e.toString());
                return;
            }
            if (!orderResponse.isSuccessful()) {
                AppLog.serviceW(true, -1, currentTask + "Order request error: " + orderResponse.code());
                return;
            }

            AppLog.serviceI(currentTask + "Request successful!");

            final com.bionic.kvt.serviceapp.api.Order orderOnServer = orderResponse.body();
            DbUtils.updateOrderFromServer(orderOnServer);

            if (orderOnServer.getCustomTemplateID() > 0)
                updateCustomTemplateFromServer(orderOnServer.getNumber(), orderOnServer.getCustomTemplateID());
        }

        AppLog.serviceI(currentTask + "Update " + ordersToBeUpdated.size() + " orders.");
    }

    private void updateCustomTemplateFromServer(final long orderNumber, final long customTemplateID) {
        final Call<CustomTemplate> customTemplateRequest =
                Session.getServiceConnection().getTemplate(customTemplateID);

        AppLog.serviceI(currentTask + "Getting custom template from: " + customTemplateRequest.request());

        final Response<CustomTemplate> customTemplateResponse;
        try {
            customTemplateResponse = customTemplateRequest.execute();
        } catch (IOException e) {
            AppLog.serviceW(true, -1, currentTask + "Custom template request fail: " + e.toString());
            return;
        }

        if (!customTemplateResponse.isSuccessful()) {
            AppLog.serviceW(true, -1, currentTask + "Custom template request error: " + customTemplateResponse.code());
            return;
        }

        AppLog.serviceI(currentTask + "Request successful. Get [" + customTemplateResponse.body().getCustomTemplateName() + "] template.");

        DbUtils.updateCustomTemplateFromServer(orderNumber, customTemplateResponse.body());
        AppLog.serviceI(currentTask + "Custom template update complete.");
    }

    private void prepareOrderFilesToUpload() {
        AppLog.serviceI(currentTask + "Service started.");
        final List<Long> orderNumberToPrepare = DbUtils.getOrdersToBePrepared();
        try (final Realm realm = Realm.getDefaultInstance()) {

            for (Long orderNumber : orderNumberToPrepare) {
                AppLog.serviceI(currentTask + "Preparing files to upload for order: " + orderNumber);

                OrderSynchronisation currentOrderSync =
                        realm.where(OrderSynchronisation.class).equalTo("number", orderNumber).findFirst();
                if (currentOrderSync != null) {
                    if (!currentOrderSync.isReadyForSync()) { // Task preparation is uncompleted
                        AppLog.serviceI(currentTask + "Previous preparation failed. Cleaning.");
                        realm.beginTransaction();
                        currentOrderSync.deleteFromRealm();
                        realm.commitTransaction(); // No logic if transaction fail!!!
                    } else { // Task preparation is completed. Skipping.
                        AppLog.serviceI(currentTask + "Files to upload already prepared: " + orderNumber);
                        continue;
                    }
                }

                final OrderSynchronisation orderSync = new OrderSynchronisation();
                orderSync.setNumber(orderNumber);

//                generatePartMap();


                // Setting zipFileWithXMLs
                orderSync.setOrderLMRAXMLReportFile(DbUtils.generateXMLReport(orderNumber, LMRA_XML));
                orderSync.setOrderDefaultXMLReportFile(DbUtils.generateXMLReport(orderNumber, DEFAULT_XML));
                orderSync.setOrderCustomXMLReportFile(DbUtils.generateXMLReport(orderNumber, CUSTOM_XML));
                orderSync.setOrderMeasurementsXMLReportFile(DbUtils.generateXMLReport(orderNumber, MEASUREMENTS_XML));
                orderSync.setOrderJobRulesXMLReportFile(DbUtils.generateXMLReport(orderNumber, JOB_RULES_XML));

                final String[] XMLFilesToZIP = {
                        orderSync.getOrderLMRAXMLReportFile(),
                        orderSync.getOrderDefaultXMLReportFile(),
                        orderSync.getOrderCustomXMLReportFile(),
                        orderSync.getOrderMeasurementsXMLReportFile(),
                        orderSync.getOrderJobRulesXMLReportFile()
                };

                final File reportsXMLZipFile = new File(Utils.getOrderDir(orderNumber), REPORTS_XML_ZIP_FILE_NAME + orderNumber + ".zip");

                // Compressing
                final boolean zipSuccessful = Utils.zipXMLReportFiles(XMLFilesToZIP, reportsXMLZipFile.toString());

                if (zipSuccessful) {
                    orderSync.setZipFileWithXMLs(reportsXMLZipFile.toString());
                } else {
                    orderSync.setZipFileWithXMLs(null);
                    AppLog.serviceI(currentTask + "XML files compressing failed.");
                    Utils.deleteRecursive(reportsXMLZipFile);
                }

                // Remove XML files
                for (String file : XMLFilesToZIP) {
                    if (file != null) Utils.deleteRecursive(new File(file));
                }

                orderSync.setZipFileWithXMLsSynced(false);

                // Setting defaultPDFReportFile
                orderSync.setDefaultPDFReportFile(Utils.getPDFReportFileName(orderNumber, false).toString());
                orderSync.setDefaultPDFReportFileSynced(false);

                orderSync.setReadyForSync(true);

                realm.beginTransaction();
                realm.copyToRealm(orderSync);
                realm.commitTransaction(); // No logic if transaction fail!!!

                AppLog.serviceI(currentTask + "Preparing files to upload done.");
            }

        }
    }

    private boolean uploadFile(final String fileToUpload, final MediaType mediaType, final String fileType, final long orderNumber) {
        final File fileName = new File(fileToUpload);

        if (!fileName.exists()) {
            AppLog.serviceE(true, orderNumber, currentTask + "File not found: " + fileToUpload);
            return false;
        }

        final RequestBody requestFile = RequestBody.create(mediaType, fileName);
        final String checksum = Utils.getFileMD5Sum(fileName);

        if (checksum.equals("")) {
            AppLog.serviceE(true, orderNumber, currentTask + "Error calculating checksum.");
            return false;
        }

        final MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", fileType)
                .addFormDataPart("checksum", checksum)
                .addFormDataPart("file", fileName.getName(), requestFile)
                .build();

        final Call<ResponseBody> call = Session.getServiceConnection().uploadFile(orderNumber, requestBody);
        AppLog.serviceI(currentTask + "UPLOAD REQUEST [" + fileType + "]: " + call.request());

        final Response<ResponseBody> uploadFileResponse;
        try {
            uploadFileResponse = call.execute();
        } catch (IOException e) {
            AppLog.serviceW(true, -1, currentTask + "Upload fail: " + e.toString());
            return false;
        }

        if (!uploadFileResponse.isSuccessful()) {
            AppLog.serviceW(true, -1, currentTask + "Upload fail: " + uploadFileResponse.code());
            return false;
        }

        AppLog.serviceI(currentTask + "Upload successful: " + uploadFileResponse.code());
        return true;
    }

    private void uploadOrderFiles() {
        AppLog.serviceI(currentTask + "Service started.");
        boolean uploadResult;

        try (final Realm realm = Realm.getDefaultInstance()) {

            RealmResults<OrderSynchronisation> currentOrderToSyncList =
                    realm.where(OrderSynchronisation.class)
                            .equalTo("isReadyForSync", true)
                            .equalTo("isSyncComplete", false)
                            .findAll();

            realm.beginTransaction();
            boolean isError;
            for (OrderSynchronisation orderToSync : currentOrderToSyncList) {
                isError = false;

                // ZIP with XMLs
                if (orderToSync.getZipFileWithXMLs() != null && !orderToSync.isZipFileWithXMLsSynced()) {

                    uploadResult = uploadFile(orderToSync.getZipFileWithXMLs(), MEDIA_TYPE_OCTET_STREAM, "XML_ZIP_REPORT", orderToSync.getNumber());

                    if (uploadResult) {
                        orderToSync.setZipFileWithXMLsSynced(true);
                    } else {
                        orderToSync.setZipFileWithXMLsSynced(false);
                        isError = true;
                    }
                }

                // PDF Default report
                if (orderToSync.getDefaultPDFReportFile() != null && !orderToSync.isDefaultPDFReportFileSynced()) {

                    uploadResult = uploadFile(orderToSync.getDefaultPDFReportFile(), MEDIA_TYPE_PDF, "DEFAULT_PDF_REPORT", orderToSync.getNumber());

                    if (uploadResult) {
                        orderToSync.setDefaultPDFReportFileSynced(true);
                    } else {
                        orderToSync.setDefaultPDFReportFileSynced(false);
                        isError = true;
                    }
                }

                // LMRA Photos
                if (!orderToSync.isLMRAPhotosSynced()) {
                    final RealmResults<LMRAPhoto> listLMRAPhotosInBD =
                            realm.where(LMRAPhoto.class)
                                    .equalTo("number", orderToSync.getNumber())
                                    .equalTo("lmraPhotoFileSynced", false)
                                    .findAll();
                    for (LMRAPhoto lmraPhoto : listLMRAPhotosInBD) {

                        uploadResult = uploadFile(lmraPhoto.getLmraPhotoFile(), MEDIA_TYPE_JPEG, "LMRA_PHOTO", orderToSync.getNumber());

                        if (uploadResult) {
                            lmraPhoto.setLmraPhotoFileSynced(true);
                        } else {
                            lmraPhoto.setLmraPhotoFileSynced(false);
                            isError = true;
                        }
                    }

                    // Check if all photos synced
                    final RealmResults<LMRAPhoto> listLMRAPhotosNotSyncedInBD =
                            realm.where(LMRAPhoto.class)
                                    .equalTo("number", orderToSync.getNumber())
                                    .equalTo("lmraPhotoFileSynced", false)
                                    .findAll();
                    if (listLMRAPhotosNotSyncedInBD.size() == 0) {
                        orderToSync.setLMRAPhotosSynced(true);
                    }
                }

                orderToSync.setError(isError);

                // Checking all statuses
                if (!orderToSync.isError()
                        && orderToSync.isZipFileWithXMLsSynced()
                        && orderToSync.isDefaultPDFReportFileSynced()
                        && orderToSync.isLMRAPhotosSynced()) {
                    // All done
                    orderToSync.setSyncComplete(true);
                } else {
                    orderToSync.setSyncComplete(false);
                }
            }

            realm.commitTransaction();
            AppLog.serviceI(currentTask + "Upload complete.");
        }
    }

    private void generatePartMap() {
        AppLog.serviceI(currentTask + "Service started.");

        if (Session.getPartMap().size() != 0) {
            AppLog.serviceI(currentTask + "Map already generated.");
            return;
        }

        final String jsonComponent = new JSONHelper().readFromFile(getApplicationContext(), BuildConfig.COMPONENTS_JSON);

        if (jsonComponent.isEmpty()) {
            AppLog.serviceE(true, -1, currentTask + "Error generating map: Empty JSON.");
            return;
        }

        final JsonParser jsonParser = new JsonParser();
        final Map<String, LinkedHashMap<String, JsonObject>> partMap = Session.getPartMap();
        partMap.clear();

        try {
            JsonElement parentElement = jsonParser.parse(jsonComponent);
            JsonArray parentArray = parentElement.getAsJsonArray();
            for (int k = 0; k < parentArray.size(); k++) {
                JsonObject secondObject = parentArray.get(k).getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = secondObject.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    JsonArray thirdArray = entry.getValue().getAsJsonArray();
                    LinkedHashMap<String, JsonObject> elementMap = new LinkedHashMap<>();
                    for (int j = 0; j < thirdArray.size(); j++) {
                        JsonObject thirdObject = thirdArray.get(j).getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySetSecond = thirdObject.entrySet();
                        for (Map.Entry<String, JsonElement> entrySecond : entrySetSecond) {
                            elementMap.put(entrySecond.getKey(), entrySecond.getValue().getAsJsonObject());
                        }
                    }
                    partMap.put(entry.getKey(), elementMap);
                }
            }
        } catch (Exception e) {
            AppLog.serviceE(true, -1, currentTask + "Error generating map: " + e.toString());
        }

        Session.setPartMap(partMap);
        AppLog.serviceI(currentTask + "Map generated.");

    }

}
