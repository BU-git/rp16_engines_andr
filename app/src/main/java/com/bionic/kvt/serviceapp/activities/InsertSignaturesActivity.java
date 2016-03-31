package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
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

        Button buttonEngineerSignature = (Button) findViewById(R.id.button_engineer_signature);
        buttonEngineerSignature.setOnClickListener(new SignatureOnClickListener());

        Button buttonClientSignature = (Button) findViewById(R.id.buton_client_signature);
        buttonClientSignature.setOnClickListener(new SignatureOnClickListener());

        Button butonSend = (Button) findViewById(R.id.button_send);
        butonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send
            }
        });
    }

    private class SignatureOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //start Draw Signature Activity
            Intent intent = new Intent(InsertSignaturesActivity.this, DrawSignatureActivity.class);
            startActivity(intent);
        }
    }
}