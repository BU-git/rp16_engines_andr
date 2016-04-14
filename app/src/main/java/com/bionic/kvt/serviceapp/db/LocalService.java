package com.bionic.kvt.serviceapp.db;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalService extends Service {
    private static final long UPDATE_PERIOD = 60_000; // 60 sec

    private final Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();
    private Callbacks orderActivity;

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

    //Callback interface for communication with service client - OrderPageActivity
    public interface Callbacks {
        void updateUpdateStatus(String message, int visibility);
    }

    //Here Order Activity register to the service as Callbacks client
    public void registerClient(Activity activity) {
        this.orderActivity = (Callbacks) activity;
    }

    //Periodic update from server
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateOrders();
            handler.postDelayed(this, UPDATE_PERIOD);
        }
    };

    // Public method for activity.
    // Start synchronisation loop.
    public void runTask() {
        updateOrders();
        handler.postDelayed(updateTask, UPDATE_PERIOD);
    }

    // Public method for activity.
    // Start synchronisation loop.
    // Do not terminate current update cycle.
    public void stopTask() {
        handler.removeCallbacks(updateTask);
    }

    private void updateOrders() {
        //Exit if currently syncing
        if (Session.isSyncingFromServer()) return;

        Session.setIsSyncingFromServer(true);
        serviceLogging("Updating orders.", View.VISIBLE);
        getOrdersBriefListFromServer();
    }

    private void getOrdersBriefListFromServer() {
        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getOrderServiceConnection().getOrdersBrief(Session.getEngineerId());

        serviceLogging("Getting orders brief list from: " + orderBriefListRequest.request());
        orderBriefListRequest.enqueue(new Callback<List<OrderBrief>>() {
            @Override
            public void onResponse(final Call<List<OrderBrief>> call,
                                   final Response<List<OrderBrief>> response) {
                if (response.isSuccessful()) {
                    serviceLogging("Request successful. Get " + response.body().size() + "brief orders.");

                    List<OrderBrief> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(response.body());

                    if (ordersToBeUpdated.size() == 0) {
                        serviceLogging("Nothing to update.", View.INVISIBLE);
                        Session.setIsSyncingFromServer(false);
                        return;
                    }

                    serviceLogging("Getting orders from server.");
                    for (OrderBrief orderBrief : ordersToBeUpdated) {
                        getOrderFomServer(orderBrief.getNumber(), Session.getEngineerId());
                    }

                    serviceLogging("Orders update complete!", View.INVISIBLE);
                    Session.setIsSyncingFromServer(false);
                } else {
                    serviceLogging("Orders brief list request error: " + response.code(), View.INVISIBLE);
                    Session.setIsSyncingFromServer(false);
                }
            }

            @Override
            public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                serviceLogging("Orders brief list request fail: " + t.toString(), View.INVISIBLE);
                Session.setIsSyncingFromServer(false);
            }
        });
    }

    private void getOrderFomServer(long orderNumber, String userId) {
        final Call<Order> orderRequest =
                Session.getOrderServiceConnection().getOrder(orderNumber, userId);

        serviceLogging("Getting order from: " + orderRequest.request());

        orderRequest.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(final Call<com.bionic.kvt.serviceapp.api.Order> call,
                                   final Response<Order> response) {
                if (response.isSuccessful()) {
                    serviceLogging("Request successful!");
                    DbUtils.updateOrderTableFromServer(response.body());
                } else {
                    serviceLogging("Order request error: " + response.code());
                }
            }

            @Override
            public void onFailure(final Call<Order> call, final Throwable t) {
                serviceLogging("Order request fail: " + t.toString());
            }
        });
    }

    private void serviceLogging(String message, int... visibility) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog(message);
        if (visibility.length > 0)
            orderActivity.updateUpdateStatus(message, visibility[0]);
    }
}
