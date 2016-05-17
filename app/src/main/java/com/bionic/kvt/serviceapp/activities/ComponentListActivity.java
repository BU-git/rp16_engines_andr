package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.ElementExpandableListAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.CalculationHelper;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a list of Components. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ComponentDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ComponentListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static List<DefectState> defectStateList = new ArrayList<>();
    private String TAG = ComponentListActivity.class.getName();

    @BindView(R.id.component_list)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);
        ButterKnife.bind(this);

        Utils.runBackgroundServiceIntent(ComponentListActivity.this, GlobalConstants.GENERATE_PART_MAP);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Session.getPartMap()));

        if (findViewById(R.id.component_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @OnClick(R.id.component_list_next_button)
    public void OnNextClick(View v) {
        DbUtils.saveDefectStateListToDB(defectStateList);
        DbUtils.saveScoreToDB(Session.getCurrentOrder(), CalculationHelper.INSTANCE.getGeneralScore(Session.getPartMap(), defectStateList));

        Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
        startActivity(intent);
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Map<String, LinkedHashMap<String, JsonObject>> mValues;
        private int selectedItem = -1;

        public SimpleItemRecyclerViewAdapter(Map<String, LinkedHashMap<String, JsonObject>> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            List valuesList = Arrays.asList(mValues.keySet().toArray());
            holder.mItem = valuesList.get(position).toString();
//            Log.d(TAG, "Item: " + holder.mItem);
            holder.mIdView.setText(mValues.keySet().toArray()[position].toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ElementExpandableListAdapter.groupClickedPosition = position;
                    selectedItem = position;
                    notifyDataSetChanged();

                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ComponentDetailFragment.ARG_ITEM_ID, holder.mItem);
                        ComponentDetailFragment fragment = new ComponentDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.component_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ComponentDetailActivity.class);
                        intent.putExtra(ComponentDetailFragment.ARG_ITEM_ID, holder.mItem);

                        context.startActivity(intent);
                    }

                }
            });
            if (position == selectedItem) {
                holder.mView.setSelected(true);
            } else {
                holder.mView.setSelected(false);
            }

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public String mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}