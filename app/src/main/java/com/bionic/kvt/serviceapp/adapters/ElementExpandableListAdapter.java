package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.activities.ComponentDetailFragment;
import com.bionic.kvt.serviceapp.helpers.CalculationHelper;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter for the Expandable Parts List
 */
public class ElementExpandableListAdapter extends BaseExpandableListAdapter {
    public final static Integer layoutMagicNumber = 1000;
    public final static Integer viewMagicNumber = 10;
    public static Integer groupClickedPosition = 0;
    public static Integer childClickedPosition;
    public static Integer score = 1;
    String TAG = ElementExpandableListAdapter.class.getName();

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private Map<String, JsonObject> _listDataChild;

    public ElementExpandableListAdapter(Context context, Map<String, JsonObject> listChildData) {
        this._context = context;

        /*
        Creating mutable map as a copy of a current child map.
        Initial map is used for XML generation.
        Current mutated map is used to display the data in Child with an additional "Score" field.
         */
        LinkedHashMap<String, JsonObject> mutableListChild = (LinkedHashMap<String, JsonObject>) ((LinkedHashMap<String, JsonObject>) listChildData).clone();
        mutableListChild.put(context.getResources().getString(R.string.score_text), null);
        String[] arrayChild = mutableListChild.keySet().toArray(new String[mutableListChild.keySet().size()]);
        List<String> listSortedChild = new LinkedList<>();
        for (String child : arrayChild) {
            if (child.equals(context.getResources().getString(R.string.score_text))) {
                listSortedChild.add(0, child);
            } else {
                listSortedChild.add(child);
            }
        }
        this._listDataHeader = listSortedChild;
        this._listDataChild = mutableListChild;
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
        if (elementLayout.findViewById(groupPosition) == null) {

            final LinearLayout problemPlaceholderLayout = new LinearLayout(_context);
            problemPlaceholderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            problemPlaceholderLayout.setOrientation(LinearLayout.VERTICAL);
            problemPlaceholderLayout.setId(groupPosition);
            elementLayout.addView(problemPlaceholderLayout);
            //Creating layout id as a concatenation of magic number, group clicked position and group position
            Integer layoutId = Integer.valueOf(String.valueOf(layoutMagicNumber) + groupClickedPosition + groupPosition);
            problemPlaceholderLayout.setId(layoutId);

            Set<Map.Entry<String, JsonElement>> childSet = childElement.entrySet();

            for (final Map.Entry<String, JsonElement> child : childSet) {
                Integer position = Utils.getSetIndex(childSet, child);
                final CheckBox checkBox = new CheckBox(this._context);
                //Creating checkbox id as a concatenation of magic number, group position and checkbox position
                final Integer id = Integer.valueOf(String.valueOf(viewMagicNumber) + String.valueOf(groupPosition) + String.valueOf(position));
                checkBox.setId(id);

                childClickedPosition = position;

                checkBox.setText(child.getKey() + "\n");

                problemPlaceholderLayout.addView(checkBox);
                problemPlaceholderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final LinearLayout problemDetailLayout = new LinearLayout(_context);
                final TextView text = new TextView(_context);
                final Spinner omvangSpinner = (Spinner) infalInflater.inflate(R.layout.template_omvang, null);
                final Spinner actiesSpinner = (Spinner) infalInflater.inflate(R.layout.template_acties, null);
                final Spinner intensitySpinner = (Spinner) infalInflater.inflate(R.layout.template_intensiteit, null);

                problemDetailLayout.addView(omvangSpinner);
                problemDetailLayout.addView(intensitySpinner);
                problemDetailLayout.addView(actiesSpinner);

                final Switch oplegostSwitch = new Switch(_context);
                oplegostSwitch.setText(R.string.solved);
                problemDetailLayout.addView(oplegostSwitch);
                problemDetailLayout.setId(groupPosition);

                problemDetailLayout.addView(text);
                //Hiding default fields if checkbox is not selected;
                problemDetailLayout.setVisibility(View.GONE);


                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Tracking state of the checkboxes
                        final DefectState state = new DefectState(ComponentDetailFragment.ARG_CURRENT, groupClickedPosition, id);
                        if (Session.getDefectStateList().contains(state)) {
                            omvangSpinner.setEnabled(false);
                            actiesSpinner.setEnabled(false);
                            intensitySpinner.setEnabled(false);
                            oplegostSwitch.setEnabled(false);
                        } else {
                            omvangSpinner.setEnabled(true);
                            omvangSpinner.setSelection(0);
                            actiesSpinner.setEnabled(true);
                            actiesSpinner.setSelection(0);
                            intensitySpinner.setEnabled(true);
                            intensitySpinner.setSelection(0);
                            oplegostSwitch.setEnabled(true);
                            oplegostSwitch.setSelected(false);

                        }
                        state.performScoreAdjustments(child);
                        notifyDataSetChanged();

                        state.setElement((String) getGroup(groupPosition));
                        state.setProblem(child.getKey());
                        //Setting default fields

                        omvangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                state.setExtentId(omvangSpinner.getSelectedItemPosition());
                                state.setExtent((String) omvangSpinner.getSelectedItem());
                                state.performScoreAdjustments(child);
                                notifyDataSetChanged();

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
                                state.performScoreAdjustments(child);
                                notifyDataSetChanged();

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
                                state.performScoreAdjustments(child);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        oplegostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                state.setFixed(oplegostSwitch.isChecked());
                                state.performScoreAdjustments(child);
                                notifyDataSetChanged();
                            }
                        });

                        if (checkBox.isChecked()) {
                            problemDetailLayout.setVisibility(View.VISIBLE);
                            if (!Session.getDefectStateList().contains(state)) {
                                Session.getDefectStateList().add(state);
                            }
                        } else {
                            problemDetailLayout.setVisibility(View.GONE);
                            state.setCondition(GlobalConstants.DEFAULT_SCORE);
                            state.performScoreAdjustments(child);
                            Session.getDefectStateList().remove(state);
                        }
                        notifyDataSetChanged();

                    }
                });

                problemPlaceholderLayout.addView(problemDetailLayout);


                for (DefectState d : Session.getDefectStateList()) {
                    if (d.getPart().equals(ComponentDetailFragment.ARG_CURRENT) && d.getGroupPosition().equals(groupClickedPosition)) {
                        if (checkBox.getId() == d.getCheckboxPosition()) {
                            omvangSpinner.setSelection(d.getExtentId());
                            intensitySpinner.setSelection(d.getIntensityId());
                            actiesSpinner.setSelection(d.getActionId());

                            oplegostSwitch.setChecked(d.isFixed());
                            checkBox.setChecked(true);
                            Integer tempCondition = CalculationHelper.INSTANCE.getCondition(
                                    d.getExtentId(),
                                    d.getIntensityId(),
                                    d.isFixed(),
                                    child.getValue().getAsJsonArray().get(0).getAsString());
                            if (tempCondition != null) d.setCondition(tempCondition);

                            d.setInitialScore(child.getValue().getAsJsonArray().get(1).getAsInt());
                            if (d.getCondition() != null) {
                                d.setCorrelation(CalculationHelper.INSTANCE.getConditionFactor(d.getCondition()));
                                d.setCorrelatedScore(d.getCorrelation() * d.getInitialScore());
                            }
                        }
                    }
                    notifyDataSetChanged();
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
        View ind = convertView.findViewById(R.id.component_detail_image);
        View mScoreView = convertView.findViewById(R.id.component_detail_score);
        if (ind != null) {
            ImageView indicator = (ImageView) ind;
            TextView scoreText = (TextView) mScoreView;
            if (groupPosition == 0) {
                indicator.setVisibility(View.GONE);
                scoreText.setVisibility(View.VISIBLE);
                if (Session.getDefectStateList() != null && Session.getDefectStateList().size() > 0) {
                    Integer partScore = CalculationHelper.INSTANCE.getScoreByPart(Session.getDefectStateList(), ComponentDetailFragment.ARG_CURRENT);
                    if (partScore != null && partScore >= 1) {
                        score = partScore;
                    }
                } else {
                    score = GlobalConstants.DEFAULT_SCORE;
                }
                scoreText.setText(String.valueOf(score));
            } else {
                indicator.setVisibility(View.VISIBLE);
                scoreText.setVisibility(View.GONE);
                indicator.setImageResource(isExpanded ? R.drawable.list_group_expanded : R.drawable.list_group_closed);
            }
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.component_detail_title);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTextAppearance(_context, android.R.style.TextAppearance_Medium);
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
