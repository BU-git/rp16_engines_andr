package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.OrderReportJobRules;
import com.bionic.kvt.serviceapp.utils.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class JobRulesActivity extends BaseActivity {
    @BindView(R.id.job_rules_fuel_added)
    CheckBox fuelAdded;

    @BindView(R.id.job_rules_water_separators_drained)
    CheckBox waterSeparatorsDrained;

    @BindView(R.id.job_rules_leave_operational)
    CheckBox leaveOperational;

    @BindView(R.id.job_rules_customer_material)
    Switch customerMaterial;

    @BindView(R.id.job_rules_material_from_bus)
    Switch materialFromBus;

    @BindView(R.id.job_rules_repair_advice)
    Switch repairAdvice;

    @BindView(R.id.job_rules_remaining_work)
    Switch remainingWork;

    @BindView(R.id.job_rules_text_internal_remarks)
    EditText internalRemarksText;

    @BindView(R.id.job_rules_text_external_remarks)
    EditText externalRemarksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_rules);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.job_rules));

        // Limit lines number to 15
        internalRemarksText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (((EditText) v).getLineCount() >= 10) return true;
                }
                return false;
            }
        });

        // Limit lines number to 5
        externalRemarksText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (((EditText) v).getLineCount() >= 5) return true;
                }
                return false;
            }
        });

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(JobRulesActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + JobRulesActivity.class.getSimpleName());

        // Filling saved data
        final Realm realm = Realm.getDefaultInstance();
        final OrderReportJobRules currentJobRules = realm.where(OrderReportJobRules.class)
                .equalTo("number", Session.getCurrentOrder()).findFirst();
        if (currentJobRules != null) {
            fuelAdded.setChecked(currentJobRules.isFuelAdded());
            waterSeparatorsDrained.setChecked(currentJobRules.isWaterSeparatorsDrained());
            leaveOperational.setChecked(currentJobRules.isLeaveOperational());
            customerMaterial.setChecked(currentJobRules.isUseCustomerMaterial());
            materialFromBus.setChecked(currentJobRules.isUseMaterialFromBus());
            repairAdvice.setChecked(currentJobRules.isRepairAdvice());
            remainingWork.setChecked(currentJobRules.isRemainingWork());
            internalRemarksText.setText(currentJobRules.getInternalRemarksText());
            externalRemarksText.setText(currentJobRules.getExternalRemarksText());
        }
        realm.close();

    }

    @OnClick(R.id.job_rules_save_button)
    public void onSaveClick(View v) {
        OrderReportJobRules jobRules = new OrderReportJobRules();
        jobRules.setNumber(Session.getCurrentOrder());
        jobRules.setFuelAdded(fuelAdded.isChecked());
        jobRules.setWaterSeparatorsDrained(waterSeparatorsDrained.isChecked());
        jobRules.setLeaveOperational(leaveOperational.isChecked());
        jobRules.setUseCustomerMaterial(customerMaterial.isChecked());
        jobRules.setUseMaterialFromBus(materialFromBus.isChecked());
        jobRules.setRepairAdvice(repairAdvice.isChecked());
        jobRules.setRemainingWork(remainingWork.isChecked());
        jobRules.setInternalRemarksText(internalRemarksText.getText().toString());
        jobRules.setExternalRemarksText(externalRemarksText.getText().toString());
        DbUtils.setOrderReportJobRules(jobRules);

        final Intent intent = new Intent(getApplicationContext(), PDFReportPreviewActivity.class);
        startActivity(intent);
    }
}