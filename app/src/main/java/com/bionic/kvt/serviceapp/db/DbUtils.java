package com.bionic.kvt.serviceapp.db;

import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_END_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_START_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE_UPLOADED;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_IN_PROGRESS;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_NOT_FOUND;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_NOT_STARTED;
import static com.bionic.kvt.serviceapp.GlobalConstants.OrderMaintenanceType;
import static com.bionic.kvt.serviceapp.GlobalConstants.OrderStatus;
import static com.bionic.kvt.serviceapp.GlobalConstants.PASSWORD_HASH_ITERATIONS;

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
        Session.addToSessionLog("Resetting User table.");

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.delete(User.class);
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
        Session.addToSessionLog("Resetting Order table.");

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.delete(Order.class);
        realm.delete(Component.class);
        realm.delete(Employee.class);
        realm.delete(Info.class);
        realm.delete(Installation.class);
        realm.delete(Part.class);
        realm.delete(Relation.class);
        realm.delete(Task.class);

        realm.delete(OrderReportJobRules.class);
        realm.delete(OrderReportMeasurements.class);
        realm.delete(OrderSynchronisation.class);

        realm.commitTransaction();

        realm.close();
    }

    public static void deleteUser(final String email) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final User user = realm.where(User.class).equalTo("email", email).findFirst();
        if (user != null) {
            user.deleteFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void updateOrderOverviewList(List<OrderOverview> listToUpdate) {
        Session.addToSessionLog("Updating Order Overview List");

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Order> allOrdersInDb =
                realm.where(Order.class).equalTo("employeeEmail", Session.getEngineerEmail()).findAll();
        final RealmResults<Order> allOrdersInDbSorted = allOrdersInDb.sort("number");

        listToUpdate.clear();
        for (Order order : allOrdersInDbSorted) {
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

            listToUpdate.add(orderOverview);
        }

        realm.close();
        Session.addToSessionLog("Added " + listToUpdate.size() + " orders to view.");
    }

    public static List<Long> getOrdersToBeUpdated(final List<OrderBrief> serverOrderBriefList) {
        Session.addToSessionLog("Looking for orders to be updated.");

        final List<Long> ordersToBeUpdated = new ArrayList<>();
        final Realm realm = Realm.getDefaultInstance();

        for (OrderBrief orderBrief : serverOrderBriefList) {
            Order orderInDb = realm
                    .where(Order.class)
                    .equalTo("number", orderBrief.getNumber())
                    .findFirst();

            if (orderInDb == null) { // No such order in DB
                ordersToBeUpdated.add(orderBrief.getNumber());
                continue;
            }

            if (orderInDb.getOrderStatus() == ORDER_STATUS_NOT_STARTED
                    && isOrderNewerOnServer(orderInDb, orderBrief)) { // We have order in DB but it's outdated
                ordersToBeUpdated.add(orderBrief.getNumber());
            }
        }

        realm.close();
        Session.addToSessionLog("Found " + ordersToBeUpdated.size() + " orders to be updated.");
        return ordersToBeUpdated;
    }

    public static List<Long> getOrdersToBeUploaded() {
        Session.addToSessionLog("Looking for orders to be uploaded.");

        final List<Long> ordersToBeUploaded = new ArrayList<>();
        final Realm realm = Realm.getDefaultInstance();

        final RealmResults<Order> completeOrdersInDb = realm
                .where(Order.class)
                .equalTo("orderStatus", ORDER_STATUS_COMPLETE)
                .findAll();

        for (Order order : completeOrdersInDb) {
            ordersToBeUploaded.add(order.getNumber());
        }

        realm.close();
        Session.addToSessionLog("Found " + ordersToBeUploaded.size() + " orders to be uploaded.");
        return ordersToBeUploaded;
    }

    private static boolean isOrderNewerOnServer(final Order orderInDb, final OrderBrief orderBrief) {
        if (orderInDb.getImportDate()
                .compareTo(new Date(orderBrief.getImportDate())) != 0)
            return true;

        if (orderInDb.getLastServerChangeDate()
                .compareTo(new Date(orderBrief.getLastServerChangeDate())) != 0)
            return true;

        return false;
    }

    private static void removeOrderFromDB(final long orderNumber) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Order.class).equalTo("number", orderNumber).findFirst().deleteFromRealm();
        realm.where(OrderReportJobRules.class).equalTo("number", orderNumber).findFirst().deleteFromRealm();
        realm.where(OrderReportMeasurements.class).equalTo("number", orderNumber).findFirst().deleteFromRealm();

        realm.commitTransaction(); // No logic if transaction fail!!!
        realm.close();

        //TODO Do we need to remove files?
    }

    public static void updateOrderFromServer(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        Session.addToSessionLog("Updating order from server order data: " + serverOrder.getNumber());

        final Realm realm = Realm.getDefaultInstance();
        final Order currentOrderInDB = realm
                .where(Order.class)
                .equalTo("number", serverOrder.getNumber())
                .findFirst();

        if (currentOrderInDB == null) { // New order
            createNewOrderInDb(serverOrder);
            Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + " done.");
        }

        if (currentOrderInDB != null) { // Existing order
            switch (currentOrderInDB.getOrderStatus()) {
                case ORDER_STATUS_NOT_STARTED:
                    Session.addToSessionLog("Deleting order: " + currentOrderInDB.getNumber());
                    removeOrderFromDB(currentOrderInDB.getNumber());
                    createNewOrderInDb(serverOrder);
                    Session.addToSessionLog("Update order table from server order " + serverOrder.getNumber() + " done.");
                    break;

                case ORDER_STATUS_IN_PROGRESS:
                    Session.addToSessionLog("*** WARRING ***: Cannot update order in status IN_PROGRESS. Order #"
                            + currentOrderInDB.getNumber());
                    break;

                case ORDER_STATUS_COMPLETE:
                    Session.addToSessionLog("*** ERROR ***: Cannot update order in status COMPLETE. Order #"
                            + currentOrderInDB.getNumber());
                    break;
                case ORDER_STATUS_COMPLETE_UPLOADED:
                    Session.addToSessionLog("*** ERROR ***: Cannot update order in status COMPLETE_UPLOADED. Order #"
                            + currentOrderInDB.getNumber());
                    break;
                case ORDER_STATUS_NOT_FOUND:
                    break;
            }
        }
        realm.close();

    }

    private static void createNewOrderInDb(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
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
        newOrder.setCustomTemplateID(serverOrder.getCustomTemplateID());
        newOrder.setOrderStatus(serverOrder.getOrderStatus());

        newOrder.setMaintenanceStartTime(new Date(0));
        newOrder.setMaintenanceEndTime(new Date(0));

        newOrder.setEmployeeEmail(serverOrder.getEmployee().getEmail());

        realm.commitTransaction(); // No logic if transaction fail!!!
        Session.addToSessionLog(newOrder.toString());
        realm.close();
    }

    public static void updateUserFromServer(final com.bionic.kvt.serviceapp.api.User serverUser) {
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
    }

    public static boolean isUserLoginValid(final String email, final String password) {
        Session.addToSessionLog("Validating user: " + email);

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<User> usersInDB = realm.where(User.class).equalTo("email", email).findAll();
        if (usersInDB.size() != 1) {
            realm.close();
            return false;
        }

        final MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Session.addToSessionLog("NoSuchAlgorithmException (SHA-256): " + e.toString());
            realm.close();
            return false;
        }

        final String saltInDB = usersInDB.first().getSalt();
        final String passwordHashInDB = usersInDB.first().getPasswordHash();
        realm.close();

        if (passwordHashInDB == null) return false;

        byte[] hash = (password + saltInDB).getBytes();
        for (int i = 0; i <= PASSWORD_HASH_ITERATIONS; i++) {
            digester.update(hash);
            hash = digester.digest();
        }

        return passwordHashInDB.equals(Utils.convertByteArrayToHexString(hash));
    }

    public static void setUserSession(final String email) {
        Session.addToSessionLog("Setting user session: " + email);

        Session.clearSession();
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<User> result = realm.where(User.class).equalTo("email", email).findAll();
        if (result.size() == 1) {
            Session.setEngineerEmail(email);
            Session.setEngineerName(result.get(0).getName());
        }
        realm.close();
    }

    public static void setOrderStatus(final long orderNumber, @OrderStatus final int status) {
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

    public static @OrderStatus int getOrderStatus(final long orderNumber) {
        Session.addToSessionLog("Getting order [" + orderNumber + "] status.");
        int result = ORDER_STATUS_NOT_FOUND;
        final Realm realm = Realm.getDefaultInstance();
        final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
        if (order != null) result = order.getOrderStatus();
        realm.close();
        return result;
    }

    public static void setOrderReportJobRules(final OrderReportJobRules jobRules) {
        Session.addToSessionLog("Saving Job Rules");

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Remove if already exist
        final OrderReportJobRules currentJobRules = realm.where(OrderReportJobRules.class)
                .equalTo("number", jobRules.getNumber()).findFirst();
        if (currentJobRules != null) currentJobRules.deleteFromRealm();
        // Save new
        realm.copyToRealm(jobRules);
        realm.commitTransaction();
        realm.close();
    }

    public static void setOrderReportMeasurements(final OrderReportMeasurements measurements) {
        Session.addToSessionLog("Saving Measurements");

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Remove if already exist
        final OrderReportMeasurements currentMeasurements = realm.where(OrderReportMeasurements.class)
                .equalTo("number", measurements.getNumber()).findFirst();
        if (currentMeasurements != null) currentMeasurements.deleteFromRealm();
        // Save new
        realm.copyToRealm(measurements);
        realm.commitTransaction();
        realm.close();
    }

    public static void setOrderMaintenanceTime(final long orderNumber, @OrderMaintenanceType final int timeType, final Date time) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
        if (order != null) {
            switch (timeType) {
                case ORDER_MAINTENANCE_START_TIME:
                    order.setMaintenanceStartTime(time);
                    Session.addToSessionLog("Setting order [" + orderNumber + "] maintenance start time: " + Utils.getDateTimeStringFromDate(time));
                    break;
                case ORDER_MAINTENANCE_END_TIME:
                    order.setMaintenanceEndTime(time);
                    Session.addToSessionLog("Setting order [" + orderNumber + "] maintenance end time: " + Utils.getDateTimeStringFromDate(time));
                    break;
            }
        }
        realm.commitTransaction();
        realm.close();
    }
}
