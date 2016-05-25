package com.bionic.kvt.serviceapp;

import android.app.Application;

import com.bionic.kvt.serviceapp.api.ConnectionServiceAPI;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton for Application initialisation and store user session data.
 */

public class Session extends Application {
    private static Session currentUserSession;

    // Setting timeout for network requests
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS);

    // Setting Back Office server. See {@link build.gradle} for actual address
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BACK_OFFICE_HOST)
            .addConverterFactory(GsonConverterFactory.create());

    // Map for Engine components.
    // Generated once in runtime in {@link BackgroundService}
    // and depend on current Application language.
    private static Map<String, LinkedHashMap<String, JsonObject>> partMap;

    // Store defect state for current order
    private List<DefectState> defectStateList;

    private ConnectionServiceAPI connectionServiceAPI;
    private File currentAppInternalPrivateDir;
    private File currentAppExternalPrivateDir;

    private String engineerName;
    private String engineerEmail;
    private long currentOrder;

    // Used to store signature bitmaps
    private byte[] byteArrayEngineerSignature;
    private byte[] byteArrayClientSignature;

    // Realm configuration for application log
    private RealmConfiguration logConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserSession = this;

        partMap = new LinkedHashMap<>();
        defectStateList = new ArrayList<>();

        currentAppInternalPrivateDir = new File(getFilesDir(), "orders");
        currentAppExternalPrivateDir = getApplicationContext().getExternalFilesDir("");

        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        connectionServiceAPI = retrofit.create(ConnectionServiceAPI.class);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(BuildConfig.DB_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        // Realm config for log DB
        logConfig = new RealmConfiguration.Builder(this)
                .name("kvtLog.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        AppLog.initLog();
    }

    public static Realm getLogRealm() {
        return Realm.getInstance(currentUserSession.logConfig);
    }

    public static ConnectionServiceAPI getServiceConnection() {
        return currentUserSession.connectionServiceAPI;
    }

    public static void clearSession() {
        currentUserSession.engineerName = null;
        currentUserSession.engineerEmail = null;
        currentUserSession.currentOrder = 0L;
        currentUserSession.defectStateList.clear();
        currentUserSession.byteArrayEngineerSignature = null;
        currentUserSession.byteArrayClientSignature = null;
    }

    public static void clearCurrentOrder() {
        currentUserSession.currentOrder = 0L;
        currentUserSession.byteArrayEngineerSignature = null;
        currentUserSession.byteArrayClientSignature = null;
    }

    public static Map<String, LinkedHashMap<String, JsonObject>> getPartMap() {
        return partMap;
    }

    public static void setPartMap(Map<String, LinkedHashMap<String, JsonObject>> partMap) {
        Session.partMap = partMap;
    }

    public static String getEngineerName() {
        return currentUserSession.engineerName;
    }

    public static void setEngineerName(String engineerName) {
        currentUserSession.engineerName = engineerName;
    }

    public static String getEngineerEmail() {
        return currentUserSession.engineerEmail;
    }

    public static void setEngineerEmail(String engineerEmail) {
        currentUserSession.engineerEmail = engineerEmail;
    }

    public static long getCurrentOrder() {
        return currentUserSession.currentOrder;
    }

    public static void setCurrentOrder(long order) {
        currentUserSession.currentOrder = order;
    }

    public static File getCurrentAppDir() {
        return currentUserSession.currentAppInternalPrivateDir;
    }

    public static File getAppExternalPrivateDir() {
        return currentUserSession.currentAppExternalPrivateDir;
    }

    public static byte[] getByteArrayEngineerSignature() {
        return currentUserSession.byteArrayEngineerSignature;
    }

    public static void setByteArrayEngineerSignature(byte[] byteArrayEngineerSignature) {
        currentUserSession.byteArrayEngineerSignature = byteArrayEngineerSignature;
    }

    public static byte[] getByteArrayClientSignature() {
        return currentUserSession.byteArrayClientSignature;
    }

    public static void setByteArrayClientSignature(byte[] byteArrayClientSignature) {
        currentUserSession.byteArrayClientSignature = byteArrayClientSignature;
    }

    public static List<DefectState> getDefectStateList() {
        return currentUserSession.defectStateList;
    }

}
