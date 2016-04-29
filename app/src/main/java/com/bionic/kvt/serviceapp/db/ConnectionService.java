package com.bionic.kvt.serviceapp.db;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bionic.kvt.serviceapp.BuildConfig.IS_LOGGING_ON;

public class ConnectionService extends Service {

    private static final long UPDATE_PERIOD = 60_000; // 60 sec

    private final Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();
    private Callbacks orderActivity;

    public class LocalBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Callback interface for communication with service client - OrderPageActivity
    public interface Callbacks {
        void updateUpdateStatus(String message);

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
        if (!Utils.isNetworkConnected(getApplicationContext())) {
            serviceLogging("No connection to network. Canceling update.");
            return;
        }

        // TODO: THIS FLAG IS NOT WORKING!
        Session.setIsSyncingFromServer(true);
        if (IS_LOGGING_ON) Session.addToSessionLog("Updating orders.");

        final Call<List<OrderBrief>> orderBriefListRequest =
                Session.getServiceConnection().getOrdersBrief(Session.getEngineerId());

        if (IS_LOGGING_ON)
            Session.addToSessionLog("Getting orders brief list from: " + orderBriefListRequest.request());

        orderBriefListRequest.enqueue(
                new Callback<List<OrderBrief>>() {
                    @Override
                    public void onResponse(final Call<List<OrderBrief>> call,
                                           final Response<List<OrderBrief>> response) {
                        if (!response.isSuccessful()) {
                            serviceLogging("Orders brief list request error: " + response.code());
                            Session.setIsSyncingFromServer(false);
                            return;
                        }

                        serviceLogging("Request successful. Get " + response.body().size() + " brief orders.");

                        List<OrderBrief> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(response.body());

                        if (ordersToBeUpdated.isEmpty()) {
                            serviceLogging("Nothing to update.");
                            Session.setIsSyncingFromServer(false);
                            return;
                        }

                        serviceLogging("Getting orders from server.");
                        for (OrderBrief orderBrief : ordersToBeUpdated) {
                            updateOrderFomServer(orderBrief.getNumber(), Session.getEngineerId());
                        }

                        serviceLogging("Orders update complete!");

                        // TODO: THIS FLAG IS NOT WORKING!
                        Session.setIsSyncingFromServer(false);
                    }

                    @Override
                    public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                        serviceLogging("Orders brief list request fail: " + t.toString());
                        Session.setIsSyncingFromServer(false);
                    }
                }

        );
    }

    private void updateOrderFomServer(long orderNumber, String userId) {
        final Call<Order> orderRequest =
                Session.getServiceConnection().getOrder(orderNumber, userId);

        if (IS_LOGGING_ON)
            Session.addToSessionLog("Getting order from: " + orderRequest.request());

        orderRequest.enqueue(
                new Callback<Order>() {
                    @Override
                    public void onResponse(final Call<com.bionic.kvt.serviceapp.api.Order> call,
                                           final Response<Order> response) {
                        if (!response.isSuccessful()) {
                            serviceLogging("Order request error: " + response.code());
                            return;
                        }

                        if (IS_LOGGING_ON) Session.addToSessionLog("Request successful!");
                        DbUtils.updateOrderFromServer(response.body());

                        orderActivity.updateOrderAdapter();
                    }

                    @Override
                    public void onFailure(final Call<Order> call, final Throwable t) {
                        serviceLogging("Order request fail: " + t.toString());
                    }
                }
        );
    }

    private void serviceLogging(String message) {
        if (IS_LOGGING_ON) Session.addToSessionLog(message);
        orderActivity.updateUpdateStatus(message);
    }
}
