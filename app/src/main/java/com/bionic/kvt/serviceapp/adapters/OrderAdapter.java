package com.bionic.kvt.serviceapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private String[][] testOrderList = {
            {"123456789", "29-06-2016", "Generator", "Repair", "Kiev", "In progress"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"358395563", "02-11-2016", "Motor", "Check", "Odessa", "Not started"}
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View orderView;

        public ViewHolder(View v) {
            super(v);
            orderView = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.orderView.findViewById(R.id.order_number)).setText(testOrderList[position][0]);
        ((TextView) holder.orderView.findViewById(R.id.order_service_date)).setText(testOrderList[position][1]);
        ((TextView) holder.orderView.findViewById(R.id.order_device_type)).setText(testOrderList[position][2]);
        ((TextView) holder.orderView.findViewById(R.id.order_task)).setText(testOrderList[position][3]);
        ((TextView) holder.orderView.findViewById(R.id.order_address)).setText(testOrderList[position][4]);
        ((TextView) holder.orderView.findViewById(R.id.order_status)).setText(testOrderList[position][5]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return testOrderList.length;
    }
}
