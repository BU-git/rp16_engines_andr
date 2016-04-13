package com.bionic.kvt.serviceapp.db;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUtils {

    // Completely erase User table and add Demo user
    public static void resetUserTable() {
        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Resetting User table.");


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

    // Completely erase Order Table and all sub tables and add Demo orders
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
//
//        realm.beginTransaction();
//        Order order = realm.createObject(Order.class);
//
//        Relation relation = realm.createObject(Relation.class);
//        order.setRelation(relation);
//
//        Employee employee = realm.createObject(Employee.class);
//        order.setEmployee(employee);
//
//        Installation installation = realm.createObject(Installation.class);
//        order.setInstallation(installation);
//
//        Task task = realm.createObject(Task.class);
//        RealmList<Task> taskRealmList = new RealmList<>(task);
//        order.setTasks(taskRealmList);
//
//        Component component = realm.createObject(Component.class);
//        RealmList<Component> componentRealmList = new RealmList<>(component);
//        order.setComponents(componentRealmList);
//
//        Part part = realm.createObject(Part.class);
//        RealmList<Part> partRealmList = new RealmList<>(part);
//        order.setParts(partRealmList);
//
//        Info info = realm.createObject(Info.class);
//        RealmList<Info> infoRealmList = new RealmList<>(info);
//        order.setExtraInfo(infoRealmList);

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

    public static List<OrderBrief> getOrdersToBeUpdated(final List<OrderBrief> serverOrderBriefList) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Looking for orders to be updated.");
        // Code to generate List<OrderBrief> - Orders actually newer on Server than in app database

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Found XXX orders to be updated.");
        return null;
    }

    public static void updateOrderTableFromServer(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Updating order table from server order data: " + serverOrder.getNumber());


//        final Realm realm = Realm.getDefaultInstance();
//
//        final RealmResults<User> allCurrentUsers = realm.where(User.class).findAll();
//
//        // Set all current users in DB not on server
//        realm.beginTransaction();
//        for (User userInDb : allCurrentUsers) {
//            userInDb.setOnServer(false);
//        }
//        realm.where(User.class).equalTo("email", "demo@kvt.nl").findAll().get(0).setOnServer(true);
//        realm.commitTransaction(); //No logic if transaction fail!!!
//
//        // Updating users in DB
//        realm.beginTransaction();
//        for (com.bionic.kvt.serviceapp.api.User userOnServer : serverUserList) {
//            // Searching for user in DB
//            RealmResults<User> getUserInDb = realm.where(User.class)
//                    .equalTo("email", userOnServer.getEmail())
//                    .findAll();
//
//            if (getUserInDb.size() == 1) { // We have this user on DB, updating it
//                User thisUser = getUserInDb.get(0);
//                thisUser.setName(userOnServer.getName());
//                thisUser.setPassword(userOnServer.getPassword());
//                thisUser.setOnServer(true);
//            } else { // New user, creating it in DB
//                User newUser = realm.createObject(User.class);
//                newUser.setName(userOnServer.getName());
//                newUser.setEmail(userOnServer.getEmail());
//                newUser.setPassword(userOnServer.getPassword());
//                newUser.setOnServer(true);
//            }
//        }
//        realm.commitTransaction(); //No logic if transaction fail!!!
//
//        // Returning updated in DB User count;
//        final int count = realm.where(User.class).equalTo("isOnServer", true).findAll().size();
//        realm.close();

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Update order table from server order " + serverOrder.getNumber() + "done.");

    }

    public static int updateUserTableFromServer(final List<com.bionic.kvt.serviceapp.api.User> serverUserList) {
        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Updating User table from server data");


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
            Session.getSession().addLog("Validating user: " + email + " : " + password);


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
        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Setting user session: " + email);


        Session.getSession().clearSession();

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<User> result = realm.where(User.class).equalTo("email", email).findAll();

        if (result.size() == 1) {
            Session.getSession().setEngineerEmail(email);
            Session.getSession().setEngineerName(result.get(0).getName());
        }

        realm.close();
    }
}
