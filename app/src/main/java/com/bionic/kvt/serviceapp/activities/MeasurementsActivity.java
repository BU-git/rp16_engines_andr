package com.bionic.kvt.serviceapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.MeasurementsExpListAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.OrderReportMeasurements;
import com.bionic.kvt.serviceapp.utils.AppLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.bionic.kvt.serviceapp.adapters.MeasurementsExpListAdapter.MeasurementsItem;

/**
 * An activity for entering Measurements information.<br>
 * Started by {@link ComponentListActivity} o {@link CustomTemplateActivity}<br>
 * Next activity {@link JobRulesActivity}.
 */

public class MeasurementsActivity extends BaseActivity {
    @BindView(R.id.measurements_motor_exp_list_view)
    ExpandableListView expMotorListView;

    private MeasurementsExpListAdapter listMotorAdapter;
    private List<String> listMotorDataHeader;
    private HashMap<String, List<MeasurementsItem>> listMotorDataChild;

    private AlertDialog enterMeasurementsDialog;

    private int currentGroupPosition = -1;
    private int currentChildPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.measurements));

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(MeasurementsActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + MeasurementsActivity.class.getSimpleName());

        getMeasurementsData();

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
                dialog.dismiss();
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

        enterMeasurementsDialog = dialogBuilder.create();
        enterMeasurementsDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (enterMeasurementsDialog != null && enterMeasurementsDialog.isShowing())
            enterMeasurementsDialog.dismiss();
    }

    private void putMeasurementValue(final String newMeasurement) {
        if (currentGroupPosition >= 0 && currentChildPosition >= 0) {
            listMotorDataChild.get(listMotorDataHeader.get(currentGroupPosition)).get(currentChildPosition).setItemValue(newMeasurement);
            listMotorAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.measurements_next_button)
    public void onNextClick(View v) {
        saveMeasurements();

        final Intent intent = new Intent(getApplicationContext(), JobRulesActivity.class);
        startActivity(intent);
    }

    private void saveMeasurements() {
        OrderReportMeasurements currentMeasurements = new OrderReportMeasurements();
        currentMeasurements.setNumber(Session.getCurrentOrder());
        currentMeasurements.setMotorCompressionPressure(listMotorDataChild.get(listMotorDataHeader.get(0)).get(0).getItemValue());

        currentMeasurements.setMotorOilPressure(listMotorDataChild.get(listMotorDataHeader.get(1)).get(0).getItemValue());
        currentMeasurements.setMotorOilTemperature(listMotorDataChild.get(listMotorDataHeader.get(1)).get(1).getItemValue());
        currentMeasurements.setMotorOilType(listMotorDataChild.get(listMotorDataHeader.get(1)).get(2).getItemValue());
        currentMeasurements.setMotorOilManufacture(listMotorDataChild.get(listMotorDataHeader.get(1)).get(3).getItemValue());

        currentMeasurements.setMotorCoolantTemperature(listMotorDataChild.get(listMotorDataHeader.get(2)).get(0).getItemValue());
        currentMeasurements.setMotorCoolantAcidity(listMotorDataChild.get(listMotorDataHeader.get(2)).get(1).getItemValue());
        currentMeasurements.setMotorCoolantFrost(listMotorDataChild.get(listMotorDataHeader.get(2)).get(2).getItemValue());

        currentMeasurements.setInstallationEnvironmentTemperature(listMotorDataChild.get(listMotorDataHeader.get(3)).get(0).getItemValue());

        currentMeasurements.setInstallationTestVoltage(listMotorDataChild.get(listMotorDataHeader.get(4)).get(0).getItemValue());
        currentMeasurements.setInstallationTestAmperePhase1(listMotorDataChild.get(listMotorDataHeader.get(4)).get(1).getItemValue());
        currentMeasurements.setInstallationTestAmperePhase2(listMotorDataChild.get(listMotorDataHeader.get(4)).get(2).getItemValue());
        currentMeasurements.setInstallationTestAmperePhase3(listMotorDataChild.get(listMotorDataHeader.get(4)).get(3).getItemValue());
        currentMeasurements.setInstallationTestPower(listMotorDataChild.get(listMotorDataHeader.get(4)).get(4).getItemValue());
        currentMeasurements.setInstallationTestFrequency(listMotorDataChild.get(listMotorDataHeader.get(4)).get(5).getItemValue());

        currentMeasurements.setInstallationTestNoLoadFrequency(listMotorDataChild.get(listMotorDataHeader.get(5)).get(0).getItemValue());

        currentMeasurements.setExhaustGasesTemperature(listMotorDataChild.get(listMotorDataHeader.get(6)).get(0).getItemValue());
        currentMeasurements.setExhaustGasesPressure(listMotorDataChild.get(listMotorDataHeader.get(6)).get(1).getItemValue());

        currentMeasurements.setRunningHours(listMotorDataChild.get(listMotorDataHeader.get(7)).get(0).getItemValue());

        DbUtils.setOrderReportMeasurements(currentMeasurements);
    }

    private void getMeasurementsData() {
        listMotorDataHeader = new ArrayList<>();
        listMotorDataChild = new HashMap<>();

        // Adding head data
        final String[] headerArray = getResources().getStringArray(R.array.MeasurementsHeader);
        Collections.addAll(listMotorDataHeader, headerArray);

        // Adding child data
        final String[] itemsNameArray = getResources().getStringArray(R.array.MeasurementsItemName);
        final String[] itemsUnitArray = getResources().getStringArray(R.array.MeasurementsItemUnit);

        List<MeasurementsItem> child_0_Motor = new ArrayList<>();
        child_0_Motor.add(new MeasurementsItem(itemsNameArray[0], itemsUnitArray[0]));

        List<MeasurementsItem> child_1_Motor = new ArrayList<>();
        child_1_Motor.add(new MeasurementsItem(itemsNameArray[1], itemsUnitArray[1]));
        child_1_Motor.add(new MeasurementsItem(itemsNameArray[2], itemsUnitArray[2]));
        child_1_Motor.add(new MeasurementsItem(itemsNameArray[3], itemsUnitArray[3]));
        child_1_Motor.add(new MeasurementsItem(itemsNameArray[4], itemsUnitArray[4]));

        List<MeasurementsItem> child_2_Motor = new ArrayList<>();
        child_2_Motor.add(new MeasurementsItem(itemsNameArray[6], itemsUnitArray[5]));
        child_2_Motor.add(new MeasurementsItem(itemsNameArray[7], itemsUnitArray[6]));
        child_2_Motor.add(new MeasurementsItem(itemsNameArray[7], itemsUnitArray[7]));

        List<MeasurementsItem> child_3_Installation = new ArrayList<>();
        child_3_Installation.add(new MeasurementsItem(itemsNameArray[8], itemsUnitArray[8]));

        List<MeasurementsItem> child_4_Installation = new ArrayList<>();
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[9], itemsUnitArray[9]));
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[10], itemsUnitArray[10]));
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[11], itemsUnitArray[11]));
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[12], itemsUnitArray[12]));
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[13], itemsUnitArray[13]));
        child_4_Installation.add(new MeasurementsItem(itemsNameArray[14], itemsUnitArray[14]));

        List<MeasurementsItem> child_5_Installation = new ArrayList<>();
        child_5_Installation.add(new MeasurementsItem(itemsNameArray[15], itemsUnitArray[15]));

        List<MeasurementsItem> child_6_Exhaust = new ArrayList<>();
        child_6_Exhaust.add(new MeasurementsItem(itemsNameArray[16], itemsUnitArray[16]));
        child_6_Exhaust.add(new MeasurementsItem(itemsNameArray[17], itemsUnitArray[17]));

        List<MeasurementsItem> child_7_RunningHours = new ArrayList<>();
        child_7_RunningHours.add(new MeasurementsItem(itemsNameArray[18], itemsUnitArray[18]));

        // Filling saved data
        final Realm realm = Realm.getDefaultInstance();
        final OrderReportMeasurements currentMeasurements = realm.where(OrderReportMeasurements.class)
                .equalTo("number", Session.getCurrentOrder()).findFirst();
        if (currentMeasurements != null) {
            child_0_Motor.get(0).setItemValue(currentMeasurements.getMotorCompressionPressure());

            child_1_Motor.get(0).setItemValue(currentMeasurements.getMotorOilPressure());
            child_1_Motor.get(1).setItemValue(currentMeasurements.getMotorOilTemperature());
            child_1_Motor.get(2).setItemValue(currentMeasurements.getMotorOilType());
            child_1_Motor.get(3).setItemValue(currentMeasurements.getMotorOilManufacture());

            child_2_Motor.get(0).setItemValue(currentMeasurements.getMotorCoolantTemperature());
            child_2_Motor.get(1).setItemValue(currentMeasurements.getMotorCoolantAcidity());
            child_2_Motor.get(2).setItemValue(currentMeasurements.getMotorCoolantFrost());

            child_3_Installation.get(0).setItemValue(currentMeasurements.getInstallationEnvironmentTemperature());

            child_4_Installation.get(0).setItemValue(currentMeasurements.getInstallationTestVoltage());
            child_4_Installation.get(1).setItemValue(currentMeasurements.getInstallationTestAmperePhase1());
            child_4_Installation.get(2).setItemValue(currentMeasurements.getInstallationTestAmperePhase2());
            child_4_Installation.get(3).setItemValue(currentMeasurements.getInstallationTestAmperePhase3());
            child_4_Installation.get(4).setItemValue(currentMeasurements.getInstallationTestPower());
            child_4_Installation.get(5).setItemValue(currentMeasurements.getInstallationTestFrequency());

            child_5_Installation.get(0).setItemValue(currentMeasurements.getInstallationTestNoLoadFrequency());

            child_6_Exhaust.get(0).setItemValue(currentMeasurements.getExhaustGasesTemperature());
            child_6_Exhaust.get(1).setItemValue(currentMeasurements.getExhaustGasesPressure());

            child_7_RunningHours.get(0).setItemValue(currentMeasurements.getRunningHours());
        }
        realm.close();

        listMotorDataChild.put(listMotorDataHeader.get(0), child_0_Motor);
        listMotorDataChild.put(listMotorDataHeader.get(1), child_1_Motor);
        listMotorDataChild.put(listMotorDataHeader.get(2), child_2_Motor);
        listMotorDataChild.put(listMotorDataHeader.get(3), child_3_Installation);
        listMotorDataChild.put(listMotorDataHeader.get(4), child_4_Installation);
        listMotorDataChild.put(listMotorDataHeader.get(5), child_5_Installation);
        listMotorDataChild.put(listMotorDataHeader.get(6), child_6_Exhaust);
        listMotorDataChild.put(listMotorDataHeader.get(7), child_7_RunningHours);
    }
}
