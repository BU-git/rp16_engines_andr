package com.bionic.kvt.serviceapp;

import android.app.Application;

import com.bionic.kvt.serviceapp.api.OrderServiceConnection;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.text.ParseException;
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

    private OrderServiceConnection orderServiceConnection;
    private List<String> sessionLog;
    private boolean isSyncingFromServer = false;

    private String engineerName;
    private String engineerEmail;
    private String engineerId;
    private Order currentOrder;
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

        orderServiceConnection = retrofit.create(OrderServiceConnection.class);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(BuildConfig.DB_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static OrderServiceConnection getOrderServiceConnection() {
        return currentUserSession.orderServiceConnection;
    }

    public static void clearSession() {
        currentUserSession.engineerName = null;
        currentUserSession.engineerEmail = null;
        currentUserSession.engineerId = null;
        currentUserSession.currentOrder = null;

        currentUserSession.sessionLog.clear();
        currentUserSession.orderOverviewList.clear();
//        orderStatus = null;
//        checkBoxInstructions = false;
//        checkBoxLMRA = false;
    }

    public static List<String> getSessionLog() {
        return currentUserSession.sessionLog;
    }

    public static void addToSessionLog(String message) {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
                .format(Calendar.getInstance().getTime());
        currentUserSession.sessionLog.add("[" + dateTime + "] " + message);
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

    public static void setCurrentOrder(Order order) {
//        checkBoxInstructions = false;
//        checkBoxLMRA = false;
        currentUserSession.currentOrder = order;
    }

    public static Order getCurrentOrder() {
        return currentUserSession.currentOrder;
    }

    public static List<OrderOverview> getOrderOverviewList() {
        return currentUserSession.orderOverviewList;
    }


    // ALL DOWN IS FOR TEST

    public static void setDemoData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        OrderOverview orderOverview = new OrderOverview();
        orderOverview.setNumber(4013730);
        try {
            orderOverview.setDate(sdf.parse("2016-04-05"));
        } catch (ParseException e) {
        }
        orderOverview.setInstallationName("Viatel Doorn 1");
        orderOverview.setTaskLtxa1("Serviceonderhoud noodstroominstallatie");
        orderOverview.setInstallationAddress("Leersumsestraatweg");
        orderOverview.setOrderStatus(ORDER_STATUS_NOT_STARTED);
        orderOverview.setPdfString("PDF");
        currentUserSession.orderOverviewList.add(orderOverview);

        OrderOverview orderOverview2 = new OrderOverview();
        orderOverview2.setNumber(4014137);
        try {
            orderOverview2.setDate(sdf.parse("2016-04-06"));
        } catch (ParseException e) {
        }
        orderOverview2.setInstallationName("Petrochemical Pipeline Services");
        orderOverview2.setTaskLtxa1("Belast beproeven extern");
        orderOverview2.setInstallationAddress("Sanderboutlaan");
        orderOverview2.setOrderStatus(ORDER_STATUS_NOT_STARTED);
        orderOverview2.setPdfString("PDF");
        currentUserSession.orderOverviewList.add(orderOverview2);
    }

    private boolean checkBoxInstructions;
    private boolean checkBoxLMRA;


//    public void clearOrderNumber() {
//        orderNumber = 0L;
//        orderStatus = null;
//        checkBoxInstructions = false;
//        checkBoxLMRA = false;
//    }


    public boolean isCheckBoxInstructions() {
        return checkBoxInstructions;
    }

    public void setCheckBoxInstructions(boolean checkBoxInstructions) {
        this.checkBoxInstructions = checkBoxInstructions;
    }

    public boolean isCheckBoxLMRA() {
        return checkBoxLMRA;
    }

    public void setCheckBoxLMRA(boolean checkBoxLMRA) {
        this.checkBoxLMRA = checkBoxLMRA;
    }

}
