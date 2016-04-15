package com.bionic.kvt.serviceapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
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
import butterknife.OnClick;

public class OrderPageActivity extends BaseActivity implements
        OrderAdapter.OnOrderLineClickListener,
        OrderAdapter.OnPDFButtonClickListener,
        LocalService.Callbacks {

    private OrderAdapter ordersAdapter;
    private LocalService connectionService;

    @Bind(R.id.order_updating)
    TextView orderUpdatingText;

    @Bind(R.id.order_update_status)
    TextView orderUpdateStatusText;

    @Bind(R.id.order_page_search_view)
    SearchView searchView;

    @Bind(R.id.service_engineer_id)
    TextView engineerId;

    @Bind(R.id.orders_recycler_view)
    RecyclerView ordersRecyclerView;

    private ServiceConnection serviceConnection = new ServiceConnection() {

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

        Session.setDemoData();
//        DbUtils.resetOrderTableWithSubTables();

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
        RecyclerView.LayoutManager ordersLayoutManager =
                new GridLayoutManager(this, Session.ORDER_OVERVIEW_COLUMN_COUNT);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        // Showing all orders
        ordersAdapter = new OrderAdapter(getApplicationContext(), Session.getOrderOverviewList());
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }

    @OnClick(R.id.service_engineer_logout_button)
    public void onLogOutClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        Session.clearSession();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
        switch (id) {
            case R.id.show_log:
                Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Session.setCurrentOrder(null);
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
                get(position / Session.ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(DbUtils.getOrder(currentOrderNumber));

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        // Setting selected Order to current session
        final long currentOrderNumber = Session.getOrderOverviewList().
                get(position / Session.ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(DbUtils.getOrder(currentOrderNumber));

        if (Utils.needRequestWritePermission(getApplicationContext(), this)) return;

        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
    public void updateUpdateStatus(String message, int visibility) {
        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        orderUpdateStatusText.setText("[" + time + "] " + message);
        orderUpdatingText.setVisibility(visibility);
    }

    @Override
    public void updateOrderAdapter() {
        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Update OrderAdapter data.");
        DbUtils.updateOrderOverviewList();
        ordersAdapter.setOrdersDataSet(Session.getOrderOverviewList());
        ordersAdapter.notifyDataSetChanged();
    }
}
