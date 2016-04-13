package com.bionic.kvt.serviceapp.db;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalService extends Service {
    private boolean isSyncing = false;
    private final Handler handler = new Handler();
    private static final long UPDATE_PERIOD = 30_000; // 60 sec

    private Callbacks orderActivity;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    //callbacks interface for communication with service client - OrderPageActivity!
    public interface Callbacks {
        public void updateUpdateStatus(String message);
    }

    //Here Order Activity register to the service as Callbacks client
    public void registerClient(Activity activity) {
        this.orderActivity = (Callbacks) activity;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateOrders();
            handler.postDelayed(this, UPDATE_PERIOD);
        }
    };

    public void runTask() {
        updateOrders();
        handler.postDelayed(updateTask, UPDATE_PERIOD);
    }

    public void stopTask() {
        handler.removeCallbacks(updateTask);
    }

    public void updateOrders() {
        if (isSyncing) return;
        isSyncing = true;

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Updating orders.");

        getOrdersBriefListFromServer();
    }

    private void getOrdersBriefListFromServer() {
        final String currentUserId = Utils.getUserIdFromEmail(Session.getSession().getEngineerEmail());
        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getOrderServiceConnection().getOrdersBrief(currentUserId);

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
                        getOrderFomServer(orderBrief.getNumber(), currentUserId);
                    }

                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Orders update complete!");
                    isSyncing = false;

                } else {
                    if (BuildConfig.IS_LOGGING_ON)
                        Session.getSession().addLog("Orders brief list request error: " + response.code());
                    orderActivity.updateUpdateStatus("[" + Session.getAndAddConnectionAttemptCount()+ "] Orders brief list request error: " + response.code());
                    isSyncing = false;
                }
            }

            @Override
            public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.getSession().addLog("Orders brief list request fail: " + t.toString());
                orderActivity.updateUpdateStatus("Orders brief list request fail: " + t.toString());
                isSyncing = false;
            }
        });
    }

    private void getOrderFomServer(long orderNumber, String userId) {
        final Call<Order> orderRequest =
                Session.getOrderServiceConnection().getOrder(orderNumber, userId);

        if (BuildConfig.IS_LOGGING_ON)
            Session.getSession().addLog("Getting order from: " + orderRequest.request());

        orderRequest.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(final Call<com.bionic.kvt.serviceapp.api.Order> call,
                                   final Response<Order> response) {
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
            public void onFailure(final Call<Order> call, final Throwable t) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.getSession().addLog("Order request fail: " + t.toString());
            }
        });
    }

}
