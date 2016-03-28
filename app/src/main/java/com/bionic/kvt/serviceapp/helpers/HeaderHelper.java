package com.bionic.kvt.serviceapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;

public class HeaderHelper{
    private Context mContext;
    private ImageView mImageView;
    private ImageView mImageLogoView;
    private TextView mHeaderTextView;

    public HeaderHelper(Context context){
        this.mContext = context;
    }

    public void setHeader(){
        if (mContext instanceof Activity){
            mImageView = (ImageView) ((Activity) mContext).findViewById(R.id.home_image);
            mImageView.setImageResource(R.drawable.header2015);

            mImageLogoView = (ImageView) ((Activity) mContext).findViewById(R.id.logo_image);
            mImageLogoView.setImageResource(R.drawable.logo);

            mHeaderTextView = (TextView) ((Activity) mContext).findViewById(R.id.header_text);
        }
    }
}
