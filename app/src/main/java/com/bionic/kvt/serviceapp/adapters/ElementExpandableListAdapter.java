package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.activities.ComponentDetailFragment;
import com.bionic.kvt.serviceapp.activities.ComponentListActivity;
import com.bionic.kvt.serviceapp.helpers.CalculationHelper;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.bionic.kvt.serviceapp.utils.Utils;
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
    public final static Integer layoutMagicNumber = 1000;
    public final static Integer viewMagicNumber = 10;
    public static Integer groupClickedPosition = 0;
    public static Integer childClickedPosition;
    public boolean isInitialSelect = true;
    String TAG = ElementExpandableListAdapter.class.getName();


    //Saving state
    private Context context;
    private HashMap<String, JsonObject> childMap;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<String, JsonObject> _listDataChild;

    public ElementExpandableListAdapter(Context context, Map<String, JsonObject> listChildData) {
        this._context = context;
        this._listDataHeader =  Arrays.asList(listChildData.keySet().toArray(new String[listChildData.keySet().size()]));
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
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final JsonObject childElement = (JsonObject) getChild(groupPosition, childPosition);
        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.component_element, null);
        }

        LinearLayout elementLayout = (LinearLayout) convertView.findViewById(R.id.component_element_layout);
        if (elementLayout.findViewById(groupPosition) == null){

            final LinearLayout problemPlaceholderLayout = new LinearLayout(_context);
            problemPlaceholderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            problemPlaceholderLayout.setOrientation(LinearLayout.VERTICAL);
            problemPlaceholderLayout.setId(groupPosition);
            elementLayout.addView(problemPlaceholderLayout);
            //Creating layout id as a concatenation of magic number, group clicked position and group position
            Integer layoutId = Integer.valueOf(new StringBuilder()
                    .append(Integer.valueOf(layoutMagicNumber))
                    .append(Integer.valueOf(groupClickedPosition))
                    .append(groupPosition)
                    .toString());
            problemPlaceholderLayout.setId(layoutId);

            Set<Map.Entry<String,JsonElement>> childSet = childElement.entrySet();

            for (final Map.Entry<String,JsonElement> child : childSet){
                Integer position = Utils.getSetIndex(childSet, child);
                final CheckBox checkBox = new CheckBox(this._context);
                //Creating checkbox id as a concatenation of magic number, group position and checkbox position
                final Integer id = Integer.valueOf(new StringBuilder()
                        .append(String.valueOf(viewMagicNumber))
                        .append(String.valueOf(groupPosition))
                        .append(String.valueOf(position))
                        .toString());
                checkBox.setId(id);

                childClickedPosition = position;

                checkBox.setText(child.getKey()+"\n");

                problemPlaceholderLayout.addView(checkBox);
                problemPlaceholderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final LinearLayout problemDetailLayout = new LinearLayout(_context);
                //Todo: Replace with actual default fields
                TextView text = new TextView(_context);
                final Spinner omvangSpinner = (Spinner) infalInflater.inflate(R.layout.template_omvang,null);
                final Spinner actiesSpinner = (Spinner)  infalInflater.inflate(R.layout.template_acties,null);
                final Spinner intensitySpinner = (Spinner) infalInflater.inflate(R.layout.template_intensiteit, null);

                problemDetailLayout.addView(omvangSpinner);
                problemDetailLayout.addView(intensitySpinner);
                problemDetailLayout.addView(actiesSpinner);

                final Switch oplegostSwitch = new Switch(_context);
                oplegostSwitch.setText(R.string.oplegost);
                problemDetailLayout.addView(oplegostSwitch);
                problemDetailLayout.setId(groupPosition);

                problemDetailLayout.addView(text);
                //Hiding default fields if checkbox is not selected;
                problemDetailLayout.setVisibility(View.GONE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Tracking state of the checkboxes
                        final DefectState state = new DefectState(ComponentDetailFragment.ARG_CURRENT,groupClickedPosition,id);
                        state.setElement((String) getGroup(groupPosition));
                        state.setProblem(child.getKey());

                        //Setting default fields

                        omvangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                state.setExtentId(omvangSpinner.getSelectedItemPosition());
                                state.setExtent((String) omvangSpinner.getSelectedItem());

                                state.setCondition(CalculationHelper.INSTANCE.getCondition(
                                        state.getExtentId(),
                                        state.getIntensityId(),
                                        child.getValue().getAsJsonArray().get(0).getAsString()
                                ));

                                state.setInitialScore(child.getValue().getAsJsonArray().get(1).getAsInt());
                                if (state.getCondition() != null){
                                    state.setCorrelation(CalculationHelper.INSTANCE.getConditionFactor(state.getCondition()));
                                    state.setCorrelatedScore(state.getCorrelation() * state.getInitialScore());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        intensitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                state.setIntensityId(intensitySpinner.getSelectedItemPosition());
                                state.setIntensity((String) intensitySpinner.getSelectedItem());

                                state.setCondition(CalculationHelper.INSTANCE.getCondition(
                                        state.getExtentId(),
                                        state.getIntensityId(),
                                        child.getValue().getAsJsonArray().get(0).getAsString()
                                ));

                                state.setInitialScore(child.getValue().getAsJsonArray().get(1).getAsInt());
                                if (state.getCondition() != null){
                                    state.setCorrelation(CalculationHelper.INSTANCE.getConditionFactor(state.getCondition()));
                                    state.setCorrelatedScore(state.getCorrelation() * state.getInitialScore());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        actiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                state.setActionId(actiesSpinner.getSelectedItemPosition());
                                state.setAction((String) actiesSpinner.getSelectedItem());


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        oplegostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                state.setFixed(oplegostSwitch.isChecked());
                            }
                        });



                        Log.d(TAG, "Object is: " + state.toString());


                        if (checkBox.isChecked()){
                            problemDetailLayout.setVisibility(View.VISIBLE);
                            if (!ComponentListActivity.defectStateList.contains(state)){
                                ComponentListActivity.defectStateList.add(state);
                            }
                        } else {
                            problemDetailLayout.setVisibility(View.GONE);
                            ComponentListActivity.defectStateList.remove(state);
                        }
                    }
                });

                problemPlaceholderLayout.addView(problemDetailLayout);

                for (DefectState d : ComponentListActivity.defectStateList) {
                    if (d.getPart().equals(ComponentDetailFragment.ARG_CURRENT) && d.getGroupPosition() == groupClickedPosition){
                                    if (checkBox.getId() == d.getCheckboxPosition()){
                                        omvangSpinner.setSelection(d.getExtentId());
                                        intensitySpinner.setSelection(d.getIntensityId());
                                        actiesSpinner.setSelection(d.getActionId());

                                        oplegostSwitch.setChecked(d.isFixed());
                                        checkBox.setChecked(true);

                                        d.setCondition(CalculationHelper.INSTANCE.getCondition(
                                                d.getExtentId(),
                                                d.getIntensityId(),
                                                child.getValue().getAsJsonArray().get(0).getAsString()
                                        ));

                                        d.setInitialScore(child.getValue().getAsJsonArray().get(1).getAsInt());
                                        if (d.getCondition() != null){
                                            d.setCorrelation(CalculationHelper.INSTANCE.getConditionFactor(d.getCondition()));
                                            d.setCorrelatedScore(d.getCorrelation() * d.getInitialScore());
                                        }

                                        Log.d(TAG,"Object in Iteration: " + d.toString());
                                    }
                    }
                }
            }

        } else {
            LinearLayout someLayout = (LinearLayout) elementLayout.findViewById(groupClickedPosition);

        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
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
            lblListHeader.setTextColor(_context.getColor(R.color.colorTextField));
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
