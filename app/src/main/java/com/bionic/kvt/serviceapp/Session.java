package com.bionic.kvt.serviceapp;

import android.app.Application;
import android.support.annotation.Nullable;

import com.bionic.kvt.serviceapp.api.ConnectionServiceAPI;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Session extends Application {
    private static Session currentUserSession;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BACK_OFFICE_HOST)
            .addConverterFactory(GsonConverterFactory.create());

    private ConnectionServiceAPI connectionServiceAPI;
    private List<String> sessionLog;
    private boolean isSyncingFromServer = false;

    private File currentAppExternalPrivateDir;
    private File currentOrderDir;

    private String engineerName;
    private String engineerEmail;
    private String engineerId;
    private long currentOrder;
    private List<OrderOverview> orderOverviewList;

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserSession = this;
        sessionLog = new ArrayList<>();
        orderOverviewList = new ArrayList<>();
        currentAppExternalPrivateDir = getApplicationContext().getExternalFilesDir("");

        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        connectionServiceAPI = retrofit.create(ConnectionServiceAPI.class);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(BuildConfig.DB_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static ConnectionServiceAPI getServiceConnection() {
        return currentUserSession.connectionServiceAPI;
    }

    public static void clearSession() {
        currentUserSession.engineerName = null;
        currentUserSession.engineerEmail = null;
        currentUserSession.engineerId = null;
        currentUserSession.currentOrder = 0L;
        currentUserSession.orderOverviewList.clear();
        currentUserSession.currentOrderDir = null;
    }

    public static void clearCurrentOrder() {
        currentUserSession.currentOrder = 0L;
        currentUserSession.currentOrderDir = null;
    }

    public static List<String> getSessionLog() {
        return currentUserSession.sessionLog;
    }

    public static void addToSessionLog(String message) {
        currentUserSession.sessionLog.add("[" + Utils.getDateTimeStringFromDate(new Date()) + "](" + currentUserSession.engineerEmail + ") " + message);
    }

    public static boolean isSyncingFromServer() {
        return currentUserSession.isSyncingFromServer;
    }

    public static void setIsSyncingFromServer(boolean state) {
        currentUserSession.isSyncingFromServer = state;
    }

    public static void setEngineerName(String engineerName) {
        currentUserSession.engineerName = engineerName;
    }

    public static String getEngineerName() {
        return currentUserSession.engineerName;
    }

    public static void setEngineerEmail(String engineerEmail) {
        currentUserSession.engineerEmail = engineerEmail;
        currentUserSession.engineerId = Utils.getUserIdFromEmail(engineerEmail);
    }

    public static String getEngineerEmail() {
        return currentUserSession.engineerEmail;
    }

    public static String getEngineerId() {
        return currentUserSession.engineerId;
    }

    public static void setCurrentOrder(long order) {
        currentUserSession.currentOrder = order;
        currentUserSession.currentOrderDir =
                new File(currentUserSession.currentAppExternalPrivateDir,
                        "" + currentUserSession.currentOrder);
    }

    public static long getCurrentOrder() {
        return currentUserSession.currentOrder;
    }

    public static List<OrderOverview> getOrderOverviewList() {
        return currentUserSession.orderOverviewList;
    }

    public static File getCurrentAppExternalPrivateDir() {
        return currentUserSession.currentAppExternalPrivateDir;
    }

    // Do not use directly! Run Utils.getCurrentOrderDir() instead
    @Nullable
    public static File getCurrentOrderDir() {
        return currentUserSession.currentOrderDir;
    }
}
