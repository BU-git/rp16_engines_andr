package com.bionic.kvt.serviceapp.db;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class LocalService extends Service {

    private boolean isSyncing;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
