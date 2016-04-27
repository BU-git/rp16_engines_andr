package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.OrderReportJobRules;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class JobRulesActivity extends BaseActivity {
    @Bind(R.id.job_rules_fuel_added)
    CheckBox fuelAdded;

    @Bind(R.id.job_rules_water_separators_drained)
    CheckBox waterSeparatorsDrained;

    @Bind(R.id.job_rules_leave_operational)
    CheckBox leaveOperational;

    @Bind(R.id.job_rules_customer_material)
    Switch customerMaterial;

    @Bind(R.id.job_rules_material_from_bus)
    Switch materialFromBus;

    @Bind(R.id.job_rules_repair_advice)
    Switch repairAdvice;

    @Bind(R.id.job_rules_text_operations)
    EditText operationsText;

    @Bind(R.id.job_rules_text_remarks)
    EditText remarksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_rules);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.job_rules));

        // Limit lines number to 15
        operationsText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (((EditText) v).getLineCount() >= 15) return true;
                }
                return false;
            }
        });

        // Limit lines number to 5
        remarksText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (((EditText) v).getLineCount() >= 5) return true;
                }
                return false;
            }
        });

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }

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
            operationsText.setText(currentJobRules.getOperationsText());
            remarksText.setText(currentJobRules.getRemarksText());
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
        jobRules.setOperationsText(operationsText.getText().toString());
        jobRules.setRemarksText(remarksText.getText().toString());
        DbUtils.setOrderReportJobRules(jobRules);

        Intent intent = new Intent(getApplicationContext(), PDFReportPreviewActivity.class);
        startActivity(intent);
    }
}