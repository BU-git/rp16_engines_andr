package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
Adapter for the Expandable Parts List
 */
public class ElementExpandableListAdapter extends BaseExpandableListAdapter {
    String TAG = ElementExpandableListAdapter.class.getName();
    private Context context;
    private HashMap<String, JsonObject> childMap;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<String, JsonObject> _listDataChild;

    public ElementExpandableListAdapter(Context context, Map<String, JsonObject> listChildData) {
        this._context = context;
        this._listDataHeader =  Arrays.asList(listChildData.keySet().toArray(new String[listChildData.keySet().size()]));
        Log.d(TAG, "Child Size is: " + _listDataHeader.size());
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).getAsJsonObject();
        //return "Hello World";
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final JsonObject childElement = (JsonObject) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.component_element, null);

        }
        LinearLayout elementLayout = (LinearLayout) convertView.findViewById(R.id.component_element_layout);

        if (elementLayout.findViewById(groupPosition) == null){
            LinearLayout someLayout = new LinearLayout(_context);
            someLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            someLayout.setOrientation(LinearLayout.VERTICAL);
            someLayout.setId(groupPosition);
            elementLayout.addView(someLayout);

            Set<Map.Entry<String,JsonElement>> childSet = childElement.entrySet();


            for (Map.Entry<String,JsonElement> child : childSet){
                CheckBox checkBox = new CheckBox(this._context);
                checkBox.setText(child.getKey());
                someLayout.addView(checkBox);
            }

        } else {
            LinearLayout someLayout = (LinearLayout) elementLayout.findViewById(groupPosition);

        }


        //parent.addView(elementLayout);
        /*
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.component_element);

        txtListChild.setText(childText);
        */
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //@// TODO: 4/15/2016 Replace with Object Array Size
        return 1;
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).getAsJsonArray().size();
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).getAsInt();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.component_detail_text, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.component_detail_title);
        lblListHeader.setText(headerTitle);
        lblListHeader.setFocusable(false);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
