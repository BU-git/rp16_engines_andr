package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.MeasurementsExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeasurementsActivity extends BaseActivity {
    @Bind(R.id.measurements_motor_exp_list_view)
    ExpandableListView expMotorListView;
    MeasurementsExpandableListAdapter listMotorAdapter;
    List<String> listMotorDataHeader;
    HashMap<String, List<String[]>> listMotorDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.measurements));

        prepareListData();

        listMotorAdapter = new MeasurementsExpandableListAdapter(this, listMotorDataHeader, listMotorDataChild);
        expMotorListView.setAdapter(listMotorAdapter);


    }

    @OnClick(R.id.measurements_next_button)
    public void onNextClick(View v) {
        Intent intent = new Intent(getApplicationContext(), JobRulesActivity.class);
        startActivity(intent);
    }

    private void prepareListData() {
        listMotorDataHeader = new ArrayList<>();
        listMotorDataChild = new HashMap<>();

        // Adding child data
        listMotorDataHeader.add("Motor: Compression test");
        listMotorDataHeader.add("Motor: Oil");
        listMotorDataHeader.add("Motor: Coolant");
        listMotorDataHeader.add("Installation: Environment");
        listMotorDataHeader.add("Installation: Test run charge");
        listMotorDataHeader.add("Installation: Test run without load");
        listMotorDataHeader.add("Exhaust: Exhaust gases");

        // Adding child data
        List<String[]> childMotor1 = new ArrayList<>();
        childMotor1.add(new String[]{"Pressure","5.11","bar"});

        List<String[]> childMotor2 = new ArrayList<>();
        childMotor2.add(new String[]{"Pressure","6.671","bar"});
        childMotor2.add(new String[]{"Temperature","36.6","\u00B0C"});
        childMotor2.add(new String[]{"Type","Cool",""});
        childMotor2.add(new String[]{"Manufacture","Motor Sich",""});

        List<String[]> childMotor3 = new ArrayList<>();
        childMotor3.add(new String[]{"Temperature","3.89","\u00B0C"});
        childMotor3.add(new String[]{"Acidity","12.0","pH"});
        childMotor3.add(new String[]{"Frost protection","2.2","\u00B0C"});

        List<String[]> childInstallation1 = new ArrayList<>();
        childInstallation1.add(new String[]{"Temperature","45.7","\u00B0C"});

        List<String[]> childInstallation2 = new ArrayList<>();
        childInstallation2.add(new String[]{"Generator voltage","233.8","Volt"});
        childInstallation2.add(new String[]{"Ampere Phase 1","5.6","Ampere"});
        childInstallation2.add(new String[]{"Ampere Phase 2","5.8","Ampere"});
        childInstallation2.add(new String[]{"Ampere Phase 3","5.4","Ampere"});
        childInstallation2.add(new String[]{"Power during test","14.67","kW"});
        childInstallation2.add(new String[]{"Frequency charge","51.3","Hz"});

        List<String[]> childInstallation3 = new ArrayList<>();
        childInstallation3.add(new String[]{"Frequency charge","55.7","Hz"});

        List<String[]> childExhaust1 = new ArrayList<>();
        childExhaust1.add(new String[]{"Temperature","76.7","\u00B0C"});
        childExhaust1.add(new String[]{"Pressure","4.34","Mbar"});

        listMotorDataChild.put(listMotorDataHeader.get(0), childMotor1);
        listMotorDataChild.put(listMotorDataHeader.get(1), childMotor2);
        listMotorDataChild.put(listMotorDataHeader.get(2), childMotor3);
        listMotorDataChild.put(listMotorDataHeader.get(3), childInstallation1);
        listMotorDataChild.put(listMotorDataHeader.get(4), childInstallation2);
        listMotorDataChild.put(listMotorDataHeader.get(5), childInstallation3);
        listMotorDataChild.put(listMotorDataHeader.get(6), childExhaust1);
    }
}
