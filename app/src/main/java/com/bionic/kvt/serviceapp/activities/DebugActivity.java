package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.api.User;
import com.bionic.kvt.serviceapp.db.DbUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebugActivity extends BaseActivity {
    private final List<User> userListOnServer = new ArrayList<>();
    private final List<OrderBrief> orderBriefList = new ArrayList<>();
    private Order order;

    @Bind(R.id.connection_order_id)
    EditText orderIdInput;
    @Bind(R.id.synchronisation_log)
    TextView synchronisationLog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        ButterKnife.bind(this);

        showApplicationLog();
    }

    @OnClick(R.id.reset_user_db)
    public void onResetUserBDClick(View v) {
        DbUtils.resetUserTable();
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("User DB reset done!");
        showApplicationLog();
    }

    @OnClick(R.id.refresh_log)
    public void onRefreshLogClick(View v) {
        showApplicationLog();
    }

    @OnClick(R.id.connection_get_users)
    public void onGetUsersClick(View v) {
        getUserList();
    }

    @OnClick(R.id.connection_get_orders_brief)
    public void onGetOrdersBriefClick(View v) {
        getOrdersBriefList();
    }

    @OnClick(R.id.connection_get_order)
    public void onGetOrderClick(View v) {
        final long orderId;
        if ("".equals(orderIdInput.getText().toString())) {
            orderId = 0;
        } else {
            orderId = Long.valueOf(orderIdInput.getText().toString());
        }
        getOrderById(orderId);
    }

    private void getUserList() {
        final Call<List<User>> userListRequest = Session.getServiceConnection().getAllUsers();

        addLogMessage("Getting user list from " + userListRequest.request());

        userListRequest.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(final Call<List<User>> call,
                                   final Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userListOnServer.clear();
                    userListOnServer.addAll(response.body());
                    addLogMessage("Request successful! Get " + userListOnServer.size() + " users.");
                    for (User user : userListOnServer) {
                        addLogMessage("User:" + user.toString());
                    }
                } else {
                    addLogMessage("User request error: " + response.code());
                }
            }

            @Override
            public void onFailure(final Call<List<User>> call, final Throwable t) {
                addLogMessage("User request fail: " + t.toString());
            }
        });
    }

    private void getOrdersBriefList() {
        final Call<List<OrderBrief>> userListRequest =
                Session.getServiceConnection().getOrdersBrief(Session.getEngineerId());

        addLogMessage("Getting orders brief list from " + userListRequest.request());

        userListRequest.enqueue(new Callback<List<OrderBrief>>() {
            @Override
            public void onResponse(final Call<List<OrderBrief>> call,
                                   final Response<List<OrderBrief>> response) {
                if (response.isSuccessful()) {
                    orderBriefList.clear();
                    orderBriefList.addAll(response.body());
                    addLogMessage("Request successful! Get " + orderBriefList.size() + " orders.");
                    for (OrderBrief order : orderBriefList) {
                        addLogMessage("Getting orders brief list:" + order.toString());
                    }
                } else {
                    addLogMessage("Orders brief list request error: " + response.code());
                }
            }

            @Override
            public void onFailure(final Call<List<OrderBrief>> call, final Throwable t) {
                addLogMessage("Orders brief list request fail: " + t.toString());
            }
        });
    }

    private void getOrderById(long id) {
        final Call<Order> orderRequest =
                Session.getServiceConnection().getOrder(id, Session.getEngineerId());

        addLogMessage("Getting order from " + orderRequest.request());

        orderRequest.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(final Call<Order> call, final Response<Order> response) {
                if (response.isSuccessful()) {
                    order = response.body();
                    addLogMessage("Request successful! Get order: " + order.getNumber());
                    addLogMessage(order.toString());
                } else {
                    addLogMessage("Order error: " + response.code());
                }
            }

            @Override
            public void onFailure(final Call<Order> call, final Throwable t) {
                addLogMessage("Order request fail: " + t.toString());
            }
        });
    }

    private void showApplicationLog() {
        final StringBuilder sb = new StringBuilder("Application log:");
        for (String logLine : Session.getSessionLog()) {
            sb.append("\n").append(logLine);
        }
        synchronisationLog.setText(sb.toString());
    }

    private void addLogMessage(String message) {
        String logText = synchronisationLog.getText().toString();
        logText = logText + "\n[TEST] ==> " + message;
        synchronisationLog.setText(logText);
    }
}
