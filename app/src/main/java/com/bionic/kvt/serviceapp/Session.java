package com.bionic.kvt.serviceapp;

import android.app.Application;

import com.bionic.kvt.serviceapp.api.OrderServiceApi;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Session extends Application {

    private static Session currentUserSession;
    private OrderServiceApi orderServiceApi;

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserSession = this;

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BACK_OFFICE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        orderServiceApi = retrofit.create(OrderServiceApi.class);
    }

    public static OrderServiceApi orderServiceApi() {
        return currentUserSession.orderServiceApi;
    }

    public static Session getSession() {
        return currentUserSession;
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

    private String engineerName;
    private String engineerId;
    private Long orderNumber;
    private String orderStatus;
    private boolean checkBoxInstructions;
    private boolean checkBoxLMRA;

    public void clearSession() {
        engineerName = null;
        engineerId = null;
        orderNumber = null;
        orderStatus = null;
        checkBoxInstructions = false;
        checkBoxLMRA = false;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void clearOrderNumber() {
        orderNumber = null;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        checkBoxInstructions = false;
        checkBoxLMRA = false;
        this.orderNumber = orderNumber;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }

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
