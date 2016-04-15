package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderPageDetailActivity extends BaseActivity {
    @Bind(R.id.service_engenieer_accept_toggleButton)
    ToggleButton acceptButton;

    @Bind(R.id.service_engenieer_start_button)
    Button startButton;

    @Bind(R.id.process_order_page_accept_instructions)
    TextView orderAcceptInstructions;

    @Bind(R.id.process_order_page_order_complete)
    TextView orderIsComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == null) return;

        if (Session.getCurrentOrder().getOrderStatus() == Session.ORDER_STATUS_COMPLETE) {
            acceptButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            orderAcceptInstructions.setVisibility(View.GONE);
            orderIsComplete.setVisibility(View.VISIBLE);
        }

        // Setting Order data to textView
//            ((TextView) findViewById(R.id.process_order_page_detail_order_number_data)).setText(orderNumber);

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
