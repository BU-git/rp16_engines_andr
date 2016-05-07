package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.LMRAAdapter;
import com.bionic.kvt.serviceapp.dialogs.LMRADialog;
import com.bionic.kvt.serviceapp.models.LMRA;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LMRAActivity extends BaseActivity {
    private static final String LMRALISTNAME = "LMRA List";

    @BindView(R.id.activity_lmra_list)
    ListView listViewLMRA;

    public static ArrayList<LMRA> lmraList = new ArrayList<>();
    public static LMRAAdapter lmraAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.lmra_title));

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Saving session for the screen orientation
        if (savedInstanceState != null) {
            lmraList = savedInstanceState.getParcelableArrayList(LMRALISTNAME);
        } else {
            //Prepopulate data from DB, if needed
        }

        lmraAdapter = new LMRAAdapter(this, lmraList);
        listViewLMRA.setAdapter(lmraAdapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LMRALISTNAME, lmraList);
        super.onSaveInstanceState(outState);
    }

    //Add + as a menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_lmra, menu);
        return true;
    }

    //Fire up the Dialog
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_lmra_add:
                AppCompatDialogFragment lmraDialog = new LMRADialog();
                lmraDialog.show(getSupportFragmentManager(), "New LMRA");
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
