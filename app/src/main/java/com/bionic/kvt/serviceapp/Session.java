package com.bionic.kvt.serviceapp;

import android.app.Application;

import com.bionic.kvt.serviceapp.api.OrderServiceConnection;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Session extends Application {
    private static Session currentUserSession;

    private OrderServiceConnection orderServiceConnection;
    private ArrayList<String> sessionLog;
    private boolean isSyncingFromServer = false;

    private String engineerName;
    private String engineerEmail;
    private String engineerId;
    private long currentOrderNumber;


    public static final int ORDER_STATUS_NOT_STARTED = 0;
    public static final int ORDER_STATUS_IN_PROGRESS = 1;
    public static final int ORDER_STATUS_COMPLETE = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserSession = this;
        sessionLog = new ArrayList<>();

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
        currentUserSession.currentOrderNumber = 0L;
//        orderNumber = 0L;
//        orderStatus = null;
//        checkBoxInstructions = false;
//        checkBoxLMRA = false;
    }

    public static ArrayList<String> getSessionLog() {
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

    public static void setCurrentOrderNumber(long orderNumber) {
//        checkBoxInstructions = false;
//        checkBoxLMRA = false;
        currentUserSession.currentOrderNumber = orderNumber;
    }

    public static long getCurrentOrderNumber() {
        return currentUserSession.currentOrderNumber;
    }


    //ALL DOWN IS FOR TEST
    public static final List<String[]> ordersDataSet = new LinkedList<>();
    public static int ordersDataSetColNumber;


    static {
        ordersDataSet.add(new String[]{"826547892", "29-06-2016", "Generator", "Repair", "Kiev", "Not started", "PDF"});
        ordersDataSet.add(new String[]{"354323678", "19-03-2016", "Motor", "Check", "Lviv", "Not started", "PDF"});
        ordersDataSet.add(new String[]{"308197851", "19-03-2016", "Motor", "Check", "Donetsk", "Not started", "PDF"});
        ordersDataSet.add(new String[]{"354363467", "19-03-2016", "Motor", "Check", "Lviv", "In progress", "PDF"});
        ordersDataSet.add(new String[]{"276836365", "19-03-2016", "Motor", "Check", "Kharkiv", "Not started", "PDF"});
        ordersDataSet.add(new String[]{"375844315", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354398595", "19-03-2016", "Generator", "Check", "Very looooong adresss ", "Not started", "PDF"});
        ordersDataSet.add(new String[]{"256738589", "19-03-2016", "Motor", "Check", "Kharkiv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354543622", "19-03-2016", "Generator", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354267990", "19-03-2016", "Motor", "Repair", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"323566436", "19-03-2016", "Motor", "Check", "Kharkiv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"516665789", "19-03-2016", "Motor", "Repair", "Dnipro", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"851567939", "19-03-2016", "Generator", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"350954613", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"95153w508", "19-03-2016", "Generator", "Repair", "Odessa", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"154234525", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"045767689", "02-11-2016", "Generator", "Repair", "Odessa", "Completed", "PDF"});

        ordersDataSetColNumber = ordersDataSet.get(0).length;
    }


    private String orderStatus;
    private boolean checkBoxInstructions;
    private boolean checkBoxLMRA;


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

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
