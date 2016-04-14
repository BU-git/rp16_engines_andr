package com.bionic.kvt.serviceapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

public class OrderPageActivity extends AppCompatActivity implements
        OrderAdapter.OnOrderLineClickListener,
        OrderAdapter.OnPDFButtonClickListener,
        LocalService.Callbacks {

    private OrderAdapter ordersAdapter;
    private RecyclerView ordersRecyclerView;

    private LocalService connectionService;
    private boolean serviceBound = false;
    private TextView orderUpdatingText;
    private TextView orderUpdateStatusText;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            connectionService = binder.getService();
            serviceBound = true;

            connectionService.registerClient(OrderPageActivity.this);

            if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Order page service connected.");

            // Running service task
            connectionService.runTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            OrderPageActivity.this.connectionService = null;
            serviceBound = false;

            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("Order page service disconnected.");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

//        DbUtils.resetOrderTableWithSubTables();

        orderUpdatingText = (TextView) findViewById(R.id.order_updating);
        orderUpdateStatusText = (TextView) findViewById(R.id.order_update_status);

        //Configuring Search view
        SearchView searchView = (SearchView) findViewById(R.id.order_page_search_view);
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

        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(
                searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setTextSize(14);


        // Configuring Log out button
        Button logOut = (Button) findViewById(R.id.service_engineer_logout_button);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Session.clearSession();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        // Configuring engineer Id
        TextView engineerId = (TextView) findViewById(R.id.service_engineer_id);
        engineerId.setText(Session.getEngineerName() + " (" + Session.getEngineerEmail() + ")");

        // Configuring Recycler View
        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager ordersLayoutManager =
                new GridLayoutManager(this, Session.ORDER_OVERVIEW_COLUMN_COUNT);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        // Generating OrderOverviewList
        DbUtils.updateOrderOverviewList();

        // Showing all orders
        ordersAdapter = new OrderAdapter(Session.getOrderOverviewList());
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);

        if (BuildConfig.IS_LOGGING_ON) Session.addToSessionLog("Order page created.");
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

    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (serviceBound) {
            connectionService.stopTask();
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        // Setting selected Order number to current session
        Session.setCurrentOrderNumber(
                Session.getOrderOverviewList().
                        get(position / Session.ORDER_OVERVIEW_COLUMN_COUNT).getNumber()
        );

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        // Setting selected Order number to current session
        Session.setCurrentOrderNumber(
                Session.getOrderOverviewList().
                        get(position / Session.ORDER_OVERVIEW_COLUMN_COUNT).getNumber()
        );

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
//        ordersRecyclerView.swapAdapter(ordersAdapter, false);
    }

    @Override
    public void updateUpdateStatus(String message, int visibility) {
        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        orderUpdateStatusText.setText("[" + time + "] " + message);
        orderUpdatingText.setVisibility(visibility);
    }
}
