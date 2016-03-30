package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.adapters.LMRAAdapter;
import com.bionic.kvt.serviceapp.models.LMRA;

import java.util.ArrayList;

public class LMRAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra);

        this.setTitle(R.string.lmra_title);

        final ArrayList<LMRA> lmraList = new ArrayList<LMRA>();
        final LMRAAdapter lmraAdapter = new LMRAAdapter(this,lmraList);
        final ListView listView = (ListView) findViewById(R.id.lmra_list);
        listView.setAdapter(lmraAdapter);


        LMRA newLMRA = new LMRA("Test1", "test description");
        LMRA newLMRA2 = new LMRA("Test2", "test description");
        LMRA newLMRA3 = new LMRA("Test3", "test description");
        LMRA newLMRA4 = new LMRA("Test4", "test description");
        LMRA newLMRA5 = new LMRA("Test5", "test description");
        LMRA newLMRA6 = new LMRA("Test6", "test description");
        LMRA newLMRA7 = new LMRA("Test7", "test description");
        LMRA newLMRA8 = new LMRA("Test8", "test description");

        lmraAdapter.add(newLMRA);
        lmraAdapter.add(newLMRA2);
        lmraAdapter.add(newLMRA3);
        lmraAdapter.add(newLMRA4);
        lmraAdapter.add(newLMRA5);
        lmraAdapter.add(newLMRA6);
        lmraAdapter.add(newLMRA7);
        lmraAdapter.add(newLMRA8);


    }
}
