package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.api.Order;
import com.bionic.kvt.serviceapp.api.OrderBrief;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static com.bionic.kvt.serviceapp.BuildConfig.IS_LOGGING_ON;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_OVERVIEW_COLUMN_COUNT;

public class OrderPageActivity extends BaseActivity implements
        OrderAdapter.OnOrderLineClickListener,
        OrderAdapter.OnPDFButtonClickListener,
        LoaderManager.LoaderCallbacks<OrderPageActivity.OrderUpdateResult> {

    private static final int ORDERS_LOADER_ID = 3;
    private static final long UPDATE_PERIOD = 60_000; // 60 sec
    private List<OrderOverview> orderOverviewList = new ArrayList<>();
    private OrderAdapter ordersAdapter;
    Handler updateHandler = new Handler();
    AsyncTaskLoader<OrderUpdateResult> updateLoader;

    @Bind(R.id.order_update_status)
    TextView orderUpdateStatusText;

    @Bind(R.id.order_page_search_view)
    SearchView searchView;

    @Bind(R.id.orders_recycler_view)
    RecyclerView ordersRecyclerView;

    static class OrderUpdateResult {
        int status;
        String message;

        public OrderUpdateResult(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
        ButterKnife.bind(this);

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
        String currentEngineer = getText(R.string.service_engineer) + " " + Session.getEngineerName() +
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
        ordersAdapter = new OrderAdapter(getApplicationContext(), orderOverviewList);
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);

        getSupportLoaderManager().initLoader(ORDERS_LOADER_ID, null, OrderPageActivity.this);
    }

    private Runnable orderUpdateTask = new Runnable() {
        @Override
        public void run() {
            updateLoader.forceLoad();
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
    protected void onResume() {
        super.onResume();
        Session.clearCurrentOrder();
//        updateHandler.post(orderUpdateTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(orderUpdateTask);
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        final long currentOrderNumber = orderOverviewList.
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        final long currentOrderNumber = orderOverviewList.
                get(position / ORDER_OVERVIEW_COLUMN_COUNT).getNumber();
        Session.setCurrentOrder(currentOrderNumber);

        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
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

    public void updateOrderAdapter() {
        DbUtils.updateOrderOverviewList(orderOverviewList);
        if (IS_LOGGING_ON) Session.addToSessionLog("Setting OrderAdapter data.");
        ordersAdapter.setOrdersDataSet(orderOverviewList);
        ordersAdapter.notifyDataSetChanged();
    }

    public static class UpdateDataFromServer extends AsyncTaskLoader<OrderUpdateResult> {
        private Context context;

        public UpdateDataFromServer(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void forceLoad() {
            super.forceLoad();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
        }

        @Override
        public void deliverResult(OrderUpdateResult data) {
            super.deliverResult(data);
        }

        @Override
        public OrderUpdateResult loadInBackground() {
            if (!Utils.isNetworkConnected(context))
                return new OrderUpdateResult(1, "No connection to network. Canceling update.");

            final Call<List<OrderBrief>> orderBriefListRequest =
                    Session.getServiceConnection().getOrdersBrief(Session.getEngineerEmail());

            if (IS_LOGGING_ON)
                Session.addToSessionLog("Updating orders. Getting orders brief list from: " + orderBriefListRequest.request());

            final Response<List<OrderBrief>> orderBriefListResponse;
            try {
                orderBriefListResponse = orderBriefListRequest.execute();
            } catch (IOException e) {
                return new OrderUpdateResult(1, "Orders brief list request fail: " + e.toString());
            }

            if (!orderBriefListResponse.isSuccessful())
                return new OrderUpdateResult(1, "Orders brief list request error: " + orderBriefListResponse.code());

            if (IS_LOGGING_ON)
                Session.addToSessionLog("Request successful. Get " + orderBriefListResponse.body().size() + " brief orders.");

            final List<OrderBrief> ordersToBeUpdated = DbUtils.getOrdersToBeUpdated(orderBriefListResponse.body());

            if (ordersToBeUpdated.isEmpty())
                return new OrderUpdateResult(0, "Nothing to update.");

            for (OrderBrief orderBrief : ordersToBeUpdated) {
                final Call<Order> orderRequest =
                        Session.getServiceConnection().getOrder(orderBrief.getNumber(), Session.getEngineerEmail());

                if (IS_LOGGING_ON)
                    Session.addToSessionLog("Getting order from: " + orderRequest.request());

                final Response<Order> orderResponse;
                try {
                    orderResponse = orderRequest.execute();
                } catch (IOException e) {
                    return new OrderUpdateResult(1, "Order request fail: " + e.toString());
                }
                if (!orderResponse.isSuccessful()) {
                    return new OrderUpdateResult(1, "Order request error: " + orderResponse.code());
                }

                if (IS_LOGGING_ON) Session.addToSessionLog("Request successful!");

                DbUtils.updateOrderFromServer(orderResponse.body());
            }

            return new OrderUpdateResult(0, "Update " + ordersToBeUpdated.size() + " orders.");
        }
    }

    @Override
    public Loader<OrderUpdateResult> onCreateLoader(int id, Bundle args) {
        if (id == ORDERS_LOADER_ID) {
            updateLoader = new UpdateDataFromServer(this);
            return updateLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<OrderUpdateResult> loader, OrderUpdateResult data) {
        if (loader.getId() == ORDERS_LOADER_ID) {
            if (IS_LOGGING_ON) Session.addToSessionLog(data.message);
            orderUpdateStatusText.setText("[" + Utils.getTimeStringFromDate(Calendar.getInstance().getTime()) + "] " + data.message);
            updateOrderAdapter();
            updateHandler.postDelayed(orderUpdateTask, UPDATE_PERIOD);
        }
    }

    @Override
    public void onLoaderReset(Loader<OrderUpdateResult> loader) {
        // NOOP
    }
}
