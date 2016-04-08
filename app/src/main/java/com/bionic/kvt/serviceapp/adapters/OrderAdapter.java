package com.bionic.kvt.serviceapp.adapters;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.UserViewHolder> {
    private List<String[]> ordersDataSet;
    private int ordersDataSetColNumber;

    OnOrderLineClickListener onOrderLineClickListener;
    OnPDFButtonClickListener onPDFButtonClickListener;

    public OrderAdapter(List<String[]> ordersDataSet) {
        this.ordersDataSet = ordersDataSet;
        if (ordersDataSet.size() > 0)
            ordersDataSetColNumber = ordersDataSet.get(0).length;
    }

    public void setOrdersDataSet(List<String[]> ordersDataSet) {
        this.ordersDataSet = ordersDataSet;
        if (ordersDataSet.size() > 0)
            ordersDataSetColNumber = ordersDataSet.get(0).length;
        else ordersDataSetColNumber = 0;
    }

    public Long getOrderNumber(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= ordersDataSet.size()) return (long) 0;
        return Long.valueOf(ordersDataSet.get(rowNumber)[0]);
    }

    @Nullable
    public String OrderStatus(int rowNumber) {
        if (rowNumber < 0 || rowNumber >= ordersDataSet.size()) return null;
        return ordersDataSet.get(rowNumber)[Session.ordersDataSetColNumber - 2];
    }

    public interface OnOrderLineClickListener {
        void OnOrderLineClicked(View view, int position);
    }

    public interface OnPDFButtonClickListener {
        void OnPDFButtonClicked(View view, int position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View oneCellView;

        public UserViewHolder(View itemView) {
            super(itemView);
            oneCellView = itemView.findViewById(R.id.one_cell);
        }
    }

    public void setOnOrderLineClickListener(OnOrderLineClickListener onOrderLineClickListener,
                                            OnPDFButtonClickListener onPDFButtonClickListener) {
        this.onOrderLineClickListener = onOrderLineClickListener;
        this.onPDFButtonClickListener = onPDFButtonClickListener;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_page_one_cell, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        int row = position / ordersDataSetColNumber;
        int cell = position - row * ordersDataSetColNumber;

        TextView textCell = (TextView) holder.oneCellView.findViewById(R.id.one_cell_text);
        Button buttonCell = (Button) holder.oneCellView.findViewById(R.id.order_make_pdf_button);

        String orderStatus;

        // PDF Button
        if (cell == ordersDataSetColNumber - 1) {
            textCell.setVisibility(View.GONE);
            buttonCell.setVisibility(View.VISIBLE);
            orderStatus = Session.ordersDataSet.get(row)[ordersDataSetColNumber - 2];
            if (!("Completed".equals(orderStatus))) {
                buttonCell.setEnabled(false);
            }
        } else {
            textCell.setVisibility(View.VISIBLE);
            buttonCell.setVisibility(View.GONE);
            textCell.setText(ordersDataSet.get(row)[cell]);
        }

        //Order Status
//        ContextCompat.getColor(context, R.color.colorOK);
        if (cell == ordersDataSetColNumber - 2) {
            orderStatus = Session.ordersDataSet.get(row)[ordersDataSetColNumber - 2];
            if ("Completed".equals(orderStatus)) {
                textCell.setTextColor(Color.parseColor("#388E3C"));
            } else if ("In progress".equals(orderStatus)) {
                textCell.setTextColor(Color.parseColor("#FFA000"));
            } else {
                textCell.setTextColor(Color.parseColor("#CC0234"));
            }
        } else {
            textCell.setTextColor(Color.parseColor("#424242"));
        }

        textCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderLineClickListener.OnOrderLineClicked(v, position);
            }
        });

        buttonCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPDFButtonClickListener.OnPDFButtonClicked(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersDataSet.size() * ordersDataSetColNumber;
    }
}
