package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.utils.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity for detail order information.<br>
 * Started by {@link OrderPageDetailActivity}.<br>
 * Next activity {@link ComponentListActivity} or {@link CustomTemplateActivity}.<br>
 * Can start {@link LMRAActivity}.
 * <p/>
 */

public class OrderWorkActivity extends BaseActivity {
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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.preparations));

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(OrderWorkActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + OrderWorkActivity.class.getSimpleName());
    }

    @OnClick(R.id.order_processing_first_stage_lmra_button)
    public void onLMRAClick(View v) {
        final Intent intent = new Intent(getApplicationContext(), LMRAActivity.class);
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
