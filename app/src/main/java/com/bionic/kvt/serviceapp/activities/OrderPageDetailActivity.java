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

        final String orderNumber = ((Session) getApplication()).getOrderNumber();
        acceptButton = (ToggleButton) findViewById(R.id.service_engenieer_accept_toggleButton);
        startButton = (Button) findViewById(R.id.service_engenieer_start_button);
        orderAcceptInstructions = findViewById(R.id.process_order_page_accept_instructions);

        if (orderNumber != null) {
            ((TextView) findViewById(R.id.process_order_page_detail_order_number_data)).setText(orderNumber);


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
                startButton.setEnabled(true);
                orderAcceptInstructions.setVisibility(View.INVISIBLE);
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
