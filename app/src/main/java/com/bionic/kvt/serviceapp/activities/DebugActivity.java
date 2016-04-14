package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.api.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebugActivity extends BaseActivity {
    private List<User> userListOnServer = new ArrayList<>();
    private List<OrderBrief> orderBriefList = new ArrayList<>();
    private Order order;

    private EditText orderIdInput;
    private TextView synchronisationLog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        synchronisationLog = (TextView) findViewById(R.id.synchronisation_log);

        final Button refreshLog = (Button) findViewById(R.id.refresh_log);
        refreshLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showApplicationLog();
            }
        });


        final Button getUsers = (Button) findViewById(R.id.connection_get_users);
        getUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserList();
            }
        });


        final Button getOrdersBriefList = (Button) findViewById(R.id.connection_get_orders_brief);
        getOrdersBriefList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrdersBriefList();
            }
        });


        orderIdInput = (EditText) findViewById(R.id.connection_order_id);
        final Button getOrdersById = (Button) findViewById(R.id.connection_get_order);
        getOrdersById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long orderId;
                if ("".equals(orderIdInput.getText().toString())) {
                    orderId = 0;
                } else {
                    orderId = Long.valueOf(orderIdInput.getText().toString());
                }
                getOrderById(orderId);
            }
        });

        showApplicationLog();
    }

    private void getUserList() {
        final Call<List<User>> userListRequest =
                Session.getOrderServiceConnection().getAllUsers();

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
                Session.getOrderServiceConnection().getOrdersBrief(Session.getEngineerId());

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
                Session.getOrderServiceConnection().getOrder(id, Session.getEngineerId());

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
