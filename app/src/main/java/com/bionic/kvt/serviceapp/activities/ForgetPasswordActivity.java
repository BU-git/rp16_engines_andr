package com.bionic.kvt.serviceapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;

public class ForgetPasswordActivity extends AppCompatActivity {
    public static final String TAG = ForgetPasswordActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        HeaderHelper headerHelper = new HeaderHelper(this);
        headerHelper.setHeader();

        Button button = (Button) findViewById(R.id.email_forget_pass_button);
    }
}
