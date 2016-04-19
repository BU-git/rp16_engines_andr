package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bionic.kvt.serviceapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeasurementsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.measurements_next_button)
    public void onNextClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NSAActivity.class);
        startActivity(intent);
    }
}
