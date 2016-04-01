package com.bionic.kvt.serviceapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.UserViewHolder> {
    public static final int COLUMN_NUMBER = 7;
    public String[][] testOrderList = {
            {"123456789", "29-06-2016", "Generator", "Repair", "Kiev", "In progress", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "In progress", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Not starte", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Generator", "Check", "Very looooong adresss ", "Completed", "PDF"},
            {"354535789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "In progress", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"516665789", "19-03-2016", "Motor", "Check", "Lviv", "Not starte", "PDF"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "Not starte", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "Completed", "PDF"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed", "PDF"},
            {"358395563", "02-11-2016", "Generator", "Check", "Odessa", "Not started", "PDF"}
    };

    //    private ordersDataSet;
    OnOrderLineClickListener onOrderLineClickListener;
    OnPDFButtonClickListener onPDFButtonClickListener;

//    public  OrderAdapter( ordersDataSet){
//        this.ordersDataSet = ordersDataSet;
//    }

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
        int row = position / COLUMN_NUMBER;
        int cell = position - row * COLUMN_NUMBER;

        TextView textCell = (TextView) holder.oneCellView.findViewById(R.id.one_cell_text);
        Button buttonCell = (Button) holder.oneCellView.findViewById(R.id.order_make_pdf_button);

        if (cell == COLUMN_NUMBER - 1) {
            textCell.setVisibility(View.GONE);
            buttonCell.setVisibility(View.VISIBLE);
        } else {
            textCell.setVisibility(View.VISIBLE);
            buttonCell.setVisibility(View.GONE);
            textCell.setText(testOrderList[row][cell]);
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
        return testOrderList.length * COLUMN_NUMBER;
    }
}
