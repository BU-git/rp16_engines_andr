package com.bionic.kvt.serviceapp.db;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUtils {

    public static void resetUserTable() {
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

    public static void createTableIfNotExist(Class realmClass) {
        final Realm realm = Realm.getDefaultInstance();
        if (realm.where(realmClass).findAll().size() == 0) {
            if (realmClass == User.class) {
                resetUserTable();
            }
        }
        realm.close();
    }

    public static int updateUserTableFromServer(final List<com.bionic.kvt.serviceapp.models.User> serverUserList) {
        final Realm realm = Realm.getDefaultInstance();

        final RealmResults<User> allCurrentUsers = realm.where(User.class).findAll();
        int index;

        // Set all current users in DB not on server
        realm.beginTransaction();
        for (User userInDb : allCurrentUsers) {
            userInDb.setOnServer(false);
        }
        realm.where(User.class).equalTo("email", "demo@kvt.nl").findAll().get(0).setOnServer(true);
        realm.commitTransaction(); //No logic if transaction fail!!!

        // Updating users in DB
        realm.beginTransaction();
        for (com.bionic.kvt.serviceapp.models.User userOnServer : serverUserList) {
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
        Realm realm = Realm.getDefaultInstance();

        boolean res = realm.where(User.class)
                .equalTo("email", email)
                .equalTo("password", password)
                .findAll()
                .size() == 1;

        realm.close();

        return res;
    }


}
