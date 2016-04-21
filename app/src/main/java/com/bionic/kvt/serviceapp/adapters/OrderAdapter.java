package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.models.OrderOverview;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.bionic.kvt.serviceapp.GlobalConstants.*;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.UserViewHolder> {
    private final Context context;
    private List<OrderOverview> orderOverviewList;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private OnOrderLineClickListener onOrderLineClickListener;
    private OnPDFButtonClickListener onPDFButtonClickListener;


    public OrderAdapter(Context context, List<OrderOverview> orderOverviewList) {
        this.context = context;
        this.orderOverviewList = orderOverviewList;
    }

    public void setOrdersDataSet(List<OrderOverview> orderOverviewList) {
        this.orderOverviewList = orderOverviewList;
    }

    public interface OnOrderLineClickListener {
        void OnOrderLineClicked(View view, int position);
    }

    public interface OnPDFButtonClickListener {
        void OnPDFButtonClicked(View view, int position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final View oneCellView;

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
        int row = position / ORDER_OVERVIEW_COLUMN_COUNT;
        int cell = position % ORDER_OVERVIEW_COLUMN_COUNT;

        TextView textCell = (TextView) holder.oneCellView.findViewById(R.id.one_cell_text);
        Button buttonCell = (Button) holder.oneCellView.findViewById(R.id.order_make_pdf_button);

        // Setting default cell text color
        textCell.setTextColor(ContextCompat.getColor(context, R.color.colorMainText));

        switch (cell) {
            case 0: // Column Order number
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(orderOverviewList.get(row).getNumber().toString());
                break;

            case 1: // Column Date
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(simpleDateFormat.format(orderOverviewList.get(row).getDate()));
                break;

            case 2: // Column Installation
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(orderOverviewList.get(row).getInstallationName());
                break;

            case 3: // Column Task
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(orderOverviewList.get(row).getTaskLtxa1());
                break;

            case 4: // Column Address
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(orderOverviewList.get(row).getInstallationAddress());
                break;

            case 5: // Column Status
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_COMPLETE) {
                    textCell.setText("Complete");
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorOK));
                }

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_IN_PROGRESS) {
                    textCell.setText("In progress");
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorWarring));
                }

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_NOT_STARTED) {
                    textCell.setText("Not started");
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorError));
                }
                break;

            case 6: // Column PDF Button
                textCell.setVisibility(View.GONE);
                buttonCell.setVisibility(View.VISIBLE);
                if (orderOverviewList.get(row).getOrderStatus() != ORDER_STATUS_COMPLETE)
                    buttonCell.setEnabled(false);
                break;
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
        return orderOverviewList.size() * ORDER_OVERVIEW_COLUMN_COUNT;
    }
}
