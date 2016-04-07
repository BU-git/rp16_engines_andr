package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.views.DrawingView;

import java.io.File;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.insertImage;

public class InsertSignaturesActivity extends AppCompatActivity {

    private DrawingView engineerDrawingView;
    private DrawingView clientDrawingView;
    private Button buttonComplete;
    private ToggleButton buttonConfirmEngineer;
    private ToggleButton buttonConfirmClient;

    public boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public File getPrivateDocumentsStorageDir(Context context, String folder) {
        File storageDir = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), folder);
        if (!storageDir.mkdirs()) {
            Log.e(getLocalClassName(), "Directory not created: " + storageDir.toString());
        }
        return storageDir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insert_signatures);

        if (!isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

        File publicDocumentsStorageDir = getPrivateDocumentsStorageDir(getApplicationContext(), "KVTImages");
        if (!publicDocumentsStorageDir.exists()) {
            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        engineerDrawingView = (DrawingView) findViewById(R.id.draw_engineer_signature);
        clientDrawingView = (DrawingView) findViewById(R.id.draw_client_signature);

        Button buttonClearEngineer = (Button) findViewById(R.id.button_clear_engineer);
        buttonClearEngineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engineerDrawingView.clearCanvas();
            }
        });

        Button buttonClearClient = (Button) findViewById(R.id.button_clear_client);
        buttonClearClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientDrawingView.clearCanvas();
            }
        });

        buttonComplete = (Button) findViewById(R.id.button_complete);
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
                startActivity(intent);
            }
        });

        buttonConfirmEngineer = (ToggleButton) findViewById(R.id.button_confirm_engineer);
        buttonConfirmClient = (ToggleButton) findViewById(R.id.button_confirm_client);

        buttonConfirmEngineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engineerDrawingView.setDrawingCacheEnabled(true);
                String engineerSignature = insertImage(
                        getContentResolver(),
                        engineerDrawingView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png",
                        "Engineer's signature"
                );
                if (engineerSignature == null) {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Signatures could not be saved", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                    buttonConfirmEngineer.setChecked(false);
                }
                engineerDrawingView.destroyDrawingCache();
                buttonComplete.setEnabled(buttonConfirmEngineer.isChecked() & buttonConfirmClient.isChecked());
            }
        });


        buttonConfirmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientDrawingView.setDrawingCacheEnabled(true);
                String clientSignature = insertImage(
                        getContentResolver(),
                        clientDrawingView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png",
                        "Client's signature"
                );
                if (clientSignature == null) {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Signature could not be saved", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                    buttonConfirmClient.setChecked(false);
                }
                clientDrawingView.destroyDrawingCache();
                buttonComplete.setEnabled(buttonConfirmEngineer.isChecked() & buttonConfirmClient.isChecked());
            }
        });
    }

}