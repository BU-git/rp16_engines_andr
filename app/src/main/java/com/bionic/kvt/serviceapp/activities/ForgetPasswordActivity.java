package com.bionic.kvt.serviceapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.helpers.MailHelper;

public class ForgetPasswordActivity extends BaseActivity {
    public static final String TAG = ForgetPasswordActivity.class.getName();
    private MailHelper mailHelper;
    private boolean sentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        HeaderHelper headerHelper = new HeaderHelper(this);
        headerHelper.setHeader();

        final AutoCompleteTextView mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        Button button = (Button) findViewById(R.id.email_forget_pass_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
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
            });
        }
    }

    private boolean isEmailValid(String email) {
        //Email Contains @, // TODO: 4/8/2016 replace with a valid validator
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
