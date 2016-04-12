package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

public class OrderPageActivity extends AppCompatActivity
        implements OrderAdapter.OnOrderLineClickListener, OrderAdapter.OnPDFButtonClickListener {

    private OrderAdapter ordersAdapter;
    private RecyclerView ordersRecyclerView;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        realm = Realm.getDefaultInstance();

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
                Session.getSession().clearSession();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Configuring connection button
        Button connectionStatus = (Button) findViewById(R.id.service_engineer_connection_button);
        connectionStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConnectionActivity.class);
                startActivity(intent);
            }
        });


        // Configuring engineer Id
        TextView engineerId = (TextView) findViewById(R.id.service_engineer_id);
        engineerId.setText(Session.getSession().getEngineerName() + " (" + Session.getSession().getEngineerEmail() + ")");

        // Configuring Recycler View
        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager ordersLayoutManager = new GridLayoutManager(this, Session.ordersDataSetColNumber);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        // Showing all orders
        ordersAdapter = new OrderAdapter(Session.ordersDataSet);
        ordersAdapter.setOnOrderLineClickListener(this, this);
        ordersRecyclerView.setAdapter(ordersAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        // Setting selected Order number to current session
        Session.getSession().setOrderNumber(ordersAdapter.getOrderNumber(position / Session.ordersDataSetColNumber));
        Session.getSession().setOrderStatus(ordersAdapter.OrderStatus(position / Session.ordersDataSetColNumber));

        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        // Setting selected Order number to current session
        Session.getSession().setOrderNumber(ordersAdapter.getOrderNumber(position / Session.ordersDataSetColNumber));

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
            ordersAdapter.setOrdersDataSet(Session.ordersDataSet);
        } else {
            List<String[]> searchOrdersDataSet = new LinkedList<>();
            for (String[] oneOrder : Session.ordersDataSet) {
                if (oneOrder[0].contains(query)) {
                    searchOrdersDataSet.add(oneOrder);
                }
            }
            ordersAdapter.setOrdersDataSet(searchOrdersDataSet);
        }

        ordersRecyclerView.swapAdapter(ordersAdapter, false);
    }
}
