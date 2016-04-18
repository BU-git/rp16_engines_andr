package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    @SuppressWarnings("deprecation")
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
            final LinearLayout problemPlaceholderLayout = new LinearLayout(_context);
            problemPlaceholderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            problemPlaceholderLayout.setOrientation(LinearLayout.VERTICAL);
            problemPlaceholderLayout.setId(groupPosition);
            elementLayout.addView(problemPlaceholderLayout);

            Set<Map.Entry<String,JsonElement>> childSet = childElement.entrySet();


            for (Map.Entry<String,JsonElement> child : childSet){
                final CheckBox checkBox = new CheckBox(this._context);

                final TextView textView = new TextView(this._context);
                LinearLayout problemLayout = new LinearLayout(_context);
                problemLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                LinearLayout problemDetailPlaceholderLayout = new LinearLayout(_context);
                problemDetailPlaceholderLayout.setOrientation(LinearLayout.VERTICAL);


                textView.setText(child.getKey()+"\n");
                textView.setGravity(Gravity.FILL);


                textView.setTextAppearance(_context,android.R.style.TextAppearance_Medium);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(_context.getColor(R.color.colorDefaultTextField));
                } else {
                    textView.setTextColor(ColorStateList.valueOf(Color.BLACK));
                }

                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setSingleLine(false);
                //checkBox.setText(child.getKey());
                problemLayout.addView(checkBox);
                problemLayout.addView(textView);

                problemDetailPlaceholderLayout.addView(problemLayout);

                final LinearLayout problemDetailLayout = new LinearLayout(_context);

                TextView text = new TextView(_context);
                //Dummy inherited data

                text.setText("Some Text");

                problemDetailLayout.addView(text);
                problemDetailLayout.setVisibility(View.GONE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (checkBox.isChecked()){
                            problemDetailLayout.setVisibility(View.VISIBLE);
                        } else {
                            problemDetailLayout.setVisibility(View.GONE);
                        }
                    }
                });

                problemDetailPlaceholderLayout.addView(problemDetailLayout);

                problemPlaceholderLayout.addView(problemDetailPlaceholderLayout);


            }

        } else {
            LinearLayout someLayout = (LinearLayout) elementLayout.findViewById(groupPosition);

        }

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
        lblListHeader.setText("\t\t" + headerTitle);
        lblListHeader.setTextAppearance(_context,android.R.style.TextAppearance_Medium);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lblListHeader.setTextColor(_context.getColor(R.color.colorDefaultTextField));
        } else {
            lblListHeader.setTextColor(ColorStateList.valueOf(Color.BLACK));
        }
        lblListHeader.getClipBounds();
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
