package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderWorkScreenActivity extends BaseActivity {
    @BindView(R.id.process_order_page_hint)
    TextView nextButtonHint;

    @BindView(R.id.order_processing_first_stage_next_button)
    Button nextButton;

    @BindView(R.id.order_processing_first_stage_instructions_checkbox)
    CheckBox checkBoxInstructions;

    @BindView(R.id.order_processing_first_stage_lmra_checkbox)
    CheckBox checkBoxLMRA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_work_screen);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.preparations));
    }

    @OnClick(R.id.order_processing_first_stage_lmra_button)
    public void onLMRAClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LMRAActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.order_processing_first_stage_next_button)
    public void onNextClick(View v) {
        Intent intent;

        if (DbUtils.isCustomTemplate(Session.getCurrentOrder())) {
            intent = new Intent(getApplicationContext(), CustomTemplateActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), ComponentListActivity.class);
        }
        startActivity(intent);
    }

    @OnClick({R.id.order_processing_first_stage_instructions_checkbox,
            R.id.order_processing_first_stage_lmra_checkbox})
    public void onCheckboxClick(View v) {
        nextButtonEnableHintDisable();
    }

    private void nextButtonEnableHintDisable() {
        nextButton.setEnabled(checkBoxInstructions.isChecked() && checkBoxLMRA.isChecked());
        if (nextButton.isEnabled()) {
            nextButtonHint.setVisibility(View.INVISIBLE);
        } else {
            nextButtonHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextButtonEnableHintDisable();
    }
}
