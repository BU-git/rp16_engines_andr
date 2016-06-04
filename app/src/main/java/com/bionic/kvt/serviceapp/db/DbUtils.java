package com.bionic.kvt.serviceapp.db;

import android.support.annotation.Nullable;

import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.db.Components.Component;
import com.bionic.kvt.serviceapp.db.Components.CustomTemplateElement;
import com.bionic.kvt.serviceapp.db.Components.Employee;
import com.bionic.kvt.serviceapp.db.Components.Info;
import com.bionic.kvt.serviceapp.db.Components.Installation;
import com.bionic.kvt.serviceapp.db.Components.Part;
import com.bionic.kvt.serviceapp.db.Components.Relation;
import com.bionic.kvt.serviceapp.db.Components.Task;
import com.bionic.kvt.serviceapp.models.LMRAModel;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.bionic.kvt.serviceapp.utils.XMLGenerator;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.bionic.kvt.serviceapp.GlobalConstants.CUSTOM_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.CUSTOM_XML_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.DEFAULT_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.DEFAULT_XML_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.JOB_RULES_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.JOB_RULES_XML_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.LMRA_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.LMRA_XML_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.MEASUREMENTS_XML;
import static com.bionic.kvt.serviceapp.GlobalConstants.MEASUREMENTS_XML_FILE_NAME;
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
import static com.bionic.kvt.serviceapp.GlobalConstants.XMLReportType;

public class DbUtils {
    public static final Gson GSON = new Gson();

    // Completely erase User table and add Demo user
    public static void resetUserTable() {
        AppLog.serviceI("Resetting User database.");
        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.delete(User.class);
            realm.commitTransaction();
        }
    }

    // Completely erase Order Table and all sub tables
    public static void resetOrderTableWithSubTables() {
        AppLog.serviceI("Resetting Order database.");

        try (final Realm realm = Realm.getDefaultInstance()) {
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
            realm.delete(CustomTemplate.class);
            realm.delete(CustomTemplateElement.class);
            realm.delete(DefectState.class);
            realm.delete(LMRAPhoto.class);
            realm.delete(LMRAItem.class);

            realm.commitTransaction();

            Utils.deleteRecursive(Session.getCurrentAppDir());
        }
    }

    public static void deleteUser(final String email) {
        AppLog.serviceI("Deleting user from DB: " + email);
        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.where(User.class).equalTo("email", email).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    public static void updateOrderOverviewList(final List<OrderOverview> listToUpdate) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final RealmResults<Order> allOrdersInDbSorted = realm.where(Order.class)
                    .equalTo("employeeEmail", Session.getEngineerEmail())
                    .findAllSorted("number");

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
        }
    }

    public static void updateLMRAList(final List<LMRAModel> listToUpdate) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final RealmResults<LMRAItem> allLMRAItemsInDbSorted = realm.where(LMRAItem.class)
                    .equalTo("number", Session.getCurrentOrder())
                    .findAllSorted("lmraId");

            listToUpdate.clear();
            for (LMRAItem lmraItem : allLMRAItemsInDbSorted) {
                final LMRAModel lmraModel = new LMRAModel();
                lmraModel.setLmraId(lmraItem.getLmraId());
                lmraModel.setLmraName(lmraItem.getLmraName());
                lmraModel.setLmraDescription(lmraItem.getLmraDescription());

                List<File> listLMRAPhotos = null;

                final RealmResults<LMRAPhoto> listLMRAPhotosInBD =
                        realm.where(LMRAPhoto.class)
                                .equalTo("number", Session.getCurrentOrder())
                                .equalTo("lmraId", lmraItem.getLmraId())
                                .findAllSorted("lmraPhotoFile");

                if (listLMRAPhotosInBD.size() > 0) {
                    listLMRAPhotos = new ArrayList<>();
                    for (LMRAPhoto lmraPhoto : listLMRAPhotosInBD) {
                        listLMRAPhotos.add(new File(lmraPhoto.getLmraPhotoFile()));
                    }
                }

                lmraModel.setListLMRAPhotos(listLMRAPhotos);
                listToUpdate.add(lmraModel);
            }
        }
    }

    public static List<Long> getOrdersToBeUpdated(final List<OrderBrief> serverOrderBriefList) {
        AppLog.serviceI("Looking for orders to be updated.");

        final List<Long> ordersToBeUpdated = new ArrayList<>();
        try (final Realm realm = Realm.getDefaultInstance()) {

            for (OrderBrief orderBrief : serverOrderBriefList) {
                Order orderInDb = realm
                        .where(Order.class)
                        .equalTo("number", orderBrief.getNumber())
                        .findFirst();

                if (orderInDb == null) { // No such order in DB
                    ordersToBeUpdated.add(orderBrief.getNumber());
                    continue;
                }

                if (isOrderNewerOnServer(orderInDb, orderBrief)) { // We have order in DB but it's outdated
                    ordersToBeUpdated.add(orderBrief.getNumber());
                }
            }

        }
        AppLog.serviceI("Found " + ordersToBeUpdated.size() + " orders to be updated.");
        return ordersToBeUpdated;
    }

    public static void removeOrdersNotOnServer(final String user, final List<OrderBrief> serverOrderBriefList) {
        AppLog.serviceI("Removing local orders that are not on server.");

        List<Long> orderNumbersOnServer = new ArrayList<>();
        List<Long> orderToBeDeleted = new ArrayList<>();
        for (OrderBrief orderBrief : serverOrderBriefList) {
            orderNumbersOnServer.add(orderBrief.getNumber());
        }

        try (final Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Order> ordersInDB = realm.where(Order.class)
                    .equalTo("employeeEmail", user)
                    .findAll();


            for (Order orderInDB : ordersInDB) {
                if (!orderNumbersOnServer.contains(orderInDB.getNumber())) {
                    // Can not delete here because RealmResults<Order> will be updated automatically
                    orderToBeDeleted.add(orderInDB.getNumber());
                }
            }
        }

        for (Long orderNumber : orderToBeDeleted) {
            removeOrderFromDB(orderNumber);
        }
    }

    private static boolean isOrderNewerOnServer(final Order orderInDb, final OrderBrief orderBrief) {
        if (orderInDb.getImportDate()
                .compareTo(new Date(orderBrief.getImportDate())) != 0)
            return true;

        return orderInDb.getLastServerChangeDate()
                .compareTo(new Date(orderBrief.getLastServerChangeDate())) != 0;

    }

    private static void removeOrderFromDB(final long orderNumber) {
        AppLog.serviceI(false, orderNumber, "Removing all order data.");
        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            realm.where(CustomTemplate.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(DefectState.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(Order.class).equalTo("number", orderNumber).findFirst().deleteFromRealm();
            realm.where(OrderReportJobRules.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(OrderReportMeasurements.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(OrderSynchronisation.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(LMRAItem.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.where(LMRAPhoto.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();

            realm.commitTransaction(); // No logic if transaction fail!!!

            removeOrderDir(orderNumber);
        }
    }

    public static void removeOrderDir(final long orderNumber) {
        AppLog.serviceI(false, orderNumber, "Removing order folder.");
        Utils.deleteRecursive(Utils.getOrderDir(orderNumber));

    }

    public static void updateOrderFromServer(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        AppLog.serviceI("Updating order from server order data: " + serverOrder.getNumber());

        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order currentOrderInDB = realm.where(Order.class)
                    .equalTo("number", serverOrder.getNumber())
                    .findFirst();

            if (currentOrderInDB == null) { // New order
                createNewOrderInDb(serverOrder);
            }

            if (currentOrderInDB != null) { // Existing order
                switch (currentOrderInDB.getOrderStatus()) {
                    case ORDER_STATUS_NOT_STARTED:
                        AppLog.serviceI("Deleting order: " + currentOrderInDB.getNumber());
                        removeOrderFromDB(currentOrderInDB.getNumber());
                        createNewOrderInDb(serverOrder);
                        break;

                    case ORDER_STATUS_IN_PROGRESS:
                        AppLog.serviceW(true, currentOrderInDB.getNumber(), "Cannot update order in status IN PROGRESS.");
                        break;
                    case ORDER_STATUS_COMPLETE:
                        AppLog.serviceW(true, currentOrderInDB.getNumber(), "Cannot update order in status COMPLETE.");
                        break;
                    case ORDER_STATUS_COMPLETE_UPLOADED:
                        AppLog.serviceW(true, currentOrderInDB.getNumber(), "Cannot update order in status UPLOADED.");
                        break;
                    case ORDER_STATUS_NOT_FOUND:
                        break;
                }
            }
        }

    }

    private static void createNewOrderInDb(final com.bionic.kvt.serviceapp.api.Order serverOrder) {
        AppLog.serviceI("Creating order: " + serverOrder.getNumber());

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            final Order newOrder = realm.createObject(Order.class);

            newOrder.setNumber(serverOrder.getNumber());
            newOrder.setOrderType(serverOrder.getOrderType());
            newOrder.setDate(new Date(serverOrder.getDate()));
            newOrder.setReference(serverOrder.getReference());
            newOrder.setNote(serverOrder.getNote());

            // Relation
            newOrder.setRelation(realm.createObjectFromJson
                    (Relation.class, GSON.toJson(serverOrder.getRelation()))
            );

            // Employee
            newOrder.setEmployee(realm.createObjectFromJson
                    (Employee.class, GSON.toJson(serverOrder.getEmployee()))
            );

            // Installation
            newOrder.setInstallation(realm.createObjectFromJson
                    (Installation.class, GSON.toJson(serverOrder.getInstallation()))
            );

            // Task
            final RealmList<Task> newTaskList = new RealmList<>();
            for (com.bionic.kvt.serviceapp.api.Task serverTask : serverOrder.getTasks()) {
                newTaskList.add(realm.createObjectFromJson
                        (Task.class, GSON.toJson(serverTask))
                );
            }
            newOrder.setTasks(newTaskList);

            // Component
            final RealmList<Component> newComponentList = new RealmList<>();
            for (com.bionic.kvt.serviceapp.api.Component serverComponent : serverOrder.getComponents()) {
                newComponentList.add(realm.createObjectFromJson
                        (Component.class, GSON.toJson(serverComponent))
                );
            }
            newOrder.setComponents(newComponentList);

            // Part
            final RealmList<Part> newPartList = new RealmList<>();
            for (com.bionic.kvt.serviceapp.api.Part serverPart : serverOrder.getParts()) {
                newPartList.add(realm.createObjectFromJson
                        (Part.class, GSON.toJson(serverPart))
                );
            }
            newOrder.setParts(newPartList);

            // Info
            final RealmList<Info> newInfoList = new RealmList<>();
            for (com.bionic.kvt.serviceapp.api.Info serverInfo : serverOrder.getExtraInfo()) {
                newInfoList.add(realm.createObjectFromJson
                        (Info.class, GSON.toJson(serverInfo))
                );
            }
            newOrder.setExtraInfo(newInfoList);

            newOrder.setImportDate(new Date(serverOrder.getImportDate()));
            newOrder.setLastServerChangeDate(new Date(serverOrder.getLastServerChangeDate()));
            newOrder.setLastAndroidChangeDate(new Date(serverOrder.getLastAndroidChangeDate()));
            newOrder.setCustomTemplateID(serverOrder.getCustomTemplateID());
            newOrder.setOrderStatus(serverOrder.getOrderStatus());
            newOrder.setIfOrderStatusSyncWithServer(true);

            newOrder.setMaintenanceStartTime(new Date(0));
            newOrder.setMaintenanceEndTime(new Date(0));

            newOrder.setEmployeeEmail(serverOrder.getEmployee().getEmail());

            realm.commitTransaction(); // No logic if transaction fail!!!
        }
    }

    public static void updateCustomTemplateFromServer(final long orderNumber, final com.bionic.kvt.serviceapp.api.CustomTemplate customTemplateOnServer) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            // Deleting current custom template
            realm.beginTransaction();
            realm.where(CustomTemplate.class).equalTo("number", orderNumber).findAll().deleteAllFromRealm();
            realm.commitTransaction();

            AppLog.serviceI("Creating custom template ID: " + customTemplateOnServer.getCustomTemplateID());
            realm.beginTransaction();
            final CustomTemplate newCustomTemplate = realm.createObject(CustomTemplate.class);
            newCustomTemplate.setNumber(orderNumber);
            newCustomTemplate.setCustomTemplateID(customTemplateOnServer.getCustomTemplateID());
            newCustomTemplate.setCustomTemplateName(customTemplateOnServer.getCustomTemplateName());

            final RealmList<CustomTemplateElement> newCustomTemplateElementList = new RealmList<>();
            for (com.bionic.kvt.serviceapp.api.CustomTemplateElement elementOnServer : customTemplateOnServer.getCustomTemplateElements()) {
                CustomTemplateElement newCustomTemplateElement = realm.createObject(CustomTemplateElement.class);
                newCustomTemplateElement.setElementType(elementOnServer.getElementType());
                newCustomTemplateElement.setElementValue(elementOnServer.getElementValue());
                newCustomTemplateElement.setElementText(elementOnServer.getElementText());
                newCustomTemplateElementList.add(newCustomTemplateElement);
            }
            newCustomTemplate.setCustomTemplateElements(newCustomTemplateElementList);

            realm.commitTransaction();
        }
    }

    public static void createNewLMRAInDB(final String lmraName, final String lmraDescription) {
        AppLog.serviceI("Creating new LMRA.");

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            final LMRAItem newLMRAItem = realm.createObject(LMRAItem.class);

            newLMRAItem.setLmraId(System.currentTimeMillis());
            newLMRAItem.setNumber(Session.getCurrentOrder());
            newLMRAItem.setLmraName(lmraName);
            newLMRAItem.setLmraDescription(lmraDescription);

            realm.commitTransaction(); // No logic if transaction fail!!!
            AppLog.serviceI("New LMRA created. ID: " + newLMRAItem.getLmraId());
        }
    }

    public static void updateLMRAInDB(final long lmraId, final String lmraName, final String lmraDescription) {
        AppLog.serviceI("Updating LMRA: " + lmraId);
        try (final Realm realm = Realm.getDefaultInstance()) {

            final LMRAItem currentLMRAItem = realm.where(LMRAItem.class)
                    .equalTo("number", Session.getCurrentOrder())
                    .equalTo("lmraId", lmraId)
                    .findFirst();

            if (currentLMRAItem == null) {
                AppLog.serviceE(true, -1, "No such LMRA: " + lmraId);
                return;
            }

            realm.beginTransaction();
            currentLMRAItem.setLmraName(lmraName);
            currentLMRAItem.setLmraDescription(lmraDescription);
            realm.commitTransaction();
        }
    }

    public static void removeLMRAFromDB(final long lmraId) {
        AppLog.serviceI("Deleting LMRA: " + lmraId);

        try (final Realm realm = Realm.getDefaultInstance()) {
            final LMRAItem currentLMRAItem = realm.where(LMRAItem.class)
                    .equalTo("number", Session.getCurrentOrder())
                    .equalTo("lmraId", lmraId)
                    .findFirst();

            if (currentLMRAItem == null) {
                AppLog.serviceE(true, -1, "No such LMRA: " + lmraId);
                return;
            }

            removeLMRAPhoto(lmraId, "");

            realm.beginTransaction();
            currentLMRAItem.deleteFromRealm();
            realm.commitTransaction(); // No logic if transaction fail!!!
        }
    }

    public static void saveLMRAPhotoInDB(final long lmraId, final File lmraPhotoFile) {
        AppLog.serviceI("Saving LMRA [" + lmraId + "] photo file to DB: " + lmraPhotoFile);

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            final LMRAPhoto newLMRAPhoto = realm.createObject(LMRAPhoto.class);
            newLMRAPhoto.setLmraId(lmraId);
            newLMRAPhoto.setNumber(Session.getCurrentOrder());
            newLMRAPhoto.setLmraPhotoFile(lmraPhotoFile.toString());
            newLMRAPhoto.setLmraPhotoFileSynced(false);
            realm.commitTransaction(); // No logic if transaction fail!!!
        }
    }

    public static void removeLMRAPhoto(final long lmraId, final String lmraPhotoFile) {
        AppLog.serviceI("Deleting photos for LMRA: " + lmraId);

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            RealmResults<LMRAPhoto> allLMRAPhotosForLMRAID;
            if ("".equals(lmraPhotoFile)) {
                allLMRAPhotosForLMRAID = realm.where(LMRAPhoto.class)
                        .equalTo("number", Session.getCurrentOrder())
                        .equalTo("lmraId", lmraId)
                        .findAll();
            } else {
                allLMRAPhotosForLMRAID = realm.where(LMRAPhoto.class)
                        .equalTo("number", Session.getCurrentOrder())
                        .equalTo("lmraId", lmraId)
                        .equalTo("lmraPhotoFile", lmraPhotoFile)
                        .findAll();
            }

            // Deleting photo files
            for (LMRAPhoto lmraPhoto : allLMRAPhotosForLMRAID) {
                final File lmraFile = new File(lmraPhoto.getLmraPhotoFile());
                if (lmraFile.exists()) lmraFile.delete();
            }

            allLMRAPhotosForLMRAID.deleteAllFromRealm();
            realm.commitTransaction(); // No logic if transaction fail!!!
        }
    }

    public static void updateUserFromServer(final com.bionic.kvt.serviceapp.api.User serverUser) {
        AppLog.serviceI("Updating User table from server data.");

        try (final Realm realm = Realm.getDefaultInstance()) {
            // Searching for user in DB
            final User userInDb = realm.where(User.class).equalTo("email", serverUser.getEmail()).findFirst();

            realm.beginTransaction();
            if (userInDb != null) { // We have this user on DB, updating it
                userInDb.setName(serverUser.getName());
                userInDb.setEmail(serverUser.getEmail());
                userInDb.setPasswordHash(serverUser.getPasswordHash());
                userInDb.setSalt(serverUser.getSalt());
            } else { // New user, creating it in DB
                final User newUser = realm.createObject(User.class);
                newUser.setName(serverUser.getName());
                newUser.setEmail(serverUser.getEmail());
                newUser.setPasswordHash(serverUser.getPasswordHash());
                newUser.setSalt(serverUser.getSalt());
            }
            realm.commitTransaction(); //No logic if transaction fail!!!
        }
    }

    public static void saveDefectStateListToDB(final List<com.bionic.kvt.serviceapp.models.DefectState> defectStateList) {
        removeDefectStateListFromDB();

        try (final Realm realm = Realm.getDefaultInstance()) {
            DefectState newDefectState;

            for (com.bionic.kvt.serviceapp.models.DefectState defectState : defectStateList) {
                AppLog.serviceI(false, Session.getCurrentOrder(), "Saving defect state: "
                        + defectState.getPart() + ">"
                        + defectState.getElement() + ">"
                        + defectState.getProblem());

                realm.beginTransaction();

                newDefectState = realm.createObject(DefectState.class);
                newDefectState.setNumber(Session.getCurrentOrder());

                newDefectState.setPart(defectState.getPart());
                newDefectState.setElement(defectState.getElement());
                newDefectState.setProblem(defectState.getProblem());

                newDefectState.setExtent(defectState.getExtent());
                newDefectState.setIntensity(defectState.getIntensity());
                newDefectState.setFixed(defectState.isFixed());
                newDefectState.setAction(defectState.getAction());

                newDefectState.setCondition(defectState.getCondition());
                newDefectState.setInitialScore(defectState.getInitialScore());
                newDefectState.setCorrelation(defectState.getCorrelation());
                newDefectState.setCorrelatedScore(defectState.getCorrelatedScore());

                realm.commitTransaction(); // No logic if transaction fail!!!
            }
        }
    }

    @Nullable
    public static DefectState getDefectStateFromDB(final long orderNumber, final String part, final String element, final String problem) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            return realm.where(DefectState.class)
                    .equalTo("number", orderNumber)
                    .equalTo("part", part)
                    .equalTo("element", element)
                    .equalTo("problem", problem)
                    .findFirst();
        }
    }

    public static void removeDefectStateListFromDB() {
        AppLog.serviceI(false, Session.getCurrentOrder(), "Removing defect states from DB.");

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.where(DefectState.class)
                    .equalTo("number", Session.getCurrentOrder())
                    .findAll()
                    .deleteAllFromRealm();
            realm.commitTransaction(); // No logic if transaction fail!!!
        }
    }

    public static void saveScoreToDB(final long orderNumber, final int score) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            if (order == null) {
                AppLog.serviceE(true, orderNumber, "Saving score to DB: No order found.");
                return;
            }

            realm.beginTransaction();
            order.setScore(score);
            realm.commitTransaction();
        }
    }

    public static boolean isUserLoginValid(final String email, final String password) {
        AppLog.serviceI("Validating user: " + email);

        try (final Realm realm = Realm.getDefaultInstance()) {
            final RealmResults<User> usersInDB = realm.where(User.class).equalTo("email", email).findAll();
            if (usersInDB.size() == 0) {
                AppLog.serviceI("Validating user login: No user found: " + email);
                return false;
            }

            final MessageDigest digester;
            try {
                digester = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                AppLog.serviceE(true, -1, "NoSuchAlgorithmException (SHA-256): " + e.toString());
                return false;
            }

            final String saltInDB = usersInDB.first().getSalt();
            final String passwordHashInDB = usersInDB.first().getPasswordHash();

            if (passwordHashInDB == null) {
                AppLog.serviceE(true, -1, "No password in DB.");
                return false;
            }

            byte[] hash = (password + saltInDB).getBytes();
            for (int i = 0; i <= PASSWORD_HASH_ITERATIONS; i++) {
                digester.update(hash);
                hash = digester.digest();
            }
            return passwordHashInDB.equals(Utils.convertByteArrayToHexString(hash));
        }
    }

    public static void setUserSession(final String email) {
        AppLog.serviceI("Setting user session: " + email);

        Session.clearSession();
        try (final Realm realm = Realm.getDefaultInstance()) {
            final RealmResults<User> usersInDB = realm.where(User.class).equalTo("email", email).findAll();
            if (usersInDB.size() != 1) {
                AppLog.serviceE(true, -1, "Validating user login: More then one user found: " + email);
                return;
            }

            Session.setEngineerEmail(email);
            Session.setEngineerName(usersInDB.get(0).getName());
        }
    }

    @Nullable
    public static String getUserHash(final String email) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final User userInDB = realm.where(User.class).equalTo("email", email).findFirst();
            if (userInDB == null) return null;
            return userInDB.getPasswordHash();
        }
    }

    public static void setOrderStatus(final long orderNumber, @OrderStatus final int status) {
        AppLog.serviceI(false, orderNumber, "Setting order status: " + status);

        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            if (order == null) {
                AppLog.serviceE(true, orderNumber, "Setting order status: No such order!");
                return;
            }
            realm.beginTransaction();
            if (order.getOrderStatus() != status) {
                order.setOrderStatus(status);
                order.setLastAndroidChangeDate(new Date());
                order.setIfOrderStatusSyncWithServer(false);
            }
            realm.commitTransaction();
        }
    }

    @OrderStatus
    public static int getOrderStatus(final long orderNumber) {
        AppLog.serviceI(false, orderNumber, "Getting order status.");
        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            if (order == null) {
                AppLog.serviceE(true, orderNumber, "Getting order status: No such order!");
                return ORDER_STATUS_NOT_FOUND;
            } else {
                return order.getOrderStatus();
            }
        }
    }

    public static void setOrderReportJobRules(final OrderReportJobRules jobRules) {
        AppLog.serviceI(false, jobRules.getNumber(), "Saving Job Rules");

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            // Remove if already exist
            final OrderReportJobRules currentJobRules = realm.where(OrderReportJobRules.class)
                    .equalTo("number", jobRules.getNumber()).findFirst();
            if (currentJobRules != null) currentJobRules.deleteFromRealm();
            // Save new
            realm.copyToRealm(jobRules);
            realm.commitTransaction();
        }
    }

    public static void setOrderReportMeasurements(final OrderReportMeasurements measurements) {
        AppLog.serviceI(false, measurements.getNumber(), "Saving Measurements");

        try (final Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            // Remove if already exist
            final OrderReportMeasurements currentMeasurements = realm.where(OrderReportMeasurements.class)
                    .equalTo("number", measurements.getNumber()).findFirst();
            if (currentMeasurements != null) currentMeasurements.deleteFromRealm();
            // Save new
            realm.copyToRealm(measurements);
            realm.commitTransaction();
        }
    }

    public static void setOrderMaintenanceTime(final long orderNumber, @OrderMaintenanceType final int timeType, final Date time) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            if (order == null) {
                AppLog.serviceE(true, orderNumber, "Setting order Maintenance time: No such order!");
                return;
            }
            realm.beginTransaction();
            switch (timeType) {
                case ORDER_MAINTENANCE_START_TIME:
                    AppLog.serviceI(false, orderNumber, "Setting order maintenance start time: " + Utils.getDateTimeStringFromDate(time));
                    order.setMaintenanceStartTime(time);
                    break;
                case ORDER_MAINTENANCE_END_TIME:
                    AppLog.serviceI(false, orderNumber, "Setting order maintenance end time: " + Utils.getDateTimeStringFromDate(time));
                    order.setMaintenanceEndTime(time);
                    break;
            }
            realm.commitTransaction();
        }
    }

    public static boolean isCustomTemplate(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            return order != null && order.getCustomTemplateID() > 0;
        }
    }

    // returns full XML file path as String
    @Nullable
    public static String generateXMLReport(final long orderNumber, @XMLReportType final int XMLReportType) {
        String XMLData = null;
        File XMLFile = null;

        switch (XMLReportType) {
            case LMRA_XML:
                XMLData = XMLGenerator.getXMLFromLMRA(orderNumber);
                XMLFile = new File(Utils.getOrderDir(orderNumber), LMRA_XML_FILE_NAME + orderNumber + ".xml");
                break;
            case DEFAULT_XML:
                XMLData = XMLGenerator.getXMLFromDefaultTemplate(orderNumber);
                XMLFile = new File(Utils.getOrderDir(orderNumber), DEFAULT_XML_FILE_NAME + orderNumber + ".xml");
                break;
            case CUSTOM_XML:
                XMLData = XMLGenerator.getXMLFromCustomTemplate(orderNumber);
                XMLFile = new File(Utils.getOrderDir(orderNumber), CUSTOM_XML_FILE_NAME + orderNumber + ".xml");
                break;
            case MEASUREMENTS_XML:
                XMLData = XMLGenerator.getXMLFromMeasurements(orderNumber);
                XMLFile = new File(Utils.getOrderDir(orderNumber), MEASUREMENTS_XML_FILE_NAME + orderNumber + ".xml");
                break;
            case JOB_RULES_XML:
                XMLData = XMLGenerator.getXMLFromJobRules(orderNumber);
                XMLFile = new File(Utils.getOrderDir(orderNumber), JOB_RULES_XML_FILE_NAME + orderNumber + ".xml");
                break;

        }

        if (XMLData == null) return null;

        try (FileWriter outputFile = new FileWriter(XMLFile)) {
            outputFile.write(XMLData);
        } catch (IOException e) {
            AppLog.serviceE(true, orderNumber, "Error saving XML report file: " + e.toString());
            return null;
        }

        return XMLFile.toString();
    }
}
