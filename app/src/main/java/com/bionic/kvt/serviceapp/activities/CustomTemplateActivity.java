package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.CustomTemplate;
import com.bionic.kvt.serviceapp.db.Components.CustomTemplateElement;
import com.bionic.kvt.serviceapp.utils.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class CustomTemplateActivity extends BaseActivity {
    @BindView(R.id.custom_template_content)
    LinearLayout linearLayoutView;

    private String generatedTagPrefix; // View Tag prefix
    private String customTemplateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_template);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(CustomTemplateActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + CustomTemplateActivity.class.getSimpleName());

        generatedTagPrefix = String.valueOf(Session.getCurrentOrder()) + "_";

        inflateCustomTemplate();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && customTemplateName != null)
            actionBar.setSubtitle(customTemplateName);
    }

    @OnClick(R.id.custom_template_next_button)
    public void onNextClick(View v) {
        final Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCustomTemplateData();
    }

    private void inflateCustomTemplate() {
        try (final Realm realm = Realm.getDefaultInstance()) {
            AppLog.serviceI("Generating custom template layout");

            final CustomTemplate customTemplate = realm.where(CustomTemplate.class).equalTo("number", Session.getCurrentOrder()).findFirst();
            if (customTemplate == null) {
                AppLog.E(this, "No custom template found for order : " + Session.getCurrentOrder());
                return;
            }

            customTemplateName = customTemplate.getCustomTemplateName();

            int countForIds = 0;

            // Calculate 16dp padding
            final float scale = getResources().getDisplayMetrics().density;
            final int padding = (int) (16 * scale + 0.5f);

            final LinearLayout.LayoutParams labelLayout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final LinearLayout.LayoutParams editTextLayout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final LinearLayout.LayoutParams checkBoxLayout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            checkBoxLayout.topMargin = padding;

            final LinearLayout.LayoutParams editTextAreaLayout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final LinearLayout.LayoutParams textViewLayout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewLayout.topMargin = padding;

            TextView mLabel;
            EditText mEditText;
            CheckBox mCheckBox;
            EditText mEditTextArea;
            TextView mTextView;

            for (CustomTemplateElement customTemplateElement : customTemplate.getCustomTemplateElements()) {
                switch (customTemplateElement.getElementType()) {
                    case GlobalConstants.CUSTOM_ELEMENT_TEXT_FIELD:
                        mLabel = new TextView(CustomTemplateActivity.this);
                        mLabel.setLayoutParams(labelLayout);
                        mLabel.setText(customTemplateElement.getElementText());
                        mLabel.setPadding(0, padding, 0, 0);
                        linearLayoutView.addView(mLabel);

                        mEditText = new EditText(CustomTemplateActivity.this);
                        mEditText.setTag(generatedTagPrefix + countForIds++);
                        mEditText.setLayoutParams(editTextLayout);
                        mEditText.setText(customTemplateElement.getElementValue());
                        mEditText.setBackgroundColor(ContextCompat.getColor(CustomTemplateActivity.this, R.color.colorSecondaryBackground));
                        mEditText.setLines(1);
                        linearLayoutView.addView(mEditText);
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_CHECK_BOX:
                        mCheckBox = new CheckBox(CustomTemplateActivity.this);
                        mCheckBox.setTag(generatedTagPrefix + countForIds++);
                        mCheckBox.setLayoutParams(checkBoxLayout);
                        if ("true".equals(customTemplateElement.getElementValue())) {
                            mCheckBox.setChecked(true);
                        } else {
                            mCheckBox.setChecked(false);
                        }
                        mCheckBox.setText(customTemplateElement.getElementText());
                        linearLayoutView.addView(mCheckBox);
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_TEXT_AREA:
                        mLabel = new TextView(CustomTemplateActivity.this);
                        mLabel.setLayoutParams(labelLayout);
                        mLabel.setText(customTemplateElement.getElementText());
                        mLabel.setPadding(0, padding, 0, 0);
                        linearLayoutView.addView(mLabel);

                        mEditTextArea = new EditText(CustomTemplateActivity.this);
                        mEditTextArea.setTag(generatedTagPrefix + countForIds++);
                        mEditTextArea.setLayoutParams(editTextAreaLayout);
                        mEditTextArea.setBackgroundColor(ContextCompat.getColor(CustomTemplateActivity.this, R.color.colorSecondaryBackground));
                        mEditTextArea.setLines(5);
                        mEditTextArea.setGravity(Gravity.LEFT | Gravity.TOP);
                        mEditTextArea.setText(customTemplateElement.getElementValue());
                        linearLayoutView.addView(mEditTextArea);
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_LABEL:
                        mTextView = new TextView(CustomTemplateActivity.this);
                        mTextView.setTag(generatedTagPrefix + countForIds++);
                        mTextView.setLayoutParams(textViewLayout);
                        mTextView.setText(customTemplateElement.getElementText());
                        linearLayoutView.addView(mTextView);
                        break;
                }
            }

        }
    }

    private void saveCustomTemplateData() {
        try (final Realm realm = Realm.getDefaultInstance()) {
            AppLog.serviceI("Saving custom template data.");

            final CustomTemplate customTemplate = realm.where(CustomTemplate.class).equalTo("number", Session.getCurrentOrder()).findFirst();
            if (customTemplate == null) {
                AppLog.E(this, "No custom template found for order : " + Session.getCurrentOrder());
                return;
            }

            int countForIds = 0;

            EditText mEditText;
            CheckBox mCheckBox;
            EditText mEditTextArea;

            realm.beginTransaction();
            for (CustomTemplateElement customTemplateElement : customTemplate.getCustomTemplateElements()) {
                switch (customTemplateElement.getElementType()) {
                    case GlobalConstants.CUSTOM_ELEMENT_TEXT_FIELD:
                        mEditText = (EditText) linearLayoutView.findViewWithTag(generatedTagPrefix + countForIds++);
                        customTemplateElement.setElementValue(mEditText.getText().toString());
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_CHECK_BOX:
                        mCheckBox = (CheckBox) linearLayoutView.findViewWithTag(generatedTagPrefix + countForIds++);
                        if (mCheckBox.isChecked()) {
                            customTemplateElement.setElementValue("true");
                        } else {
                            customTemplateElement.setElementValue("false");
                        }
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_TEXT_AREA:
                        mEditTextArea = (EditText) linearLayoutView.findViewWithTag(generatedTagPrefix + countForIds++);
                        customTemplateElement.setElementValue(mEditTextArea.getText().toString());
                        break;

                    case GlobalConstants.CUSTOM_ELEMENT_LABEL:
                        countForIds++; //Important. To save Tag names order
                        break;
                }
            }
            realm.commitTransaction();
        }

    }
}
