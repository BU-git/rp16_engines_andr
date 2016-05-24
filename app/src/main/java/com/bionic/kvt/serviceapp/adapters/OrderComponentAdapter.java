package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.db.Components.Component;

import java.util.List;

public class OrderComponentAdapter extends ArrayAdapter<Component> {
    private Context context;
    private List<Component> componentList;

    public OrderComponentAdapter(Context context, List<Component> componentList) {
        super(context, R.layout.template_components, componentList);
        this.context = context;
        this.componentList = componentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.template_components, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.order_component_name)).setText(componentList.get(position).getEqart());
        ((TextView) convertView.findViewById(R.id.order_component_number)).setText(String.valueOf(componentList.get(position).getEqunr()));
        ((TextView) convertView.findViewById(R.id.order_component_mark)).setText(componentList.get(position).getHerst());
        ((TextView) convertView.findViewById(R.id.order_component_sort)).setText(componentList.get(position).getTypbz());
        ((TextView) convertView.findViewById(R.id.order_component_serial)).setText(componentList.get(position).getSernr());

        return convertView;
    }

}
