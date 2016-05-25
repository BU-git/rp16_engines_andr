package com.bionic.kvt.serviceapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;

/**
 * An activity for login in to application.<br>
 * Next activity {@link OrderPageActivity} or {@link ForgetPasswordActivity}
 * <p/>
 * Required android {@code INTERNET} permission.<br>
 * If network is available, application will validate
 * login and password on server (no offline login check).<br>
 * If login is successful - user data will be stored for offline login.<br>
 * If network is not available, offline check will be executed.
 * <p/>
 * If user never login in application than network connection to server required.<br>
 * Otherwise user login will fail (Because no offline user data available yet).
 */

public class LoginActivity extends BaseActivity {
    private UserLoginTask mAuthTask = null;

    @BindView(R.id.connection_status)
    TextView mConnectionStatusText;

    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.login_progress)
    View mProgressView;

    @BindView(R.id.login_form)
    View mLoginFormView;

    private SharedPreferences userSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        AppLog.serviceI("Create activity: " + LoginActivity.class.getSimpleName());

        final HeaderHelper headerHelper = new HeaderHelper(this);
        headerHelper.setHeader();
    }

    // Restore saved password, if any
    @OnFocusChange(R.id.email)
    public void onEmailFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            userSharedPreferences = getSharedPreferences(mEmailView.getText().toString(), Context.MODE_PRIVATE);
            if (userSharedPreferences.getBoolean("isPasswordSaved", false)) {
                mPasswordView.setText(userSharedPreferences.getString("password", ""));
            }
        }
    }

    @OnEditorAction(R.id.password)
    public boolean onPasswordEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.email_sign_in_button)
    public void onSingInClick(View view) {
        attemptLogin();
    }

    @OnClick(R.id.forget_password_button)
    public void onForgetPasswordClick(View v) {
        if (!Utils.isNetworkConnected(this)) {
            AppLog.W(this, getText(R.string.no_connection).toString());
            return;
        }

        startActivity(new Intent(v.getContext(), ForgetPasswordActivity.class));
        //Disable animation
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session.clearSession();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Utils.ServerRequestResult serverRequestResult;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!Utils.isNetworkConnected(LoginActivity.this)) {
                AppLog.serviceI("No connection to network. Offline login only.");
                serverRequestResult = new Utils.ServerRequestResult(true, "No connection to network. Offline login only.");
            } else {
                serverRequestResult = Utils.getUserFromServer(mEmail);
            }

            return DbUtils.isUserLoginValid(mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            mConnectionStatusText.setText(serverRequestResult.getMessage());

            if (success) {
                final CheckBox mCheckBox = (CheckBox) findViewById(R.id.login_checkbox);

                if (mCheckBox != null && mCheckBox.isChecked()) {
                    //Get shared preferences
                    userSharedPreferences = getSharedPreferences(mEmailView.getText().toString(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor userEditor = userSharedPreferences.edit();

                    //Put data to shared preferences
                    userEditor.putString("user", mEmail);
                    userEditor.putString("password", mPassword);
                    userEditor.putBoolean("isPasswordSaved", true);
                    userEditor.apply();
                }

                DbUtils.setUserSession(mEmail);
                startActivity(new Intent(LoginActivity.this, OrderPageActivity.class));
            } else {
                if (serverRequestResult.isSuccessful())
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

}