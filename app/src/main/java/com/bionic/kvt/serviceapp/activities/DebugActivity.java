package com.bionic.kvt.serviceapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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
import com.bionic.kvt.serviceapp.helpers.MailHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebugActivity extends BaseActivity {
    private final List<OrderBrief> orderBriefList = new ArrayList<>();
    private Order order;
    private static final int MAIL_LOADER_ID = 2;

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
    public void onResetUserDBClick(View v) {
        DbUtils.resetUserTable();
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("User DB reset done!");
        showApplicationLog();
    }

    @OnClick(R.id.reset_order_db)
    public void onResetOrderDBClick(View v) {
        DbUtils.resetOrderTableWithSubTables();
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Order DB reset done!");
        showApplicationLog();
    }

    @OnClick(R.id.send_log_by_email)
    public void onRefreshLogClick(View v) {
        sentLogByEmail();
    }

    private void sentLogByEmail() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getText(R.string.email_dialog_title));
        final EditText emailEdit = new EditText(this);
        emailEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        dialogBuilder.setView(emailEdit);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmailWithLog(emailEdit.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    private void sendEmailWithLog(String email) {
        MailHelper mailHelper = new MailHelper();
        mailHelper.setRecipient(email);
        mailHelper.setMessageBody(Session.getSessionLog().toString());
        mailHelper.setSubject("KVT Service Log");

        new MailHelper.SendMail(this, mailHelper);
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
