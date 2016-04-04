package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bionic.kvt.serviceapp.R;

public class ComponentDefectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_defects);

        Button nextButton = (Button) findViewById(R.id.component_defects_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
                startActivity(intent);
            }
        });
    }
}
