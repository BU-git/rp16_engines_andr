package com.bionic.kvt.serviceapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.OrderAdapter;
import com.bionic.kvt.serviceapp.Session;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class OrderPageActivity extends AppCompatActivity
        implements OrderAdapter.OnOrderLineClickListener, OrderAdapter.OnPDFButtonClickListener {

    private static final int REQUEST_WRITE_CODE = 1;


    private boolean hasWritePermission = false;
    private String orderNumber;

    private RecyclerView ordersRecyclerView;
    private OrderAdapter ordersAdapter;
    private RecyclerView.LayoutManager ordersLayoutManager;

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
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

        ordersLayoutManager = new GridLayoutManager(this, OrderAdapter.COLUMN_NUMBER);

        ordersRecyclerView = (RecyclerView) findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setHasFixedSize(true);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        ordersAdapter = new OrderAdapter();
        ordersRecyclerView.setAdapter(ordersAdapter);
        ordersAdapter.setOnOrderLineClickListener(this, this);

    }


    @Override
    public void OnOrderLineClicked(View view, int position) {
        Intent intent = new Intent(getApplicationContext(), OrderPageDetailActivity.class);
        final Session SESSION = (Session) getApplication();
        SESSION.setOrderNumber(ordersAdapter.testOrderList[position / OrderAdapter.COLUMN_NUMBER][0]);
        startActivity(intent);
    }

    @Override
    public void OnPDFButtonClicked(View view, int position) {
        final Session SESSION = (Session) getApplication();
        SESSION.clearOrderNumber();
        orderNumber = ordersAdapter.testOrderList[position / OrderAdapter.COLUMN_NUMBER][0];

        hasWritePermission = isStoragePermissionGranted();
        if (!hasWritePermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_CODE);
            return;
        }

        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
        SESSION.setOrderNumber(orderNumber);
        startActivity(intent);
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
                intent.putExtra("order_number", orderNumber);
                startActivity(intent);
            }
        }
    }
}
