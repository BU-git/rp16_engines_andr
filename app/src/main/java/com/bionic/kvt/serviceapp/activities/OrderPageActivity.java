package com.bionic.kvt.serviceapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.LocalService;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bionic.kvt.serviceapp.GlobalConstants.*;

public class OrderPageActivity extends BaseActivity implements
        OrderAdapter.OnOrderLineClickListener,
        OrderAdapter.OnPDFButtonClickListener,
        LocalService.Callbacks {

    private OrderAdapter ordersAdapter;
    private LocalService connectionService;

    @Bind(R.id.order_update_status)
    TextView orderUpdateStatusText;

    @Bind(R.id.order_page_search_view)
    SearchView searchView;

    @Bind(R.id.service_engineer_id)
    TextView engineerId;

    @Bind(R.id.orders_recycler_view)
    RecyclerView ordersRecyclerView;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            connectionService = binder.getService();
            connectionService.registerClient(OrderPageActivity.this);

            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("Order page service connected.");

            // Running service task
            connectionService.runTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            OrderPageActivity.this.connectionService = null;

            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("Order page service disconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
        ButterKnife.bind(this);

        // Generating OrderOverviewList
        DbUtils.updateOrderOverviewList();

        //Configuring Search view
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doOrdersSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                doOrdersSearch("");
                return false;
            }
        });

        ((AutoCompleteTextView) searchView
                .findViewById(searchView
                        .getContext()
                        .getResources()
                        .getIdentifier("android:id/search_src_text", null, null))
        ).setTextSize(14);

        // Configuring engineer Id
        engineerId.setText(Session.getEngineerName() + " (" + Session.getEngineerEmail() + ")");

        // Configuring Recycler View
        ordersRecyclerView.setHasFixedSize(true);
        GridLayoutManager ordersLayoutManager =
                new GridLayoutManager(this, ORDER_OVERVIEW_COLUMN_COUNT + 3);
        ordersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int cell = position % ORDER_OVERVIEW_COLUMN_COUNT;
                if (cell == 2 || cell == 3 || cell == 4) return 2;
                return 1;
            }
        });
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        // Showing all orders
        ordersAdapter = new OrderAdapter(getApplicationContext(), Session.getOrderOverviewList());
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.log_out:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                Session.clearSession();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.show_log:
                intent = new Intent(getApplicationContext(), DebugActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Session.setCurrentOrder(0L);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectionService != null) {
            connectionService.stopTask();
            unbindService(serviceConnection);
        }
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        // Setting selected Order to current session
        final long currentOrderNumber = Session.getOrderOverviewList().
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        // Setting selected Order to current session
        final long currentOrderNumber = Session.getOrderOverviewList().
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        if (Utils.needRequestWritePermission(getApplicationContext(), this)) return;

        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.REQUEST_WRITE_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
                startActivity(intent);
            }
        }
    }

    private void doOrdersSearch(String query) {
        if ("".equals(query)) {
            ordersAdapter.setOrdersDataSet(Session.getOrderOverviewList());
        } else {
            List<OrderOverview> searchOrderOverview = new ArrayList<>();
            for (OrderOverview oneOrder : Session.getOrderOverviewList()) {
                if (oneOrder.getNumber().toString().contains(query)) {
                    searchOrderOverview.add(oneOrder);
                }
            }
            ordersAdapter.setOrdersDataSet(searchOrderOverview);
        }
        ordersAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateUpdateStatus(String message) {
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        orderUpdateStatusText.setText("[" + time + "] " + message);
    }

    @Override
    public void updateOrderAdapter() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Update OrderAdapter data.");
        DbUtils.updateOrderOverviewList();
        ordersAdapter.setOrdersDataSet(Session.getOrderOverviewList());
        ordersAdapter.notifyDataSetChanged();
    }
}
