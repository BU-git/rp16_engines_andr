package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.helpers.JSONHelper;
import com.bionic.kvt.serviceapp.models.Problem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComponentDefectsActivity extends BaseActivity {

    private List<Problem> problemList;
    private String COMPONENTFILE = BuildConfig.COMPONENTS_JSON;

    private String TAG = ComponentDefectsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_defects);
        ButterKnife.bind(this);
    }

        new JsonParser().execute();

        Button nextButton = (Button) findViewById(R.id.component_defects_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeasurementsActivity.class);
                startActivity(intent);
            }
        });


        }
    private class JsonParser extends AsyncTask<Void,Void,Void> {

        //String jsonComponent = new FileReader(new File(COMPONENTFILE));

        @Override
        protected Void doInBackground(Void... params) {

            String jsonComponent = new JSONHelper().readFromFile(getApplicationContext(), COMPONENTFILE);
            Log.d(TAG,"JSON Component:" + jsonComponent);
            if (!jsonComponent.isEmpty()) {
                try {
                    JSONArray partsJsonArray = new JSONArray(jsonComponent);
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }

            return null;
        }
    }
}
