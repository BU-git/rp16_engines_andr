package com.bionic.kvt.serviceapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.helpers.MailHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity {
    private MailHelper mailHelper;
    private boolean sentStatus;

    @Bind(R.id.email)
    AutoCompleteTextView mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

        HeaderHelper headerHelper = new HeaderHelper(this);
        headerHelper.setHeader();
    }

    @OnClick(R.id.email_forget_pass_button)
    public void onClick(View v) {
        // Reset errors.
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mailHelper = new MailHelper();
            mailHelper.setRecepient(email);
            mailHelper.setBody(getText(R.string.forget_password_body).toString());
            mailHelper.setSubject(getText(R.string.forget_password_subject).toString());

            new SendMail().execute();
            if (sentStatus) {
                Toast.makeText(ForgetPasswordActivity.this, getText(R.string.success_email_toast), Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(ForgetPasswordActivity.this, getText(R.string.no_connection), Toast.LENGTH_SHORT);
            }

        }
    }

    private boolean isEmailValid(String email) {
        //Email Contains @
        return email.contains("@");
    }

    private class SendMail extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                mailHelper.send();
                sentStatus = true;
            } catch (Exception e) {
                e.printStackTrace();
                sentStatus = false;
            }
            return null;
        }
    }
}
