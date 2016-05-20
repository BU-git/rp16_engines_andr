package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.utils.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends BaseActivity {
    @BindView(R.id.synchronisation_log)
    TextView synchronisationLog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        // Configuring engineer Id
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle("Application log");

        showApplicationLog();
    }

    @OnClick(R.id.reset_user_db)
    public void onResetUserDBClick(View v) {
        DbUtils.resetUserTable();
        showApplicationLog();
    }

    @OnClick(R.id.reset_order_db)
    public void onResetOrderDBClick(View v) {
        DbUtils.resetOrderTableWithSubTables();
        showApplicationLog();
    }

    private void showApplicationLog() {
        synchronisationLog.setText(AppLog.getLog());
    }
}