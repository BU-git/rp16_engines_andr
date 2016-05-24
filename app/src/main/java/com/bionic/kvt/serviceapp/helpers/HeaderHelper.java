package com.bionic.kvt.serviceapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;

public class HeaderHelper {
    private Context mContext;

    public HeaderHelper(Context context) {
        this.mContext = context;
    }

    public void setHeader() {
        if (mContext instanceof Activity) {
            ImageView mImageView = (ImageView) ((Activity) mContext).findViewById(R.id.home_image);
            mImageView.setImageResource(R.drawable.header);

            ImageView mImageLogoView = (ImageView) ((Activity) mContext).findViewById(R.id.logo_image);
            mImageLogoView.setImageResource(R.drawable.logo);

            TextView mHeaderTextView = (TextView) ((Activity) mContext).findViewById(R.id.header_text);
        }
    }
}
