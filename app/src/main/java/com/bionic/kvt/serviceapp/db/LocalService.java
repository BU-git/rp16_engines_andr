package com.bionic.kvt.serviceapp.db;

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
        void updateUpdateStatus(String message, int updateStatus);

        void updateOrderAdapter();
    }

    //Here Order Activity register to the service as Callbacks client
    public void registerClient(Callbacks callbacks) {
        this.orderActivity = callbacks;
    }

    //Periodic update from server
    private final Runnable updateTask = new Runnable() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }

    private void updateOrders() {
        // Exit if currently syncing
        if (Session.isSyncingFromServer()) return;

        // Is device connected to network
        if (!Utils.isConnected(getApplicationContext())) {
            serviceLogging("No connection to network.", Session.UPDATE_STATUS_WARRING);
            return;
        }

        Session.setIsSyncingFromServer(true);
        serviceLogging("Updating orders.", Session.UPDATE_STATUS_DEFAULT);
        getOrdersBriefListFromServer();
    }

    private void getOrdersBriefListFromServer() {
        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getOrderServiceConnection().getOrdersBrief(Session.getEngineerId());

        serviceLogging("Getting orders brief list from: " + orderBriefListRequest.request(),
                Session.UPDATE_STATUS_DEFAULT);

        orderBriefListRequest.enqueue(new Callback<List<OrderBrief>>() {
            @Override
            public void onResponse(final Call<List<OrderBrief>> call,
                                   final Response<List<OrderBrief>> response) {
                if (response.isSuccessful()) {
                    serviceLogging("Request successful. Get " + response.body().size() + "brief orders.",
                            Session.UPDATE_STATUS_OK);

                    List<OrderBrief> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(response.body());

                    if (ordersToBeUpdated.isEmpty()) {
                        serviceLogging("Nothing to update.", Session.UPDATE_STATUS_OK);
                        Session.setIsSyncingFromServer(false);
                        return;
                    }

                    serviceLogging("Getting orders from server.", Session.UPDATE_STATUS_DEFAULT);
                    for (OrderBrief orderBrief : ordersToBeUpdated) {
                        updateOrderFomServer(orderBrief.getNumber(), Session.getEngineerId());
                    }

                    serviceLogging("Orders update complete!", Session.UPDATE_STATUS_OK);
                    Session.setIsSyncingFromServer(false);

                    orderActivity.updateOrderAdapter();
                } else {
                    serviceLogging("Orders brief list request error: " + response.code(),
                            Session.UPDATE_STATUS_WARRING);
                    Session.setIsSyncingFromServer(false);

                }
            }

            @Override
            public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                serviceLogging("Orders brief list request fail: " + t.toString(), Session.UPDATE_STATUS_ERROR);
                Session.setIsSyncingFromServer(false);
            }
        });
    }

    private void updateOrderFomServer(long orderNumber, String userId) {
        final Call<Order> orderRequest =
                Session.getOrderServiceConnection().getOrder(orderNumber, userId);

        serviceLogging("Getting order from: " + orderRequest.request(), Session.UPDATE_STATUS_DEFAULT);

        orderRequest.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(final Call<com.bionic.kvt.serviceapp.api.Order> call,
                                   final Response<Order> response) {
                if (response.isSuccessful()) {
                    serviceLogging("Request successful!", Session.UPDATE_STATUS_DEFAULT);
                    DbUtils.updateOrderFromServer(response.body());
                } else {
                    serviceLogging("Order request error: " + response.code(),Session.UPDATE_STATUS_WARRING);
                }
            }

            @Override
            public void onFailure(final Call<Order> call, final Throwable t) {
                serviceLogging("Order request fail: " + t.toString(), Session.UPDATE_STATUS_ERROR);
            }
        });
    }

    private void serviceLogging(String message, int updateStatus) {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog(message);
        orderActivity.updateUpdateStatus(message, updateStatus);
    }
}
