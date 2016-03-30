package com.bionic.kvt.serviceapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.UserViewHolder> {
    public  String[][] testOrderList = {
            {"123456789", "29-06-2016", "Generator", "Repair", "Kiev", "In progress"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "In progress"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Not starte"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Generator", "Check", "Very looooong adresss ", "Completed"},
            {"354535789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "In progress"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"516665789", "19-03-2016", "Motor", "Check", "Lviv", "Not starte"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "Not starte"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Generator", "Check", "Lviv", "Completed"},
            {"354363789", "19-03-2016", "Motor", "Check", "Lviv", "Completed"},
            {"358395563", "02-11-2016", "Generator", "Check", "Odessa", "Not started"}
    };

    //    private ordersDataSet;
    OnOrderLineClickListener onOrderLineClickListener;

//    public  OrderAdapter( ordersDataSet){
//        this.ordersDataSet = ordersDataSet;
//    }

    public interface OnOrderLineClickListener {
        void OnOrderLineClicked(View view, int position);
    }

    public void setOnOrderLineClickListener(OnOrderLineClickListener onOrderLineClickListener) {
        this.onOrderLineClickListener = onOrderLineClickListener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View orderView;
        TextView orderNumber;
        TextView orderServiceDate;
        TextView orderDeviceType;
        TextView orderTask;
        TextView orderAddress;
        TextView orderStatus;

        public UserViewHolder(View itemView) {
            super(itemView);
            orderView = itemView;
            orderNumber = (TextView) itemView.findViewById(R.id.order_number);
            orderServiceDate = (TextView) itemView.findViewById(R.id.order_service_date);
            orderDeviceType = (TextView) itemView.findViewById(R.id.order_device_type);
            orderTask = (TextView) itemView.findViewById(R.id.order_task);
            orderAddress = (TextView) itemView.findViewById(R.id.order_address);
            orderStatus = (TextView) itemView.findViewById(R.id.order_status);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        holder.orderNumber.setText(testOrderList[position][0]);
        holder.orderServiceDate.setText(testOrderList[position][1]);
        holder.orderDeviceType.setText(testOrderList[position][2]);
        holder.orderTask.setText(testOrderList[position][3]);
        holder.orderAddress.setText(testOrderList[position][4]);
        holder.orderStatus.setText(testOrderList[position][5]);

        holder.orderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderLineClickListener.OnOrderLineClicked(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return testOrderList.length;
    }
}
