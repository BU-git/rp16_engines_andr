package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;

import java.util.HashMap;
import java.util.List;

public class MeasurementsExpListAdapter extends BaseExpandableListAdapter {
    public static class MeasurementsItem {
        private String itemName;
        private String itemValue;
        private String itemUnit;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public String getItemUnit() {
            return itemUnit;
        }

        public void setItemUnit(String itemUnit) {
            this.itemUnit = itemUnit;
        }

        public MeasurementsItem() {
        }

        public MeasurementsItem(String itemName, String itemUnit) {
            this.itemName = itemName;
            this.itemUnit = itemUnit;
        }
    }

    private Context context;
    private List<String> dataHeaderList;
    private HashMap<String, List<MeasurementsItem>> dataChildList;

    public MeasurementsExpListAdapter(Context context, List<String> dataHeaderList, HashMap<String, List<MeasurementsItem>> dataChildList) {
        this.context = context;
        this.dataHeaderList = dataHeaderList;
        this.dataChildList = dataChildList;
    }

    @Override
    public int getGroupCount() {
        return dataHeaderList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dataChildList.get(dataHeaderList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dataHeaderList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataChildList.get(dataHeaderList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.measurements_list_group, null);
        }
        final TextView groupHeader = (TextView) convertView.findViewById(R.id.measurements_list_group_header);
        groupHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final MeasurementsItem itemTextArray = (MeasurementsItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.measurements_list_item, null);
        }
        final TextView itemName = (TextView) convertView.findViewById(R.id.measurements_list_item_name);
        itemName.setText(itemTextArray.getItemName());
        final TextView itemData = (TextView) convertView.findViewById(R.id.measurements_list_item_data);
        itemData.setText(itemTextArray.getItemValue());
        final TextView itemUnit = (TextView) convertView.findViewById(R.id.measurements_list_item_unit);
        itemUnit.setText(itemTextArray.getItemUnit());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
