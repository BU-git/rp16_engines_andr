package com.bionic.kvt.serviceapp;

import android.app.Application;

import com.bionic.kvt.serviceapp.api.ServiceConnection;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Session extends Application {
    private static Session currentUserSession;

    private ServiceConnection serviceConnection;
    private List<String> sessionLog;
    private boolean isSyncingFromServer = false;

    private String engineerName;
    private String engineerEmail;
    private String engineerId;
    private long currentOrder;
    private List<OrderOverview> orderOverviewList;

    public static final int ORDER_STATUS_NOT_STARTED = 0;
    public static final int ORDER_STATUS_IN_PROGRESS = 1;
    public static final int ORDER_STATUS_COMPLETE = 2;

    public static final int ORDER_OVERVIEW_COLUMN_COUNT = 7;

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserSession = this;
        sessionLog = new ArrayList<>();
        orderOverviewList = new ArrayList<>();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACK_OFFICE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serviceConnection = retrofit.create(ServiceConnection.class);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(BuildConfig.DB_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static ServiceConnection getServiceConnection() {
        return currentUserSession.serviceConnection;
    }

    public static void clearSession() {
        currentUserSession.engineerName = null;
        currentUserSession.engineerEmail = null;
        currentUserSession.engineerId = null;
        currentUserSession.currentOrder = 0L;
        currentUserSession.orderOverviewList.clear();
    }

    public static List<String> getSessionLog() {
        return currentUserSession.sessionLog;
    }

    public static void addToSessionLog(String message) {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
                .format(Calendar.getInstance().getTime());
        currentUserSession.sessionLog.add("[" + dateTime + "](" + currentUserSession.engineerEmail + ") " + message);
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
    }

    public static long getCurrentOrder() {
        return currentUserSession.currentOrder;
    }

    public static List<OrderOverview> getOrderOverviewList() {
        return currentUserSession.orderOverviewList;
    }
}
