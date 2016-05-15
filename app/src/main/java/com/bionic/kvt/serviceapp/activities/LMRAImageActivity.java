package com.bionic.kvt.serviceapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.bionic.kvt.serviceapp.R;

import butterknife.ButterKnife;

/** */
public class LMRAImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra_image);
        ButterKnife.bind(this);

        ImageButton closeActivityButton = (ImageButton) findViewById(R.id.lmra_image_close);
        closeActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
