package com.bionic.kvt.serviceapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.ElementExpandableListAdapter;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

/**
 * A fragment representing a single Component detail screen.
 * This fragment is either contained in a {@link ComponentListActivity}
 * in two-pane mode (on tablets) or a {@link ComponentDetailActivity}
 * on handsets.
 */
public class ComponentDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public static String ARG_CURRENT;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    String TAG = ComponentDetailFragment.class.getName();
    private Integer layoutId = 0;
    private Integer checkboxId = 0;

    private int expanded = -1;
    private int collapsed = -2;

    /**
     * The dummy content this fragment is presenting.
     */
    private LinkedHashMap<String, JsonObject> mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ComponentDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Session.getPartMap().get(getArguments().getString(ARG_ITEM_ID));
            ARG_CURRENT = getArguments().getString(ARG_ITEM_ID);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getArguments().getString(ARG_ITEM_ID).trim());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        final View rootView = inflater.inflate(R.layout.component_detail, container, false);
        rootView.setNestedScrollingEnabled(true);
        rootView.setNestedScrollingEnabled(true);

        if (mItem != null) {
            final ExpandableListView list = (ExpandableListView) rootView.findViewById(R.id.component_detail);
            final ExpandableListAdapter listAdapter = new ElementExpandableListAdapter(rootView.getContext(), mItem);

            list.setAdapter(listAdapter);

            list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {

                    layoutId = Integer.valueOf(String.valueOf(ElementExpandableListAdapter.layoutMagicNumber) +
                            ElementExpandableListAdapter.groupClickedPosition +
                            groupPosition);

                    if (rootView.findViewById(layoutId) != null) {
                        rootView.findViewById(layoutId).setVisibility(View.GONE);
                    }
                }
            });


            list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                        //Id of the view to Restore
                        Integer id = Integer.valueOf(String.valueOf(ElementExpandableListAdapter.layoutMagicNumber) +
                                ElementExpandableListAdapter.groupClickedPosition +
                                groupPosition);
                        if (i != groupPosition) {
                            if (list.isGroupExpanded(i)) {
                                list.collapseGroup(i);
                            }
                        } else {
                            if (rootView.findViewById(id) != null) {
                                rootView.findViewById(id).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });

            list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    //Disallow clicks on already expanded group
                    return list.isGroupExpanded(groupPosition) || groupPosition == 0;

                }
            });
        }

        return rootView;
    }
}
