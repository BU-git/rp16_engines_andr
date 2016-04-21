package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_CLIENT;
import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_ENGINEER;

public class NSAActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsa);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @OnClick(R.id.nsa_save_button)
    public void onSaveClick(View v) {
        Utils.cleanSignatureFile(SIGNATURE_FILE_ENGINEER);
        Utils.cleanSignatureFile(SIGNATURE_FILE_CLIENT);
        Intent intent = new Intent(getApplicationContext(), InsertSignaturesActivity.class);
        startActivity(intent);
    }
}
