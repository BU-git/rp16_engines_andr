package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bionic.kvt.serviceapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NSAActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsa);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.nsa_save_button)
    public void onSaveClick(View v) {
        Intent intent = new Intent(getApplicationContext(), InsertSignaturesActivity.class);
        startActivity(intent);
    }
}
