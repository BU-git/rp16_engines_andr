package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

public class OrderProcessingFirstStageActivity extends AppCompatActivity {
    CheckBox checkBoxInstructions;
    CheckBox checkBoxLMRA;
    Button nextButton;
    TextView nextButtonHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processing_first_stage);

        //Navigation to LMRA
        Button registerDangerous = (Button) findViewById(R.id.order_processing_first_stage_lmra_button);
        registerDangerous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LMRAActivity.class);
                startActivity(intent);
            }
        });

        nextButtonHint = (TextView) findViewById(R.id.process_order_page_hint);

        nextButton = (Button) findViewById(R.id.order_processing_first_stage_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ComponentDefectsActivity.class);
                startActivity(intent);
            }
        });


        checkBoxInstructions = (CheckBox) findViewById(R.id.order_processing_first_stage_instructions_checkbox);
        checkBoxLMRA = (CheckBox) findViewById(R.id.order_processing_first_stage_lmra_checkbox);
        checkBoxInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonEnableHintDisable();
            }
        });
        checkBoxLMRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonEnableHintDisable();
            }
        });
    }

    private void nextButtonEnableHintDisable() {
        nextButton.setEnabled(checkBoxInstructions.isChecked() && checkBoxLMRA.isChecked());
        if (nextButton.isEnabled()) {
            nextButtonHint.setVisibility(View.INVISIBLE);
        } else nextButtonHint.setVisibility(View.VISIBLE);

        Session.getSession().setCheckBoxInstructions(checkBoxInstructions.isChecked());
        Session.getSession().setCheckBoxLMRA(checkBoxLMRA.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBoxInstructions.setChecked(Session.getSession().isCheckBoxInstructions());
        checkBoxLMRA.setChecked(Session.getSession().isCheckBoxLMRA());
        nextButtonEnableHintDisable();

    }
}
