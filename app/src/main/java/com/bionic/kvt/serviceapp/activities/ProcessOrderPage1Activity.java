package com.bionic.kvt.serviceapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;

public class ProcessOrderPage1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_order_page1);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("order_number");
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
        }


    }
}
