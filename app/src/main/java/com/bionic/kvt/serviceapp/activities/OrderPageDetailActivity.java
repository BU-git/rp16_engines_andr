package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_START_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_IN_PROGRESS;

public class OrderPageDetailActivity extends BaseActivity {
    @BindView(R.id.service_engenieer_accept_toggleButton)
    ToggleButton acceptButton;

    @BindView(R.id.service_engenieer_start_button)
    Button startButton;

    @BindView(R.id.process_order_page_accept_instructions)
    TextView orderAcceptInstructions;

    @BindView(R.id.process_order_page_order_complete)
    TextView orderIsComplete;

    @BindView(R.id.process_order_page_detail_order_number)
    TextView orderNumber;

    @BindView(R.id.process_order_page_detail_relation)
    TextView orderRelation;

    @BindView(R.id.process_order_page_detail_town)
    TextView orderTown;

    @BindView(R.id.process_order_page_detail_telephone)
    TextView orderPhone;

    @BindView(R.id.process_order_page_detail_employee)
    TextView orderEmployee;

    @BindView(R.id.process_order_page_detail_order_date)
    TextView orderDate;

    @BindView(R.id.process_order_page_detail_reference)
    TextView orderReference;

    @BindView(R.id.process_order_page_detail_installation)
    TextView orderInstallation;

    @BindView(R.id.process_order_page1_device_town)
    TextView orderInstallationTown;

    @BindView(R.id.instructions_text)
    TextView orderInstuctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.order_data));

        final Realm realm = Realm.getDefaultInstance();
        final Order currentOrder =
                realm.where(Order.class).equalTo("number", Session.getCurrentOrder()).findFirst();
        if (currentOrder == null) {
            Toast.makeText(getApplicationContext(), "No such order in database!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentOrder.getOrderStatus() == ORDER_STATUS_COMPLETE) {
            acceptButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            orderAcceptInstructions.setVisibility(View.GONE);
            orderIsComplete.setVisibility(View.VISIBLE);
        }

        // Setting Order data to textView
        //TODO PROPER NULL HANDLING
        try {
            orderNumber.setText(Long.toString(currentOrder.getNumber()));
            orderRelation.setText(currentOrder.getRelation().getName());
            orderTown.setText(currentOrder.getRelation().getTown());
            orderPhone.setText(currentOrder.getRelation().getTelephone());
            orderEmployee.setText(currentOrder.getEmployee().getName());
            orderDate.setText(Utils.getDateStringFromDate(currentOrder.getDate()));
            orderReference.setText(currentOrder.getReference());
            orderInstallation.setText(currentOrder.getInstallation().getName());
            orderInstallationTown.setText(currentOrder.getInstallation().getTown());

            orderInstuctions.setText(currentOrder.getNote());
        } catch (NullPointerException e) {
        }

        realm.close();
    }


    @OnClick(R.id.service_engenieer_start_button)
    public void onStartClick(View v) {
        DbUtils.setOrderMaintenanceTime(Session.getCurrentOrder(), ORDER_MAINTENANCE_START_TIME, new Date());
        DbUtils.setOrderStatus(Session.getCurrentOrder(), ORDER_STATUS_IN_PROGRESS);
        // TODO SEND STATUS UPDATE TO SERVER

        Intent intent = new Intent(getApplicationContext(), OrderWorkScreenActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.service_engenieer_accept_toggleButton)
    public void onAcceptClick(View v) {
        startButton.setEnabled(acceptButton.isChecked());
        if (acceptButton.isChecked()) {
            orderAcceptInstructions.setVisibility(View.INVISIBLE);
        } else {
            orderAcceptInstructions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acceptButton.isChecked()) {
            startButton.setEnabled(true);
            orderAcceptInstructions.setVisibility(View.INVISIBLE);
        }
    }
}
