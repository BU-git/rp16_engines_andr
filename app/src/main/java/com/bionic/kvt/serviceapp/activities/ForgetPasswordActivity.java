package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.helpers.MailHelper;
import com.bionic.kvt.serviceapp.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Boolean> {
    private static final int MAIL_LOADER_ID = 2;
    private MailHelper mailHelper;

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
        } else if (!Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            mailHelper = new MailHelper();
            mailHelper.setRecipient(email);
            mailHelper.setMessageBody(getText(R.string.forget_password_body).toString());
            mailHelper.setSubject(getText(R.string.forget_password_subject).toString());

            getSupportLoaderManager().restartLoader(MAIL_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        return new MailHelper.SendMail(this, mailHelper);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data) {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.success_email_toast), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.error_email_toast), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        // NOOP
    }

}
