package com.bionic.kvt.serviceapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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

    private MeasurementsExpandableListAdapter listMotorAdapter;
    private List<String> listMotorDataHeader;
    private HashMap<String, List<String[]>> listMotorDataChild;

    private int currentGroupPosition = -1;
    private int currentChildPosition = -1;

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

        expMotorListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                currentGroupPosition = groupPosition;
                currentChildPosition = childPosition;
                String dialogTitle = listMotorDataChild.get(listMotorDataHeader.get(groupPosition)).get(childPosition)[0] + ":";
                String currentValue = listMotorDataChild.get(listMotorDataHeader.get(groupPosition)).get(childPosition)[1];
                showInputDialog(dialogTitle, currentValue);
                return false;
            }
        });


    }

    private void showInputDialog(final String dialogTitle, final String currentValue) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(dialogTitle);
        final EditText measurementEdit = new EditText(this);
        measurementEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        measurementEdit.setText(currentValue);
        dialogBuilder.setView(measurementEdit);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                putMeasurementValue(measurementEdit.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentGroupPosition = -1;
                currentChildPosition = -1;
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    private void putMeasurementValue(final String newMeasurement) {
        if (currentGroupPosition >= 0 && currentChildPosition >= 0) {
            listMotorDataChild.get(listMotorDataHeader.get(currentGroupPosition)).get(currentChildPosition)[1] = newMeasurement;
            listMotorAdapter.notifyDataSetChanged();
        }
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
        listMotorDataHeader.add("Working hours");

        // Adding child data
        List<String[]> childMotor1 = new ArrayList<>();
        childMotor1.add(new String[]{"Pressure", "5.11", "bar"});

        List<String[]> childMotor2 = new ArrayList<>();
        childMotor2.add(new String[]{"Pressure", "6.671", "bar"});
        childMotor2.add(new String[]{"Temperature", "36.6", "\u00B0C"});
        childMotor2.add(new String[]{"Type", "Cool", ""});
        childMotor2.add(new String[]{"Manufacture", "Motor Sich", ""});

        List<String[]> childMotor3 = new ArrayList<>();
        childMotor3.add(new String[]{"Temperature", "3.89", "\u00B0C"});
        childMotor3.add(new String[]{"Acidity", "12.0", "pH"});
        childMotor3.add(new String[]{"Frost protection", "2.2", "\u00B0C"});

        List<String[]> childInstallation1 = new ArrayList<>();
        childInstallation1.add(new String[]{"Temperature", "45.7", "\u00B0C"});

        List<String[]> childInstallation2 = new ArrayList<>();
        childInstallation2.add(new String[]{"Generator voltage", "233.8", "Volt"});
        childInstallation2.add(new String[]{"Ampere Phase 1", "5.6", "Ampere"});
        childInstallation2.add(new String[]{"Ampere Phase 2", "5.8", "Ampere"});
        childInstallation2.add(new String[]{"Ampere Phase 3", "5.4", "Ampere"});
        childInstallation2.add(new String[]{"Power during test", "14.67", "kW"});
        childInstallation2.add(new String[]{"Frequency charge", "51.3", "Hz"});

        List<String[]> childInstallation3 = new ArrayList<>();
        childInstallation3.add(new String[]{"Frequency charge", "55.7", "Hz"});

        List<String[]> childExhaust1 = new ArrayList<>();
        childExhaust1.add(new String[]{"Temperature", "76.7", "\u00B0C"});
        childExhaust1.add(new String[]{"Pressure", "4.34", "Mbar"});

        List<String[]> childWorkingHours = new ArrayList<>();
        childWorkingHours.add(new String[]{"Working hours", "56.3", "Hours"});

        listMotorDataChild.put(listMotorDataHeader.get(0), childMotor1);
        listMotorDataChild.put(listMotorDataHeader.get(1), childMotor2);
        listMotorDataChild.put(listMotorDataHeader.get(2), childMotor3);
        listMotorDataChild.put(listMotorDataHeader.get(3), childInstallation1);
        listMotorDataChild.put(listMotorDataHeader.get(4), childInstallation2);
        listMotorDataChild.put(listMotorDataHeader.get(5), childInstallation3);
        listMotorDataChild.put(listMotorDataHeader.get(6), childExhaust1);
        listMotorDataChild.put(listMotorDataHeader.get(7), childWorkingHours);
    }
}
