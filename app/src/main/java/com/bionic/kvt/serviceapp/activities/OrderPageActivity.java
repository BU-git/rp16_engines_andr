package com.bionic.kvt.serviceapp.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.Session;


import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class OrderPageActivity extends AppCompatActivity
        implements OrderAdapter.OnOrderLineClickListener, OrderAdapter.OnPDFButtonClickListener {

    private static final int REQUEST_WRITE_CODE = 1;

    private OrderAdapter ordersAdapter;
    private RecyclerView ordersRecyclerView;

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.order_page_search_view);

        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(
                searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setTextSize(14);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        final Session SESSION = (Session) getApplication();
        TextView engenieerId = (TextView) findViewById(R.id.service_engenieer_id);
        engenieerId.setText(SESSION.getEngineerId());

        Button logOut = (Button) findViewById(R.id.service_engenieer_logout_button);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                ((Session) getApplication()).clearSession();
                startActivity(intent);
            }
        });


        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager ordersLayoutManager = new GridLayoutManager(this, Session.ordersDataSetColNumber);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doOrdersSearch(query);
        } else {
            ordersAdapter = new OrderAdapter(Session.ordersDataSet);
            ordersAdapter.setOnOrderLineClickListener(this, this);
            ordersRecyclerView.setAdapter(ordersAdapter);
        }
    }


    @Override
    public void OnOrderLineClicked(View view, int position) {
        final Session SESSION = (Session) getApplication();
        SESSION.setOrderNumber(ordersAdapter.getOrderNumber(position / Session.ordersDataSetColNumber));

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        final Session SESSION = (Session) getApplication();
        SESSION.setOrderNumber(ordersAdapter.getOrderNumber(position / Session.ordersDataSetColNumber));

        if (!isStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_CODE);
            return;
        }

        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
        startActivity(intent);
    }


    private void doOrdersSearch(String query) {
        if ("".equals(query)) {
            ordersAdapter = new OrderAdapter(Session.ordersDataSet);
        } else {
            List<String[]> searchOrdersDataSet = new LinkedList<>();
            for (String[] oneOrder : Session.ordersDataSet) {
                if (oneOrder[0].contains(query)) {
                    searchOrdersDataSet.add(oneOrder);
                }
            }
            ordersAdapter = new OrderAdapter(searchOrdersDataSet);
        }

        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
                startActivity(intent);
            }
        }
    }
}
