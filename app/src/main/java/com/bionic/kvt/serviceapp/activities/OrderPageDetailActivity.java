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
import com.bionic.kvt.serviceapp.db.Order;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class OrderPageDetailActivity extends BaseActivity {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Bind(R.id.service_engenieer_accept_toggleButton)
    ToggleButton acceptButton;

    @Bind(R.id.service_engenieer_start_button)
    Button startButton;

    @Bind(R.id.process_order_page_accept_instructions)
    TextView orderAcceptInstructions;

    @Bind(R.id.process_order_page_order_complete)
    TextView orderIsComplete;

    @Bind(R.id.process_order_page_detail_order_number)
    TextView orderNumber;

    @Bind(R.id.process_order_page_detail_relation)
    TextView orderRelation;

    @Bind(R.id.process_order_page_detail_town)
    TextView orderTown;

    @Bind(R.id.process_order_page_detail_telephone)
    TextView orderPhone;

    @Bind(R.id.process_order_page_detail_employee)
    TextView orderEmployee;

    @Bind(R.id.process_order_page_detail_order_date)
    TextView orderDate;

    @Bind(R.id.process_order_page_detail_reference)
    TextView orderReference;

    @Bind(R.id.process_order_page_detail_installation)
    TextView orderInstallation;

    @Bind(R.id.process_order_page1_device_town)
    TextView orderInstallationTown;

    @Bind(R.id.instructions_text)
    TextView orderInstuctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Realm realm = Realm.getDefaultInstance();
        final Order currentOrder =
                realm.where(Order.class).equalTo("number", Session.getCurrentOrder()).findFirst();
        if (currentOrder == null) return;

        if (currentOrder.getOrderStatus() == Session.ORDER_STATUS_COMPLETE) {
            acceptButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            orderAcceptInstructions.setVisibility(View.GONE);
            orderIsComplete.setVisibility(View.VISIBLE);
        }

        // Setting Order data to textView
        try {
            orderNumber.setText(Long.toString(currentOrder.getNumber()));
            orderRelation.setText(currentOrder.getRelation().getName());
            orderTown.setText(currentOrder.getRelation().getTown());
            orderPhone.setText(currentOrder.getRelation().getTelephone());
            orderEmployee.setText(currentOrder.getEmployee().getName());
            orderDate.setText(simpleDateFormat.format(currentOrder.getDate()));
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
        Intent intent = new Intent(getApplicationContext(), OrderProcessingFirstStageActivity.class);
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
