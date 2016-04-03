package com.bionic.kvt.serviceapp;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements session handling
 */
public class Session extends Application {

    public static final List<String[]> ordersDataSet = new LinkedList<>();
    public static int ordersDataSetColNumber;

    static {
        ordersDataSet.add(new String[]{"123456789", "29-06-2016", "Generator", "Repair", "Kiev", "In progress", "PDF"});
        ordersDataSet.add(new String[]{"354323678", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"308197851", "19-03-2016", "Motor", "Check", "Donetsk", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354363467", "19-03-2016", "Motor", "Check", "Lviv", "In progress", "PDF"});
        ordersDataSet.add(new String[]{"276836365", "19-03-2016", "Motor", "Check", "Kharkiv", "Not starte", "PDF"});
        ordersDataSet.add(new String[]{"375844315", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354398595", "19-03-2016", "Generator", "Check", "Very looooong adresss ", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"256738589", "19-03-2016", "Motor", "Check", "Kharkiv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"354543622", "19-03-2016", "Generator", "Check", "Lviv", "In progress", "PDF"});
        ordersDataSet.add(new String[]{"354267990", "19-03-2016", "Motor", "Repair", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"323566436", "19-03-2016", "Motor", "Check", "Kharkiv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"516665789", "19-03-2016", "Motor", "Repair", "Dnipro", "Not starte", "PDF"});
        ordersDataSet.add(new String[]{"851567939", "19-03-2016", "Generator", "Check", "Lviv", "Not starte", "PDF"});
        ordersDataSet.add(new String[]{"350954613", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"95153w508", "19-03-2016", "Generator", "Repair", "Odessa", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"154234525", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"});
        ordersDataSet.add(new String[]{"045767689", "02-11-2016", "Generator", "Repair", "Odessa", "Not started", "PDF"});

        ordersDataSetColNumber = ordersDataSet.get(0).length;
    }

    private String engineerName;
    private String engineerId;
    private String orderNumber;

    public void clearSession() {
        engineerName = null;
        engineerId = null;
        orderNumber = null;
    }

    public void clearOrderNumber() {
        orderNumber = null;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
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
}
