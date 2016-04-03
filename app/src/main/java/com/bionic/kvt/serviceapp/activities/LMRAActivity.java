package com.bionic.kvt.serviceapp.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.LMRAAdapter;
import com.bionic.kvt.serviceapp.dialogs.LMRADialog;
import com.bionic.kvt.serviceapp.models.LMRA;

import java.util.ArrayList;

public class LMRAActivity extends AppCompatActivity {

    private static final String LMRALISTNAME = "LMRA List";

    public static ArrayList<LMRA> lmraList = new ArrayList<LMRA>();

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
        switch (id){
            case R.id.menu_lmra_add:
                DialogFragment d = new LMRADialog();
                d.show(getFragmentManager(),"new Lmra");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra);

        this.setTitle(R.string.lmra_title);


        //Saving session for the screen orientation
        if (savedInstanceState != null){
            lmraList = savedInstanceState.getParcelableArrayList(LMRALISTNAME);
        } else {
            //Prepopulate data from DB, if needed
        }

        final LMRAAdapter lmraAdapter = new LMRAAdapter(this,lmraList);
        final ListView listView = (ListView) findViewById(R.id.lmra_list);
        listView.setAdapter(lmraAdapter);

    }
}
