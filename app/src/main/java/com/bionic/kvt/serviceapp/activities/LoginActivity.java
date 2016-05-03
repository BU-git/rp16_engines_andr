package com.bionic.kvt.serviceapp.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.User;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static com.bionic.kvt.serviceapp.BuildConfig.IS_LOGGING_ON;

public class LoginActivity extends BaseActivity
//        implements
//        LoaderCallbacks<Cursor>,
//        SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final int REQUEST_ACCESS_NETWORK_STATE = 0;

    private final String TAG = this.getClass().getName();
    private UserLoginTask mAuthTask = null;

    @Bind(R.id.connection_status)
    TextView mConnectionStatusText;

    @Bind(R.id.email)
    AutoCompleteTextView mEmailView;

    @Bind(R.id.password)
    EditText mPasswordView;

    @Bind(R.id.login_progress)
    View mProgressView;

    @Bind(R.id.login_form)
    View mLoginFormView;

    private View mLoginLayout;
    private SharedPreferences userSharedPreferences;

    private class UserRequestResult {
        boolean isSuccessful;
        String message;

        public UserRequestResult(boolean isSuccessful, String message) {
            this.isSuccessful = isSuccessful;
            this.message = message;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        HeaderHelper headerHelper = new HeaderHelper(this);
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
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestNetworkStatePermission();
        } else {
            if (!Utils.isNetworkConnected(LoginActivity.this)) {
                Toast.makeText(LoginActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(v.getContext(), ForgetPasswordActivity.class));
                //Disable animation
                overridePendingTransition(0, 0);
            }
        }
    }

    //Requests network permissions, if needed
    private boolean requestNetworkStatePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)) {
            Snackbar.make(mLoginLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_NETWORK_STATE}, REQUEST_ACCESS_NETWORK_STATE);
                        }
                    });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_ACCESS_NETWORK_STATE);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_ACCESS_NETWORK_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Stub for network check
            }
        }
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

    /**
     * Shows the progress UI and hides the login form.
     */
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

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        mEmailView.setAdapter(adapter);
//    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Map<String, ?> all = sharedPreferences.getAll();
//        Toast.makeText(getApplicationContext(), "name:" + all, Toast.LENGTH_SHORT).show();
//    }

//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private UserRequestResult userRequestResult;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            userRequestResult = getUserFromServer(mEmail);
            return DbUtils.isUserLoginValid(mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            mConnectionStatusText.setText(userRequestResult.message);

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

                Intent orderPageIntent = new Intent(LoginActivity.this, OrderPageActivity.class);
                startActivity(orderPageIntent);
            } else {
                if (userRequestResult.isSuccessful)
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

    private UserRequestResult getUserFromServer(final String email) {
        if (!Utils.isNetworkConnected(LoginActivity.this)) {
            if (IS_LOGGING_ON) Session.addToSessionLog("No connection to network.");
            return new UserRequestResult(true, "No connection to network. Offline login only.");
        }

        final Call<User> userRequest =
                Session.getServiceConnection().getUser(email);

        if (IS_LOGGING_ON)
            Session.addToSessionLog("Connecting to server: " + userRequest.request());

        final Response<User> userResponse;
        try {
            userResponse = userRequest.execute();
        } catch (IOException e) {
            if (IS_LOGGING_ON)
                Session.addToSessionLog("User request fail: " + e.toString());
            return new UserRequestResult(true, "User request fail: " + e.toString());
        }

        if (!userResponse.isSuccessful()) { // Request unsuccessful
            if (IS_LOGGING_ON)
                Session.addToSessionLog("Error connecting to server: " + userResponse.code());
            return new UserRequestResult(true, "Error connecting to server: " + userResponse.code());
        }

        if (userResponse.body() == null) {
            if (IS_LOGGING_ON)
                Session.addToSessionLog("Connection successful. Empty response.");
            return new UserRequestResult(true, "Connection successful. Empty response.");
        }

        if (userResponse.body().getEmail() == null) { // No such user on server
            DbUtils.deleteUser(email); // Deleting if we have local user
            if (IS_LOGGING_ON)
                Session.addToSessionLog("Connection successful. No user found: " + email);
            return new UserRequestResult(false, "The entered e-mail address is not known.\nIf you are sure, please call the administrator.");
        }

        // We have this user on server
        DbUtils.updateUserFromServer(userResponse.body());
        if (IS_LOGGING_ON)
            Session.addToSessionLog("Connection successful. User found: " + email);
        return new UserRequestResult(true, "Connection successful. User found.");
    }

}