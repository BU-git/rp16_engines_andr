package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.ElementExpandableListAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.CalculationHelper;
import com.bionic.kvt.serviceapp.helpers.JSONHelper;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static Map<String, LinkedHashMap<String, JsonObject>> partMap = new LinkedHashMap<>();
    private String TAG = ComponentListActivity.class.getName();
    //public Map<String, JsonObject> elementMap = new HashMap<>();
    private String COMPONENTFILE = BuildConfig.COMPONENTS_JSON;

    private boolean mTwoPane;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_defects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DbUtils.saveDefectStateListToDB(defectStateList);
        DbUtils.saveScoreToDB(Session.getCurrentOrder(), CalculationHelper.INSTANCE.getGeneralScore(partMap, defectStateList));
        Log.d(TAG, "Global score: " + CalculationHelper.INSTANCE.getGeneralScore(partMap, defectStateList));

        Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_component_list);

        new JsonParser().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        View recyclerView = findViewById(R.id.component_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        /*
        Button nextButton = (Button) findViewById(R.id.component_defects_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
                startActivity(intent);
            }
        });
        */


        if (findViewById(R.id.component_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(partMap));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Map<String, LinkedHashMap<String, JsonObject>> mValues;
        private int selectedItem = -1;


        public SimpleItemRecyclerViewAdapter(Map<String, LinkedHashMap<String, JsonObject>> items) {
            mValues = items;
        }

        /*
        public SimpleItemRecyclerViewAdapter(List<Problem> problems) {
            mValues = problems;
        }
        */

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.component_list_content, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            List valuesList = Arrays.asList(mValues.keySet().toArray());
            holder.mItem = valuesList.get(position).toString();
            Log.d(TAG, "Item: " + holder.mItem);
            //final String id = mValues.get(position).keySet().toArray()[position].toString();
            holder.mIdView.setText(mValues.keySet().toArray()[position].toString());
            //holder.mContentView.setText("Test2");

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

    private class JsonParser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String jsonComponent = new JSONHelper().readFromFile(getApplicationContext(), COMPONENTFILE);
            com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

            if (!jsonComponent.isEmpty()) {
                try {
                    JsonElement parent = parser.parse(jsonComponent);

                    //First level, e.g. Motor
                    JsonArray parentArray = parent.getAsJsonArray();

                    for (int k = 0; k < parentArray.size(); k++) {
                        JsonObject secondObject = parentArray.get(k).getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySet = secondObject.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            JsonArray secondArray = entry.getValue().getAsJsonArray();

                            Log.d(TAG, "Part: " + entry.getKey());

                            JsonArray thirdArray = entry.getValue().getAsJsonArray();
                            LinkedHashMap<String, JsonObject> elementMap = new LinkedHashMap<>();
                            for (int j = 0; j < thirdArray.size(); j++) {
                                JsonObject thirdObject = thirdArray.get(j).getAsJsonObject();
                                Set<Map.Entry<String, JsonElement>> entrySetSecond = thirdObject.entrySet();

                                for (Map.Entry<String, JsonElement> entrySecond : entrySetSecond) {
                                    //elementMap.put(entrySecond.getKey(),null);
                                    elementMap.put(entrySecond.getKey(), entrySecond.getValue().getAsJsonObject());
                                    //Log.d(TAG,"Element: " + entrySecond.getKey());
                                    //Log.d(TAG, "Problem: " + entrySecond.getValue());

                                }
                            }
                            partMap.put(entry.getKey(), elementMap);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }


            return null;
        }
    }
}
