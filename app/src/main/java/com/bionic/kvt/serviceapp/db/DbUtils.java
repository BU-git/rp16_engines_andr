package com.bionic.kvt.serviceapp.db;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DbUtils {

    // Completely erase User table and add Demo user
    public static void resetUserTable() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Resetting User table.");

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.clear(User.class);
        User user = realm.createObject(User.class);
        user.setName("Demo User");
        user.setEmail("demo@kvt.nl");
        user.setPassword("demo");
        user.setOnServer(true);
        realm.commitTransaction();

        realm.close();
    }

    // Completely erase Order Table and all sub tables
    public static void resetOrderTableWithSubTables() {
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

    public static void createTableIfNotExist(Class realmClass) {
        final Realm realm = Realm.getDefaultInstance();
        if (realm.where(realmClass).findAll().size() == 0) {
            if (realmClass == User.class) {
                resetUserTable();
            }
        }
        realm.close();
    }

    public static void updateOrderOverviewList() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Updating Order Overview List");

        final Realm realm = Realm.getDefaultInstance();
        final List<Order> allOrdersInDb = realm.where(Order.class).findAll();

        Session.getOrderOverviewList().clear();
        for (Order order : allOrdersInDb) {
            final OrderOverview orderOverview = new OrderOverview();
            orderOverview.setNumber(order.getNumber());
            orderOverview.setDate(order.getDate());
            orderOverview.setInstallationName(order.getInstallation().getName());
            orderOverview.setTaskLtxa1(order.getTasks().first().getLtxa1());
            orderOverview.setInstallationAddress(order.getInstallation().getAddress());
            orderOverview.setOrderStatus(order.getOrderStatus());
            orderOverview.setPdfString("PDF");

            Session.getOrderOverviewList().add(orderOverview);
        }

        realm.close();
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Added " + Session.getOrderOverviewList().size() + " orders.");
    }

    public static List<OrderBrief> getOrdersToBeUpdated(final List<OrderBrief> serverOrderBriefList) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Looking for orders to be updated.");

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

    private static boolean isOrderNewerOnServer(Order orderInDb, OrderBrief orderBrief) {
        if (orderInDb.getImportDate()
                .compareTo(orderBrief.getImportDate()) != 0)
            return true;

        if (orderInDb.getLastServerChangeDate()
                .compareTo(orderBrief.getLastServerChangeDate()) != 0)
            return true;

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

            realm.close();
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + "done.");
        }

        if (currentOrderInDB != null) { // Existing order
            if (currentOrderInDB.getOrderStatus() == Session.ORDER_STATUS_NOT_STARTED) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Deleting order: " + currentOrderInDB.getNumber());
                realm.beginTransaction();
                currentOrderInDB.removeFromRealm();
                realm.commitTransaction(); // No logic if transaction fail!!!
                createNewOrderInDb(serverOrder);

                realm.close();
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + "done.");
            }

            if (currentOrderInDB.getOrderStatus() == Session.ORDER_STATUS_IN_PROGRESS) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("*** WARRING ***: Cannot update order in status IN_PROGRESS. Order #"
                            + currentOrderInDB.getNumber());
            }

            if (currentOrderInDB.getOrderStatus() == Session.ORDER_STATUS_COMPLETE) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("*** ERROR ***: Cannot update order in status COMPLETE. Order #"
                            + currentOrderInDB.getNumber());
            }
        }
    }

    private static void createNewOrderInDb(com.bionic.kvt.serviceapp.api.Order serverOrder) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Creating order: " + serverOrder.getNumber());

        final Realm realm = Realm.getDefaultInstance();
        final Gson gson = new Gson();
        realm.beginTransaction();

        final Order newOrder = realm.createObject(Order.class);

        newOrder.setOrderType(serverOrder.getOrderType());
        newOrder.setDate(serverOrder.getDate());
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

        newOrder.setImportDate(serverOrder.getImportDate());
        newOrder.setLastServerChangeDate(serverOrder.getLastServerChangeDate());
        newOrder.setLastAndroidChangeDate(serverOrder.getLastAndroidChangeDate());

        newOrder.setOrderStatus(Session.ORDER_STATUS_NOT_STARTED); // ???????????????????????

        realm.commitTransaction(); // No logic if transaction fail!!!

        realm.close();
    }

    public static int updateUserTableFromServer(final List<com.bionic.kvt.serviceapp.api.User> serverUserList) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Updating User table from server data");

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<User> allCurrentUsers = realm.where(User.class).findAll();

        // Set all current users in DB not on server
        realm.beginTransaction();
        for (User userInDb : allCurrentUsers) {
            userInDb.setOnServer(false);
        }
        realm.where(User.class).equalTo("email", "demo@kvt.nl").findAll().get(0).setOnServer(true);
        realm.commitTransaction(); //No logic if transaction fail!!!

        // Updating users in DB
        realm.beginTransaction();
        for (com.bionic.kvt.serviceapp.api.User userOnServer : serverUserList) {
            // Searching for user in DB
            RealmResults<User> getUserInDb = realm.where(User.class)
                    .equalTo("email", userOnServer.getEmail())
                    .findAll();

            if (getUserInDb.size() == 1) { // We have this user on DB, updating it
                User thisUser = getUserInDb.get(0);
                thisUser.setName(userOnServer.getName());
                thisUser.setPassword(userOnServer.getPassword());
                thisUser.setOnServer(true);
            } else { // New user, creating it in DB
                User newUser = realm.createObject(User.class);
                newUser.setName(userOnServer.getName());
                newUser.setEmail(userOnServer.getEmail());
                newUser.setPassword(userOnServer.getPassword());
                newUser.setOnServer(true);
            }
        }
        realm.commitTransaction(); //No logic if transaction fail!!!

        // Returning updated in DB User count;
        final int count = realm.where(User.class).equalTo("isOnServer", true).findAll().size();
        realm.close();
        return count;
    }

    public static boolean isUserLoginValid(final String email, final String password) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("Validating user: " + email + " : " + password);

        Realm realm = Realm.getDefaultInstance();
        boolean res = realm.where(User.class)
                .equalTo("email", email)
                .equalTo("password", password)
                .findAll()
                .size() == 1;
        realm.close();
        return res;
    }

    public static void setUserSession(String email) {
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
}
