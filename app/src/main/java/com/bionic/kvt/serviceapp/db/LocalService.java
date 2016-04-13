package com.bionic.kvt.serviceapp.db;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.models.OrderBrief;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalService extends Service {
    private boolean isSyncing = false;
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

    public void updateOrders() {
        if (isSyncing) return;
        isSyncing = true;

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Updating orders.");

        getOrdersBriefListFromServer();
    }

    private void getOrdersBriefListFromServer() {
        final String currentUser = Session.getSession().getEngineerEmail();
        final Call<List<OrderBrief>> orderBriefListRequest = Session.getOrderServiceConnection().getOrdersBrief(currentUser);

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Getting orders brief list from: " + orderBriefListRequest.request());

        orderBriefListRequest.enqueue(new Callback<List<OrderBrief>>() {
            @Override
            public void onResponse(final Call<List<OrderBrief>> call, final Response<List<OrderBrief>> response) {
                if (response.isSuccessful()) {
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Request successful! Get " + response.body().size() + "brief orders.");

                    List<OrderBrief> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(response.body());

                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Getting orders from server.");

                    for (OrderBrief orderBrief : ordersToBeUpdated) {
                        getOrderFomServer(orderBrief.getNumber(), currentUser);
                    }

                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Orders update compleete!");
                    isSyncing = false;

                } else {
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Orders brief list request error: " + response.code());
                    isSyncing = false;
                }
            }

            @Override
            public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.getSession().addLog("Orders brief list request fail: " + t.toString());
                isSyncing = false;
            }
        });
    }


    private void getOrderFomServer(long orderNumber, String user) {
        final Call<com.bionic.kvt.serviceapp.models.Order> orderRequest = Session.getOrderServiceConnection().getOrder(orderNumber, user);

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Getting order from: " + orderRequest.request());

        orderRequest.enqueue(new Callback<com.bionic.kvt.serviceapp.models.Order>() {
            @Override
            public void onResponse(final Call<com.bionic.kvt.serviceapp.models.Order> call,
                                   final Response<com.bionic.kvt.serviceapp.models.Order> response) {
                if (response.isSuccessful()) {
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Request successful!");

                    DbUtils.updateOrderTableFromServer(response.body());

                } else {
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Order request error: " + response.code());
                }
            }

            @Override
            public void onFailure(final Call<com.bionic.kvt.serviceapp.models.Order> call, final Throwable t) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.getSession().addLog("Order request fail: " + t.toString());
            }
        });
    }

}
