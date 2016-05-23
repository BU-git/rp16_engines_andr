package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.AppLogItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class LogActivity extends BaseActivity {
    @BindView(R.id.synchronisation_log)
    TextView synchronisationLog;

    // App Log monitor. Its different from monitor in other activities
    private Realm monitorLogRealm = Session.getLogRealm();
    private RealmChangeListener<RealmResults<AppLogItem>> logListener;
    private RealmResults<AppLogItem> logsWithNotification;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        // Configuring engineer Id
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle("Application log");

        // Creating Log update callback
        logListener = new RealmChangeListener<RealmResults<AppLogItem>>() {
            @Override
            public void onChange(RealmResults<AppLogItem> appLogItems) {
                synchronisationLog.setText(AppLog.getLog());
            }
        };

        logsWithNotification = monitorLogRealm.where(AppLogItem.class).findAllSorted("dateTime");
        logsWithNotification.addChangeListener(logListener);

        synchronisationLog.setText(AppLog.getLog());
    }

    @OnClick(R.id.reset_user_db)
    public void onResetUserDBClick(View v) {
        DbUtils.resetUserTable();

    }

    @OnClick(R.id.reset_order_db)
    public void onResetOrderDBClick(View v) {
        DbUtils.resetOrderTableWithSubTables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logsWithNotification.removeChangeListener(logListener);
    }
}