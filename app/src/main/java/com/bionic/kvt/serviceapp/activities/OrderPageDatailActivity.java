package com.bionic.kvt.serviceapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;

public class OrderPageDatailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("order_number");

            ((TextView) findViewById(R.id.process_order_page_detail_order_number_data)).setText(value);
        }


    }
}
