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
import com.bionic.kvt.serviceapp.adapters.MeasurementsExpListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bionic.kvt.serviceapp.adapters.MeasurementsExpListAdapter.*;

public class MeasurementsActivity extends BaseActivity {
    @Bind(R.id.measurements_motor_exp_list_view)
    ExpandableListView expMotorListView;

    private MeasurementsExpListAdapter listMotorAdapter;
    private List<String> listMotorDataHeader;
    private HashMap<String, List<MeasurementsItem>> listMotorDataChild;

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

        listMotorAdapter = new MeasurementsExpListAdapter(this, listMotorDataHeader, listMotorDataChild);
        expMotorListView.setAdapter(listMotorAdapter);

        expMotorListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                currentGroupPosition = groupPosition;
                currentChildPosition = childPosition;
                String dialogTitle = listMotorDataChild.get(listMotorDataHeader.get(groupPosition)).get(childPosition).getItemName() + ":";
                String currentValue = listMotorDataChild.get(listMotorDataHeader.get(groupPosition)).get(childPosition).getItemValue();
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
            listMotorDataChild.get(listMotorDataHeader.get(currentGroupPosition)).get(currentChildPosition).setItemValue(newMeasurement);
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

        // Adding head data
        final String[] headerArray = getResources().getStringArray(R.array.MeasurementsHeader);
        Collections.addAll(listMotorDataHeader, headerArray);

        // Adding child data
        final String[] itemsNameArray = getResources().getStringArray(R.array.MeasurementsItemName);
        final String[] itemsUnitArray = getResources().getStringArray(R.array.MeasurementsItemUnit);

        List<MeasurementsItem> childMotor0 = new ArrayList<>();
        childMotor0.add(new MeasurementsItem(itemsNameArray[0], itemsUnitArray[0]));
        listMotorDataChild.put(listMotorDataHeader.get(0), childMotor0);

        List<MeasurementsItem> childMotor1 = new ArrayList<>();
        childMotor1.add(new MeasurementsItem(itemsNameArray[1], itemsUnitArray[1]));
        childMotor1.add(new MeasurementsItem(itemsNameArray[2], itemsUnitArray[2]));
        childMotor1.add(new MeasurementsItem(itemsNameArray[3], itemsUnitArray[3]));
        childMotor1.add(new MeasurementsItem(itemsNameArray[4], itemsUnitArray[4]));
        listMotorDataChild.put(listMotorDataHeader.get(1), childMotor1);

        List<MeasurementsItem> childMotor2 = new ArrayList<>();
        childMotor2.add(new MeasurementsItem(itemsNameArray[6], itemsUnitArray[5]));
        childMotor2.add(new MeasurementsItem(itemsNameArray[7], itemsUnitArray[6]));
        childMotor2.add(new MeasurementsItem(itemsNameArray[7], itemsUnitArray[7]));
        listMotorDataChild.put(listMotorDataHeader.get(2), childMotor2);

        List<MeasurementsItem> childInstallation3 = new ArrayList<>();
        childInstallation3.add(new MeasurementsItem(itemsNameArray[8], itemsUnitArray[8]));
        listMotorDataChild.put(listMotorDataHeader.get(3), childInstallation3);

        List<MeasurementsItem> childInstallation4 = new ArrayList<>();
        childInstallation4.add(new MeasurementsItem(itemsNameArray[9], itemsUnitArray[9]));
        childInstallation4.add(new MeasurementsItem(itemsNameArray[10], itemsUnitArray[10]));
        childInstallation4.add(new MeasurementsItem(itemsNameArray[11], itemsUnitArray[11]));
        childInstallation4.add(new MeasurementsItem(itemsNameArray[12], itemsUnitArray[12]));
        childInstallation4.add(new MeasurementsItem(itemsNameArray[13], itemsUnitArray[13]));
        childInstallation4.add(new MeasurementsItem(itemsNameArray[14], itemsUnitArray[14]));
        listMotorDataChild.put(listMotorDataHeader.get(4), childInstallation4);

        List<MeasurementsItem> childInstallation5 = new ArrayList<>();
        childInstallation5.add(new MeasurementsItem(itemsNameArray[15], itemsUnitArray[15]));
        listMotorDataChild.put(listMotorDataHeader.get(5), childInstallation5);

        List<MeasurementsItem> childExhaust6 = new ArrayList<>();
        childExhaust6.add(new MeasurementsItem(itemsNameArray[16], itemsUnitArray[16]));
        childExhaust6.add(new MeasurementsItem(itemsNameArray[17], itemsUnitArray[17]));
        listMotorDataChild.put(listMotorDataHeader.get(6), childExhaust6);

        List<MeasurementsItem> childWorkingHours7 = new ArrayList<>();
        childWorkingHours7.add(new MeasurementsItem(itemsNameArray[18], itemsUnitArray[18]));
        listMotorDataChild.put(listMotorDataHeader.get(7), childWorkingHours7);
    }
}
