package com.bionic.kvt.serviceapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity {

    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;

    @BindView(R.id.password_reset_status)
    TextView passwordResetStatus;

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
        } else if (!Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error. Focus the first form field with an error.
            focusView.requestFocus();
        } else {

            PasswordResetTask passwordResetTask = new PasswordResetTask(email);
            passwordResetTask.execute((Void) null);
        }
    }


    public class PasswordResetTask extends AsyncTask<Void, Void, Void> {

        private final String mEmail;
        private Utils.ServerRequestResult serverRequestResult;

        PasswordResetTask(String email) {
            mEmail = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!Utils.isNetworkConnected(ForgetPasswordActivity.this)) {
                Session.addToSessionLog("No connection to network.  Cannot request new password.");
                serverRequestResult = new Utils.ServerRequestResult(false, "No connection to network. Cannot request new password.");
                return null;
            }

            serverRequestResult = Utils.getUserFromServer(mEmail);
            if (!serverRequestResult.isSuccessful()) return null;

            serverRequestResult = Utils.requestPasswordReset(mEmail);

            return null;
        }

        @Override
        protected void onPostExecute(final Void success) {
            passwordResetStatus.setText(serverRequestResult.getMessage());
        }

    }

}
