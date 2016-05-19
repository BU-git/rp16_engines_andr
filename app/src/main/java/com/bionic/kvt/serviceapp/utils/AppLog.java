package com.bionic.kvt.serviceapp.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class AppLog {
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARRING = 3;
    private static final int ERROR = 4;

//    private static Realm monitorLogRealm;
//    private static RealmChangeListener<RealmResults<LogItem>> logListener;
//    private static RealmResults<LogItem> logsWithNotification;

    public static void initLog() {
        try (final Realm logRealm = Session.getLogRealm()) {
            logRealm.beginTransaction();
            logRealm.deleteAll();
            logRealm.commitTransaction();

            I("Application started.");
        }
    }

    public static RealmChangeListener<RealmResults<LogItem>> setLogListener(final Activity activity,
                                                                            final Realm monitorLogRealm) {
        return new RealmChangeListener<RealmResults<LogItem>>() {
            @Override
            public void onChange(RealmResults<LogItem> logItems) {
                if (logItems.size() == 0) return;
                Snackbar snackbar = getSnackbar(activity, logItems.get(0));
                snackbar.setAction("MORE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                snackbar.show();

                monitorLogRealm.beginTransaction();
                logItems.get(0).setNotify(false);
                monitorLogRealm.commitTransaction();
            }
        };
    }

    public static RealmResults<LogItem> addListener(final Realm monitorLogRealm,
                                                    final RealmChangeListener<RealmResults<LogItem>> logListener) {
        RealmResults<LogItem> logsWithNotification = monitorLogRealm.where(LogItem.class).equalTo("notify", true).findAll().sort("dateTime");
        logsWithNotification.addChangeListener(logListener);
        return logsWithNotification;
    }

    public static void removeListener(final Realm monitorLogRealm,
                                      final RealmResults<LogItem> logsWithNotification,
                                      final RealmChangeListener<RealmResults<LogItem>> logListener) {
        logsWithNotification.removeChangeListener(logListener);
        monitorLogRealm.close();
    }

    public static Snackbar getSnackbar(final Activity activity, final LogItem logItem) {
        final View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(rootView, logItem.getMessage(), Snackbar.LENGTH_LONG);

        switch (logItem.getLevel()) {
            case INFO:
                snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.colorOK));
                break;
            case WARRING:
                snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.colorWarring));
                break;
            case ERROR:
                snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.colorError));
                break;
        }


        // Changing action button text color
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.YELLOW);

        return snackbar;
    }

    public static StringBuilder getLog() {
        try (final Realm logRealm = Session.getLogRealm()) {
            final StringBuilder stringBuilder = new StringBuilder("Application log:");
            final RealmResults<LogItem> logItems = logRealm.where(LogItem.class).findAll().sort("dateTime");
            for (LogItem logItem : logItems) {
                stringBuilder.append("\n").append(logItem.getMessage());
            }
            return stringBuilder;
        }
    }

    public static void D(final String message) {
        add(DEBUG, false, -1, message);
    }

    public static void D(final boolean notify, final long orderNumber, final String message) {
        add(DEBUG, notify, orderNumber, message);
    }

    public static void I(final String message) {
        add(INFO, false, -1, message);
    }

    public static void I(final boolean notify, final long orderNumber, final String message) {
        add(INFO, notify, orderNumber, message);
    }

    public static void W(final boolean notify, final long orderNumber, final String message) {
        add(WARRING, notify, orderNumber, message);
    }

    public static void E(final String message) {
        add(ERROR, true, -1, message);
    }

    public static void E(final boolean notify, final long orderNumber, final String message) {
        add(ERROR, notify, orderNumber, message);
    }

    private static void add(final int level, final boolean notify, final long orderNumber, final String message) {
        try (final Realm logRealm = Session.getLogRealm()) {
            final LogItem logItem = new LogItem(System.currentTimeMillis(), new Date(), level, notify, orderNumber, message);
            logRealm.beginTransaction();
            logRealm.copyToRealm(logItem);
            logRealm.commitTransaction();
        }
    }
}
