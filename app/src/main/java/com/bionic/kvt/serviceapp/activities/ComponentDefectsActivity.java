package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bionic.kvt.serviceapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComponentDefectsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_defects);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.component_defects_next_button)
    public void onNextClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
        startActivity(intent);
    }
}
