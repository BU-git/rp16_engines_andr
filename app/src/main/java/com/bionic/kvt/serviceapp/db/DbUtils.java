package com.bionic.kvt.serviceapp.db;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_END_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_START_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_IN_PROGRESS;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_NOT_STARTED;

public class DbUtils {

    // Completely erase all database
    public static void dropDatabase() {
        final Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
            //Realm file has been deleted.
        } catch (Exception ex) {
            ex.printStackTrace();
            //No Realm file to remove.
        }
        realm.close();
    }

    // Completely erase User table and add Demo user
    public static void resetUserTable() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Resetting User table.");

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.clear(User.class);
        User user = realm.createObject(User.class);
        user.setName("Demo User");
        user.setEmail("demo@kvt.nl");
        user.setPasswordHash("ae820b72d36942625b345ec26070073e82a6f0054b2b1d0320561147653d5abe");
        user.setSalt("");
        realm.commitTransaction();

        realm.close();
    }

    // Completely erase Order Table and all sub tables
    public static void resetOrderTableWithSubTables() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Resetting Order table.");

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.clear(Order.class);
        realm.clear(Component.class);
        realm.clear(Employee.class);
        realm.clear(Info.class);
        realm.clear(Installation.class);
        realm.clear(Part.class);
        realm.clear(Relation.class);
        realm.clear(Task.class);
        realm.commitTransaction();

        realm.close();
    }

    public static void createUserTableIfNotExist() {
        final Realm realm = Realm.getDefaultInstance();
        if (realm.where(User.class).findAll().size() == 0) {
            resetUserTable();
        }
        realm.close();
    }

    public static void deleteUser(final String email) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final User user = realm.where(User.class).equalTo("email", email).findFirst();
        if (user != null) {
            user.removeFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void updateOrderOverviewList() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Updating Order Overview List");

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Order> allOrdersInDb =
                realm.where(Order.class).equalTo("employeeEmail", Session.getEngineerEmail()).findAll();
        allOrdersInDb.sort("number");

        Session.getOrderOverviewList().clear();
        for (Order order : allOrdersInDb) {
            final OrderOverview orderOverview = new OrderOverview();
            orderOverview.setNumber(order.getNumber());
            orderOverview.setDate(order.getDate());

            if (order.getInstallation() != null) {
                orderOverview.setInstallationName(order.getInstallation().getName());
                orderOverview.setInstallationAddress(order.getInstallation().getAddress());
            }
            if (order.getTasks().first() != null) {
                orderOverview.setTaskLtxa1(order.getTasks().first().getLtxa1());
            }
            orderOverview.setOrderStatus(order.getOrderStatus());
            orderOverview.setPdfString("PDF");

            Session.getOrderOverviewList().add(orderOverview);
        }

        realm.close();
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Added " + Session.getOrderOverviewList().size() + " orders to view.");
    }

    public static List<OrderBrief> getOrdersToBeUpdated(final List<OrderBrief> serverOrderBriefList) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Looking for orders to be updated.");

        final List<OrderBrief> ordersToBeUpdated = new ArrayList<>();
        final Realm realm = Realm.getDefaultInstance();

        for (OrderBrief orderBrief : serverOrderBriefList) {
            final Order orderInDb = realm
                    .where(Order.class)
                    .equalTo("number", orderBrief.getNumber())
                    .findFirst();

            if (orderInDb == null // No such order in DB
                    || isOrderNewerOnServer(orderInDb, orderBrief) // We have order in DB but it's outdated
                    ) {
                ordersToBeUpdated.add(orderBrief);
            }
        }

        realm.close();
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Found " + ordersToBeUpdated.size() + " orders to be updated.");
        return ordersToBeUpdated;
    }

    private static boolean isOrderNewerOnServer(final Order orderInDb, final OrderBrief orderBrief) {
        if (orderInDb.getImportDate()
                .compareTo(new Date(orderBrief.getImportDate())) != 0)
            return true;

        if (orderInDb.getLastServerChangeDate()
                .compareTo(new Date(orderBrief.getLastServerChangeDate())) != 0)
            return true;
//TODO WHAT WITH DONE STATUS ?
        return false;
    }

    public static void updateOrderFromServer(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Updating order from server order data: " + serverOrder.getNumber());

        final Realm realm = Realm.getDefaultInstance();

        final Order currentOrderInDB = realm
                .where(Order.class)
                .equalTo("number", serverOrder.getNumber())
                .findFirst();

        if (currentOrderInDB == null) { // New order
            createNewOrderInDb(serverOrder);

            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + " done.");
        }

        if (currentOrderInDB != null) { // Existing order
            switch (currentOrderInDB.getOrderStatus()) {
                case ORDER_STATUS_NOT_STARTED:
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("Deleting order: " + currentOrderInDB.getNumber());
                    realm.beginTransaction();
                    currentOrderInDB.removeFromRealm();
                    realm.commitTransaction(); // No logic if transaction fail!!!
                    createNewOrderInDb(serverOrder);
                    realm.close();

                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + " done.");
                    break;

                case ORDER_STATUS_IN_PROGRESS:
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("*** WARRING ***: Cannot update order in status IN_PROGRESS. Order #"
                                + currentOrderInDB.getNumber());
                    break;

                case ORDER_STATUS_COMPLETE:
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("*** ERROR ***: Cannot update order in status COMPLETE. Order #"
                                + currentOrderInDB.getNumber());
                    break;
            }
        }

    }

    private static void createNewOrderInDb(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Creating order: " + serverOrder.getNumber());

        final Realm realm = Realm.getDefaultInstance();
        final Gson gson = new Gson();
        realm.beginTransaction();

        final Order newOrder = realm.createObject(Order.class);

        newOrder.setNumber(serverOrder.getNumber());
        newOrder.setOrderType(serverOrder.getOrderType());
        newOrder.setDate(new Date(serverOrder.getDate()));
        newOrder.setReference(serverOrder.getReference());
        newOrder.setNote(serverOrder.getNote());

        // Relation
        newOrder.setRelation(realm.createObjectFromJson
                (Relation.class, gson.toJson(serverOrder.getRelation()))
        );

        // Employee
        newOrder.setEmployee(realm.createObjectFromJson
                (Employee.class, gson.toJson(serverOrder.getEmployee()))
        );

        // Installation
        newOrder.setInstallation(realm.createObjectFromJson
                (Installation.class, gson.toJson(serverOrder.getInstallation()))
        );

        // Task
        final RealmList<Task> newTaskList = new RealmList<>();
        for (com.bionic.kvt.serviceapp.api.Task serverTask : serverOrder.getTasks()) {
            newTaskList.add(realm.createObjectFromJson
                    (Task.class, gson.toJson(serverTask))
            );
        }
        newOrder.setTasks(newTaskList);

        // Component
        final RealmList<Component> newComponentList = new RealmList<>();
        for (com.bionic.kvt.serviceapp.api.Component serverComponent : serverOrder.getComponents()) {
            newComponentList.add(realm.createObjectFromJson
                    (Component.class, gson.toJson(serverComponent))
            );
        }
        newOrder.setComponents(newComponentList);

        // Part
        final RealmList<Part> newPartList = new RealmList<>();
        for (com.bionic.kvt.serviceapp.api.Part serverPart : serverOrder.getParts()) {
            newPartList.add(realm.createObjectFromJson
                    (Part.class, gson.toJson(serverPart))
            );
        }
        newOrder.setParts(newPartList);

        // Info
        final RealmList<Info> newInfoList = new RealmList<>();
        for (com.bionic.kvt.serviceapp.api.Info serverInfo : serverOrder.getExtraInfo()) {
            newInfoList.add(realm.createObjectFromJson
                    (Info.class, gson.toJson(serverInfo))
            );
        }
        newOrder.setExtraInfo(newInfoList);

        newOrder.setImportDate(new Date(serverOrder.getImportDate()));
        newOrder.setLastServerChangeDate(new Date(serverOrder.getLastServerChangeDate()));
        newOrder.setLastAndroidChangeDate(new Date(serverOrder.getLastAndroidChangeDate()));

        newOrder.setOrderStatus(ORDER_STATUS_NOT_STARTED); // TODO

        newOrder.setEmployeeEmail(serverOrder.getEmployee().getEmail());

        realm.commitTransaction(); // No logic if transaction fail!!!


        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog(newOrder.toString());
        realm.close();
    }

    public static boolean updateUserTableFromServer(final com.bionic.kvt.serviceapp.api.User serverUser) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Updating User table from server data.");

        final Realm realm = Realm.getDefaultInstance();
        // Searching for user in DB
        final User userInDb = realm.where(User.class).equalTo("email", serverUser.getEmail()).findFirst();

        realm.beginTransaction();
        if (userInDb != null) { // We have this user on DB, updating it
            userInDb.setName(serverUser.getName());
            userInDb.setEmail(serverUser.getEmail());
            userInDb.setPasswordHash(serverUser.getPasswordHash());
            userInDb.setSalt(serverUser.getSalt());
        } else { // New user, creating it in DB
            User newUser = realm.createObject(User.class);
            newUser.setName(serverUser.getName());
            newUser.setEmail(serverUser.getEmail());
            newUser.setPasswordHash(serverUser.getPasswordHash());
            newUser.setSalt(serverUser.getSalt());
        }
        realm.commitTransaction(); //No logic if transaction fail!!!
        realm.close();

        return true;
    }

    public static boolean isUserLoginValid(final String email, final String password) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Validating user: " + email + " : " + password);

//        String passwd = "";
//        try {
//            MessageDigest digester = MessageDigest.getInstance("SHA-256");
//            digester.update(password.getBytes());
//            passwd = digester.digest().toString();
//        } catch (NoSuchAlgorithmException e) {
//        }


        Realm realm = Realm.getDefaultInstance();
        boolean res = realm.where(User.class)
                .equalTo("email", email)
                .equalTo("passwordHash", password)
                .findAll()
                .size() == 1;
        realm.close();
        return res;
    }

    public static void setUserSession(final String email) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Setting user session: " + email);

        Session.clearSession();
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<User> result = realm.where(User.class).equalTo("email", email).findAll();
        if (result.size() == 1) {
            Session.setEngineerEmail(email);
            Session.setEngineerName(result.get(0).getName());
        }
        realm.close();
    }

    public static void setOrderStatus(final long orderNumber, final int status) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Setting order [" + orderNumber + "] status: " + status);

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
        if (order != null) {
            if (order.getOrderStatus() != status) {
                order.setOrderStatus(status);
                order.setLastAndroidChangeDate(new Date());
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    // Return -1 if Order not found
    public static int getOrderStatus(final long orderNumber) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Getting order [" + orderNumber + "] status.");
        int result = -1;
        final Realm realm = Realm.getDefaultInstance();
        final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
        if (order != null) result = order.getOrderStatus();
        realm.close();
        return result;
    }

    public static void setOrderReportJobRules(final OrderReportJobRules jobRules) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Saving Job Rules");

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Remove if already exist
        final OrderReportJobRules currentJobRules = realm.where(OrderReportJobRules.class)
                .equalTo("number", jobRules.getNumber()).findFirst();
        if (currentJobRules != null) currentJobRules.removeFromRealm();
        // Save new
        realm.copyToRealm(jobRules);
        realm.commitTransaction();
        realm.close();
    }

    public static void setOrderReportMeasurements(final OrderReportMeasurements measurements) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Saving Measurements");

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Remove if already exist
        final OrderReportMeasurements currentMeasurements = realm.where(OrderReportMeasurements.class)
                .equalTo("number", measurements.getNumber()).findFirst();
        if (currentMeasurements != null) currentMeasurements.removeFromRealm();
        // Save new
        realm.copyToRealm(measurements);
        realm.commitTransaction();
        realm.close();
    }

    public static void setOrderMaintenanceTime(final long orderNumber, final int timeType, final Date time) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
        if (order != null) {
            switch (timeType) {
                case ORDER_MAINTENANCE_START_TIME:
                    order.setMaintenanceStartTime(time);
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("Setting order [" + orderNumber + "] maintenance start time: " + Utils.getDateTimeStringFromDate(time));
                    break;
                case ORDER_MAINTENANCE_END_TIME:
                    order.setMaintenanceEndTime(time);
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.addToSessionLog("Setting order [" + orderNumber + "] maintenance end time: " + Utils.getDateTimeStringFromDate(time));
                    break;
            }
        }
        realm.commitTransaction();
        realm.close();
    }
}
