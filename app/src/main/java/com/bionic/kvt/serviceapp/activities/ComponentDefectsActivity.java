package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComponentDefectsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_defects);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @OnClick(R.id.component_defects_next_button)
    public void onNextClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
        startActivity(intent);
    }
}
