package com.bionic.kvt.serviceapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.HeaderHelper;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        HeaderHelper headerHelper = new HeaderHelper(this);
        headerHelper.setHeader();

    }
}
