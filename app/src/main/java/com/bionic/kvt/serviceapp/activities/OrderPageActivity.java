package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.db.OrderSynchronisation;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.AppLogItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.bionic.kvt.serviceapp.GlobalConstants.GENERATE_PART_MAP;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_OVERVIEW_COLUMN_COUNT;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE_UPLOADED;
import static com.bionic.kvt.serviceapp.GlobalConstants.PREPARE_FILES;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_ORDERS;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_ORDERS_STATUSES;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPLOAD_FILES;
import static com.bionic.kvt.serviceapp.utils.Utils.runBackgroundServiceIntent;

/**
 * An activity for overview all orders for current user.<br>
 * Started by {@link LoginActivity}.<br>
 * Next activity {@link OrderPageDetailActivity} or {@link PDFReportActivity}
 * <p/>
 * Allow to preview orders or PDF Reports.<br>
 * When activity starts it's start IntentService {@link com.bionic.kvt.serviceapp.db.BackgroundService}
 * with tasks:<br>
 * - Generate part map.<br>
 * - Prepare files to upload for orders in status {@link com.bionic.kvt.serviceapp.GlobalConstants#ORDER_STATUS_COMPLETE}.<br>
 * - Upload files for prepared order files.
 * <p/>
 * <p/>
 * It also register callbacks on data change in database for:<br>
 * - Orders list update.<br>
 * - Orders in status {@link com.bionic.kvt.serviceapp.GlobalConstants#ORDER_STATUS_COMPLETE}
 * to run background task {@link com.bionic.kvt.serviceapp.GlobalConstants#PREPARE_FILES}.<br>
 * - {@link OrderSynchronisation#isReadyForSync} to run background task
 * {@link com.bionic.kvt.serviceapp.GlobalConstants#UPLOAD_FILES}.<br>
 * - {@link OrderSynchronisation#isSyncComplete} to set order status
 * {@link com.bionic.kvt.serviceapp.GlobalConstants#ORDER_STATUS_COMPLETE_UPLOADED}.<br>
 * <p/>
 * {@code OnResume} start {@code Handler} for querying server for order update ones a minute.
 */

public class OrderPageActivity extends BaseActivity implements
        OrderAdapter.OnOrderLineClickListener,
        OrderAdapter.OnPDFButtonClickListener {

    private static final long UPDATE_PERIOD = 30_000; // 30 sec
    private List<OrderOverview> orderOverviewList = new ArrayList<>();
    private OrderAdapter ordersAdapter;
    private final Handler updateHandler = new Handler();

    private Realm monitorRealm;
    private RealmChangeListener<RealmResults<Order>> orderUpdateListener;
    private RealmResults<Order> ordersInDB;
    private RealmChangeListener<RealmResults<Order>> ordersCompleteListener;
    private RealmResults<Order> ordersCompleteInDB;
    private RealmChangeListener<RealmResults<OrderSynchronisation>> ordersSynchronisationListener;
    private RealmResults<OrderSynchronisation> ordersToSynchroniseInDB;
    private RealmChangeListener<RealmResults<OrderSynchronisation>> ordersSynchronisationCompleteListener;
    private RealmResults<OrderSynchronisation> ordersSynchroniseCompleteInDB;

    // App Log monitor
    private Realm monitorLogRealm = Session.getLogRealm();
    private RealmChangeListener<RealmResults<AppLogItem>> logListener;
    private RealmResults<AppLogItem> logsWithNotification;

    @BindView(R.id.order_page_search_view)
    SearchView searchView;

    @BindView(R.id.orders_recycler_view)
    RecyclerView ordersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
        ButterKnife.bind(this);
        AppLog.serviceI("Create activity: " + OrderPageActivity.class.getSimpleName());

        // Setting App log listener
        logListener = AppLog.setLogListener(OrderPageActivity.this, monitorLogRealm);
        logsWithNotification = AppLog.addListener(monitorLogRealm, logListener);

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
        ActionBar actionBar = getSupportActionBar();
        final String currentEngineer = getText(R.string.service_engineer) + " " + Session.getEngineerName() +
                " (" + Session.getEngineerEmail() + ")";
        if (actionBar != null) actionBar.setSubtitle(currentEngineer);

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
        ordersAdapter = new OrderAdapter(this, orderOverviewList);
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);

        // Creating Order update callback
        monitorRealm = Realm.getDefaultInstance();
        orderUpdateListener = new RealmChangeListener<RealmResults<Order>>() {
            @Override
            public void onChange(RealmResults<Order> orders) {
                updateOrderAdapter();
            }
        };

        ordersInDB = monitorRealm.where(Order.class)
                .equalTo("employeeEmail", Session.getEngineerEmail()).findAll();


        // Creating Order complete callback
        ordersCompleteListener = new RealmChangeListener<RealmResults<Order>>() {
            @Override
            public void onChange(RealmResults<Order> orders) {
                if (orders.size() > 0) {
                    runBackgroundServiceIntent(OrderPageActivity.this, PREPARE_FILES);
                }
            }
        };

        ordersCompleteInDB = monitorRealm.where(Order.class)
                .equalTo("orderStatus", ORDER_STATUS_COMPLETE).findAll();
        ordersCompleteInDB.addChangeListener(ordersCompleteListener);

        // Creating OrderSynchronisation readyForSync callback
        ordersSynchronisationListener = new RealmChangeListener<RealmResults<OrderSynchronisation>>() {
            @Override
            public void onChange(RealmResults<OrderSynchronisation> orders) {
                if (orders.size() > 0)
                    runBackgroundServiceIntent(OrderPageActivity.this, UPLOAD_FILES);
            }
        };

        ordersToSynchroniseInDB = monitorRealm.where(OrderSynchronisation.class)
                .equalTo("isReadyForSync", true).equalTo("isSyncComplete", false).equalTo("isError", false).findAll();
        ordersToSynchroniseInDB.addChangeListener(ordersSynchronisationListener);

        //TODO WHat with order with isError=true ?

        // Creating OrderSynchronisation SyncComplete callback
        ordersSynchronisationCompleteListener = new RealmChangeListener<RealmResults<OrderSynchronisation>>() {
            @Override
            public void onChange(RealmResults<OrderSynchronisation> orders) {
                for (OrderSynchronisation order : orders) {
                    if (DbUtils.getOrderStatus(order.getNumber()) < ORDER_STATUS_COMPLETE_UPLOADED)
                        DbUtils.setOrderStatus(order.getNumber(), ORDER_STATUS_COMPLETE_UPLOADED);
                }
                runBackgroundServiceIntent(OrderPageActivity.this, UPDATE_ORDERS_STATUSES);
            }
        };

        ordersSynchroniseCompleteInDB = monitorRealm.where(OrderSynchronisation.class)
                .equalTo("isSyncComplete", true).findAll();
        ordersSynchroniseCompleteInDB.addChangeListener(ordersSynchronisationCompleteListener);

        runBackgroundServiceIntent(OrderPageActivity.this, GENERATE_PART_MAP);
    }

    private Runnable orderUpdateTask = new Runnable() {
        @Override
        public void run() {
            runBackgroundServiceIntent(OrderPageActivity.this, UPDATE_ORDERS);
            runBackgroundServiceIntent(OrderPageActivity.this, UPDATE_ORDERS_STATUSES);
            updateHandler.postDelayed(orderUpdateTask, UPDATE_PERIOD);
            uploadDataWithDelay();
        }
    };

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
                intent = new Intent(this, LoginActivity.class);
                Session.clearSession();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.show_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.show_log:
                intent = new Intent(this, LogActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session.clearCurrentOrder();
        ordersInDB.addChangeListener(orderUpdateListener);
        updateHandler.post(orderUpdateTask);
        updateOrderAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(orderUpdateTask);

        ordersInDB.removeChangeListener(orderUpdateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ordersCompleteInDB.removeChangeListener(ordersCompleteListener);
        ordersToSynchroniseInDB.removeChangeListener(ordersSynchronisationListener);
        ordersSynchroniseCompleteInDB.removeChangeListener(ordersSynchronisationCompleteListener);
        monitorRealm.close();

        AppLog.removeListener(monitorLogRealm, logsWithNotification, logListener);
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        final long currentOrderNumber = orderOverviewList.
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        final Intent intent = new Intent(this, OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        final long currentOrderNumber = orderOverviewList.
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        final Intent intent = new Intent(this, PDFReportActivity.class);
        startActivity(intent);
    }

    private void doOrdersSearch(String query) {
        if ("".equals(query)) {
            ordersAdapter.setOrdersDataSet(orderOverviewList);
        } else {
            List<OrderOverview> searchOrderOverview = new ArrayList<>();
            for (OrderOverview oneOrder : orderOverviewList) {
                if (oneOrder.getNumber().toString().contains(query)) {
                    searchOrderOverview.add(oneOrder);
                }
            }
            ordersAdapter.setOrdersDataSet(searchOrderOverview);
        }
        ordersAdapter.notifyDataSetChanged();
    }

    private void updateOrderAdapter() {
        DbUtils.updateOrderOverviewList(orderOverviewList);
        ordersAdapter.notifyDataSetChanged();
    }

    private void uploadDataWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try (final Realm realm = Realm.getDefaultInstance()) {
                    if (realm.where(Order.class)
                            .equalTo("orderStatus", ORDER_STATUS_COMPLETE)
                            .findAll()
                            .size() > 0)
                        runBackgroundServiceIntent(OrderPageActivity.this, PREPARE_FILES);

                    if (realm.where(OrderSynchronisation.class)
                            .equalTo("isReadyForSync", true)
                            .equalTo("isSyncComplete", false)
                            .equalTo("isError", false)
                            .findAll()
                            .size() > 0)
                        runBackgroundServiceIntent(OrderPageActivity.this, UPLOAD_FILES);
                }
            }
        }, 5000);
    }

}
