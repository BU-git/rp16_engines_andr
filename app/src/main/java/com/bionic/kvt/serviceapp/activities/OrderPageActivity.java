package com.bionic.kvt.serviceapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;

public class OrderPageActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private RecyclerView.Adapter ordersAdapter;
    private RecyclerView.LayoutManager ordersLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        ordersRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        ordersLayoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        // specify an adapter (see also next example)
        ordersAdapter = new OrderAdapter();
        ordersRecyclerView.setAdapter(ordersAdapter);
    }
}
