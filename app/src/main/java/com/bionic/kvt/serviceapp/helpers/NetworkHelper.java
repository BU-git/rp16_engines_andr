package com.bionic.kvt.serviceapp.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkHelper {
    Context mContext;
    public NetworkHelper(Context mContext){
        this.mContext = mContext;
    }

    //Check if network is at least connected
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
