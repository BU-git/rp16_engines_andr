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

            //Dummy data
            LMRA newLMRA = new LMRA("Test1", "test description");
            LMRA newLMRA2 = new LMRA("Test2", "test description");
            LMRA newLMRA3 = new LMRA("Test3", "test description");
            LMRA newLMRA4 = new LMRA("Test4", "test description");
            LMRA newLMRA5 = new LMRA("Test5", "test description");
            LMRA newLMRA6 = new LMRA("Test6", "test description");
            LMRA newLMRA7 = new LMRA("Test7", "test description");
            LMRA newLMRA8 = new LMRA("Test8", "test description");

            lmraList.add(newLMRA);
            lmraList.add(newLMRA2);
            lmraList.add(newLMRA3);
            lmraList.add(newLMRA4);
            lmraList.add(newLMRA5);
            lmraList.add(newLMRA6);
            lmraList.add(newLMRA7);
            lmraList.add(newLMRA8);

        }

        final LMRAAdapter lmraAdapter = new LMRAAdapter(this,lmraList);
        final ListView listView = (ListView) findViewById(R.id.lmra_list);
        listView.setAdapter(lmraAdapter);

    }
}
