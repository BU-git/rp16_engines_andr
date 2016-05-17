package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.models.OrderOverview;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.File;
import java.util.List;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_OVERVIEW_COLUMN_COUNT;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE_UPLOADED;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_IN_PROGRESS;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_NOT_STARTED;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.UserViewHolder> {
    private final Context context;
    private List<OrderOverview> orderOverviewList;

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
                textCell.setText(String.valueOf(orderOverviewList.get(row).getNumber()));
                break;

            case 1: // Column Date
                textCell.setVisibility(View.VISIBLE);
                buttonCell.setVisibility(View.GONE);
                textCell.setText(Utils.getDateStringFromDate(orderOverviewList.get(row).getDate()));
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

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_COMPLETE_UPLOADED) {
                    textCell.setText(context.getText(R.string.uploaded));
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorOK));
                }

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_COMPLETE) {
                    textCell.setText(context.getText(R.string.complete));
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorOK));
                }

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_IN_PROGRESS) {
                    textCell.setText(context.getText(R.string.in_progress));
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorWarring));
                }

                if (orderOverviewList.get(row).getOrderStatus() == ORDER_STATUS_NOT_STARTED) {
                    textCell.setText(context.getText(R.string.not_started));
                    textCell.setTextColor(ContextCompat.getColor(context, R.color.colorError));
                }
                break;

            case 6: // Column PDF Button
                textCell.setVisibility(View.GONE);
                buttonCell.setVisibility(View.VISIBLE);
                File pdfReportFile = Utils.getPDFReportFileName(orderOverviewList.get(row).getNumber(), false);
                if (orderOverviewList.get(row).getOrderStatus() >= ORDER_STATUS_COMPLETE
                        && pdfReportFile.exists()) {
                    buttonCell.setEnabled(true);
                }
                else {
                    buttonCell.setEnabled(false);
                }

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
