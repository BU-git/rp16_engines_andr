package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.bionic.kvt.serviceapp.views.DrawingView;

import java.io.File;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.insertImage;

public class InsertSignaturesActivity extends BaseActivity {
    private DrawingView engineerDrawingView;
    private DrawingView clientDrawingView;
    private Button buttonComplete;
    private ToggleButton buttonConfirmEngineer;
    private ToggleButton buttonConfirmClient;

    private final int ENGINEER_BUTTON = 1;
    private final int CLIENT_BUTTON = 2;
    private int currentButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_signatures);

        engineerDrawingView = (DrawingView) findViewById(R.id.draw_engineer_signature);
        clientDrawingView = (DrawingView) findViewById(R.id.draw_client_signature);

        Button buttonClearEngineer = (Button) findViewById(R.id.button_clear_engineer);
        buttonClearEngineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engineerDrawingView.clearCanvas();
                buttonConfirmEngineer.setChecked(false);
                buttonComplete.setEnabled(false);
            }
        });

        Button buttonClearClient = (Button) findViewById(R.id.button_clear_client);
        buttonClearClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientDrawingView.clearCanvas();
                buttonConfirmClient.setChecked(false);
                buttonComplete.setEnabled(false);
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
        buttonConfirmEngineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentButtonClicked = ENGINEER_BUTTON;
                onConfirmClicked();
            }
        });

        buttonConfirmClient = (ToggleButton) findViewById(R.id.button_confirm_client);
        buttonConfirmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentButtonClicked = CLIENT_BUTTON;
                onConfirmClicked();
            }
        });
    }

    private void onConfirmClicked() {
        if (Utils.needRequestWritePermission(getApplicationContext(), this)) {
            buttonConfirmEngineer.setChecked(false);
            buttonConfirmClient.setChecked(false);
            buttonComplete.setEnabled(false);
            return;
        }

        if (!Utils.isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

        File publicDocumentsStorageDir = Utils.getPublicDirectoryStorageDir(Environment.DIRECTORY_PICTURES, "KVTPictures");
        if (!publicDocumentsStorageDir.exists()) {
            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        DrawingView currentDrawingView;
        String description;
        ToggleButton currentToggleButton;
        switch (currentButtonClicked) {
            case ENGINEER_BUTTON:
                currentDrawingView = engineerDrawingView;
                description = "Engineer's signature";
                currentToggleButton = buttonConfirmEngineer;
                break;
            default: // CLIENT_BUTTON:
                currentDrawingView = clientDrawingView;
                description = "Client's signature";
                currentToggleButton = buttonConfirmClient;
                break;
        }

        currentDrawingView.setDrawingCacheEnabled(true);
        String signature = insertImage(
                getContentResolver(),
                currentDrawingView.getDrawingCache(),
                UUID.randomUUID().toString() + ".png",
                description
        );

        if (signature == null) {
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Signatures could not be saved", Toast.LENGTH_SHORT);
            unsavedToast.show();
            currentToggleButton.setChecked(false);
        }

        currentToggleButton.setChecked(true);
        currentDrawingView.destroyDrawingCache();
        buttonComplete.setEnabled(buttonConfirmEngineer.isChecked() & buttonConfirmClient.isChecked());
        currentButtonClicked = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.REQUEST_WRITE_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                onConfirmClicked();
            }
        }
    }
}