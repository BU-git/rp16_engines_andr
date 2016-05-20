package com.bionic.kvt.serviceapp.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.utils.AppLog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LMRAImageActivity extends Activity {
    @BindView(R.id.lmra_image)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra_image);
        ButterKnife.bind(this);
        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + LMRAImageActivity.class.getSimpleName());

        imageView.setImageURI(Uri.fromFile(new File(getIntent().getStringExtra("imageFile"))));
        new PhotoViewAttacher(imageView);
    }

    @OnClick(R.id.lmra_image_close)
    public void onClick(View v) {
        finish();
    }
}
