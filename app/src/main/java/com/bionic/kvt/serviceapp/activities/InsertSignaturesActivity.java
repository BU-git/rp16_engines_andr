package com.bionic.kvt.serviceapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.views.DrawingView;

import java.util.UUID;

import static android.provider.MediaStore.Images.Media.*;

public class InsertSignaturesActivity extends AppCompatActivity {

    private DrawingView engineerDrawingView;

    private DrawingView clientDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insert_signatures);

        engineerDrawingView = (DrawingView) findViewById(R.id.draw_engineer_signature);
        clientDrawingView = (DrawingView) findViewById(R.id.draw_client_signature);

        Button buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engineerDrawingView.setDrawingCacheEnabled(true);
                clientDrawingView.setDrawingCacheEnabled(true);
                String engineerSignature = insertImage(
                        getContentResolver(),
                        engineerDrawingView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png",
                        "Engineer's signature"
                );
                String clientSignature = insertImage(
                        getContentResolver(),
                        clientDrawingView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png",
                        "Client's signature"
                );
                if (engineerSignature != null && clientSignature != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Signatures saved to Gallery", Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Signatures could not be saved", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                engineerDrawingView.destroyDrawingCache();
                clientDrawingView.destroyDrawingCache();
            }
        });
    }

}