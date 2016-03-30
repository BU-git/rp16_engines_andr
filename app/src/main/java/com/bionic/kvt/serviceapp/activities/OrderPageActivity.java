package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;

public class OrderPageActivity extends AppCompatActivity implements OrderAdapter.OnOrderLineClickListener {
    private RecyclerView ordersRecyclerView;
    private OrderAdapter ordersAdapter;
    private RecyclerView.LayoutManager ordersLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);

        ordersLayoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        ordersAdapter = new OrderAdapter();
        ordersRecyclerView.setAdapter(ordersAdapter);
        ordersAdapter.setOnOrderLineClickListener(this);
    }

    @Override
    public void OnOrderLineClicked(View view, int position) {
        Intent intent = new Intent(getApplicationContext(), ProcessOrderPage1Activity.class);
        intent.putExtra("order_number", ordersAdapter.testOrderList[position][0]);
        startActivity(intent);
    }
}
