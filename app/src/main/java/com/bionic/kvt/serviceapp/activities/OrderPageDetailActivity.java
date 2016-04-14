package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

public class OrderPageDetailActivity extends AppCompatActivity {
    private ToggleButton acceptButton;
    private Button startButton;
    private View orderAcceptInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);

        final long orderNumber = Session.getCurrentOrderNumber();
        acceptButton = (ToggleButton) findViewById(R.id.service_engenieer_accept_toggleButton);
        startButton = (Button) findViewById(R.id.service_engenieer_start_button);
        orderAcceptInstructions = findViewById(R.id.process_order_page_accept_instructions);

//        if ("Completed".equals(Session.getOrderStatus())) {
//            acceptButton.setVisibility(View.GONE);
//            startButton.setVisibility(View.GONE);
//            orderAcceptInstructions.setVisibility(View.GONE);
//            findViewById(R.id.process_order_page_order_complete).setVisibility(View.VISIBLE);
//        }

        if (orderNumber != 0L) {
//            ((TextView) findViewById(R.id.process_order_page_detail_order_number_data)).setText(orderNumber);


            findViewById(R.id.service_engenieer_start_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), OrderProcessingFirstStageActivity.class);
                    startActivity(intent);
                }
            });

        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(acceptButton.isChecked());
                if (acceptButton.isChecked()) {
                    orderAcceptInstructions.setVisibility(View.INVISIBLE);
                } else {
                    orderAcceptInstructions.setVisibility(View.VISIBLE);
                }
            }
        });

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
