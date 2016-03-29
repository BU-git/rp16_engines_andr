package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bionic.kvt.serviceapp.R;

public class InsertSignaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insert_signatures);

        Button butonSend = (Button) findViewById(R.id.button_send);
        butonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send
            }
        });
    }
}
