package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

public class OrderPageDetailActivity extends AppCompatActivity {
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);

        final Session SESSION = (Session) getApplication();
        orderNumber = SESSION.getOrderNumber();

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

        (findViewById(R.id.service_engenieer_accept_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.service_engenieer_start_button).setEnabled(true);
                findViewById(R.id.process_order_page_detail_instructions).setVisibility(View.INVISIBLE);
            }
        });




    }




}
