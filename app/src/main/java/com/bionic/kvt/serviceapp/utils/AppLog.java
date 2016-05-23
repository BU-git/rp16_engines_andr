package com.bionic.kvt.serviceapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.activities.LogActivity;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class AppLog {
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARRING = 3;
    private static final int ERROR = 4;

    public static void initLog() {
        try (final Realm logRealm = Session.getLogRealm()) {
            logRealm.beginTransaction();
            logRealm.deleteAll();
            logRealm.commitTransaction();

            serviceI("Application started.");
        }
    }

    public static RealmChangeListener<RealmResults<AppLogItem>> setLogListener(final Activity activity,
                                                                               final Realm monitorLogRealm) {
        return new RealmChangeListener<RealmResults<AppLogItem>>() {
            @Override
            public void onChange(RealmResults<AppLogItem> appLogItems) {
                if (appLogItems.size() == 0) return;
                final Snackbar snackbar = getSnackbar(activity, appLogItems.get(0).getLogItemID());
                if (snackbar == null) return;
                snackbar.setAction("MORE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, LogActivity.class));
                    }
                });
                snackbar.show();

                monitorLogRealm.beginTransaction();
                appLogItems.get(0).setNotify(false);
                monitorLogRealm.commitTransaction();
            }
        };
    }

    public static RealmResults<AppLogItem> addListener(final Realm monitorLogRealm,
                                                       final RealmChangeListener<RealmResults<AppLogItem>> logListener) {
        RealmResults<AppLogItem> logsWithNotification = monitorLogRealm.where(AppLogItem.class).equalTo("notify", true).findAllSorted("dateTime");
        logsWithNotification.addChangeListener(logListener);
        return logsWithNotification;
    }

    public static void removeListener(final Realm monitorLogRealm,
                                      final RealmResults<AppLogItem> logsWithNotification,
                                      final RealmChangeListener<RealmResults<AppLogItem>> logListener) {
        logsWithNotification.removeChangeListener(logListener);
        monitorLogRealm.close();
    }

    public static StringBuilder getLog() {
        try (final Realm logRealm = Session.getLogRealm()) {
            final StringBuilder stringBuilder = new StringBuilder("Application log:");
            final RealmResults<AppLogItem> appLogItems = logRealm.where(AppLogItem.class).findAll().sort("dateTime");

            for (AppLogItem appLogItem : appLogItems) {
                stringBuilder.append("\n");

                switch (appLogItem.getLevel()) {
                    case DEBUG:
                        stringBuilder.append("DEBUG  ");
                        break;
                    case INFO:
                        stringBuilder.append("INFO   ");
                        break;
                    case WARRING:
                        stringBuilder.append("WARRING");
                        break;
                    case ERROR:
                        stringBuilder.append("ERROR  ");
                        break;
                }
                stringBuilder.append(" [").append(Utils.getDateTimeStringFromDate(appLogItem.getDateTime())).append("]");
                if (appLogItem.getOrderNumber() > 0) {
                    stringBuilder.append("(").append(appLogItem.getOrderNumber()).append("): ");
                } else {
                    stringBuilder.append("(NoOrder): ");
                }

                stringBuilder.append(appLogItem.getMessage());
            }
            return stringBuilder;
        }
    }

    public static void E(final Activity activity, final String message) {
        final long logItemID = serviceAdd(ERROR, false, -1, message);
        showSnackbar(activity, logItemID);
        ;
    }

    public static void W(final Activity activity, final String message) {
        final long logItemID = serviceAdd(WARRING, false, -1, message);
        showSnackbar(activity, logItemID);
    }

    public static void I(final Activity activity, final String message) {
        final long logItemID = serviceAdd(INFO, false, -1, message);
        showSnackbar(activity, logItemID);
    }

    public static void D(final Activity activity, final String message) {
        final long logItemID = serviceAdd(DEBUG, false, -1, message);
        showSnackbar(activity, logItemID);
    }

    public static void serviceD(final String message) {
        serviceAdd(DEBUG, false, -1, message);
    }

    public static void serviceD(final boolean notify, final long orderNumber, final String message) {
        serviceAdd(DEBUG, notify, orderNumber, message);
    }

    public static void serviceI(final String message) {
        serviceAdd(INFO, false, -1, message);
    }

    public static void serviceI(final boolean notify, final long orderNumber, final String message) {
        serviceAdd(INFO, notify, orderNumber, message);
    }

    public static void serviceW(final String message) {
        serviceAdd(WARRING, true, -1, message);
    }

    public static void serviceW(final boolean notify, final long orderNumber, final String message) {
        serviceAdd(WARRING, notify, orderNumber, message);
    }

    public static void serviceE(final String message) {
        serviceAdd(ERROR, true, -1, message);
    }

    public static void serviceE(final boolean notify, final long orderNumber, final String message) {
        serviceAdd(ERROR, notify, orderNumber, message);
    }

    // Private methods

    private static synchronized long serviceAdd(final int level, final boolean notify, final long orderNumber, final String message) {
        try (final Realm logRealm = Session.getLogRealm()) {
            long logItemID;
            AppLogItem appLogItem;
            do { // ID can be already
                logItemID = System.currentTimeMillis();
                appLogItem = logRealm.where(AppLogItem.class).equalTo("logItemID", logItemID).findFirst();
            } while (appLogItem != null);

            appLogItem = new AppLogItem(logItemID, new Date(), level, notify, orderNumber, message);
            logRealm.beginTransaction();
            logRealm.copyToRealm(appLogItem);
            logRealm.commitTransaction();
            return logItemID;
        }
    }

    private static void showSnackbar(final Activity activity, final long logItemID) {
        final Snackbar snackbar = getSnackbar(activity, logItemID);
        if (snackbar != null)
            snackbar.setAction("MORE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(new Intent(activity, LogActivity.class));
                }
            }).show();
    }

    @Nullable
    private static Snackbar getSnackbar(final Activity activity, final long logItemID) {
        try (final Realm logRealm = Session.getLogRealm()) {
            final AppLogItem appLogItem = logRealm.where(AppLogItem.class).equalTo("logItemID", logItemID).findFirst();
            if (appLogItem == null) return null;

            final View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            Snackbar snackbar;
            if (appLogItem.getLevel() == ERROR) {
                snackbar = Snackbar.make(rootView, appLogItem.getMessage(), Snackbar.LENGTH_INDEFINITE);
            } else {
                snackbar = Snackbar.make(rootView, appLogItem.getMessage(), Snackbar.LENGTH_LONG);
            }
            final View sbView = snackbar.getView();
            final TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            switch (appLogItem.getLevel()) {
                case INFO:
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.colorOK));
                    break;
                case WARRING:
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWarring));
                    break;
                case ERROR:
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.colorError));
                    break;
            }

            return snackbar;

        }
    }
}
