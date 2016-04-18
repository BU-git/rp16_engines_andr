package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.bionic.kvt.serviceapp.views.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsertSignaturesActivity extends BaseActivity {
    @Bind(R.id.draw_engineer_signature)
    DrawingView engineerDrawingView;

    @Bind(R.id.draw_client_signature)
    DrawingView clientDrawingView;

    @Bind(R.id.button_complete)
    Button buttonComplete;

    @Bind(R.id.button_confirm_engineer)
    ToggleButton buttonConfirmEngineer;

    @Bind(R.id.button_confirm_client)
    ToggleButton buttonConfirmClient;

    private final int ENGINEER_BUTTON = 1;
    private final int CLIENT_BUTTON = 2;
    private int currentButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_signatures);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_clear_engineer)
    public void onClearEngineerClick(View v) {
        engineerDrawingView.clearCanvas();
        buttonConfirmEngineer.setChecked(false);
        buttonComplete.setEnabled(false);
    }

    @OnClick(R.id.button_clear_client)
    public void onClearClientClick(View v) {
        clientDrawingView.clearCanvas();
        buttonConfirmClient.setChecked(false);
        buttonComplete.setEnabled(false);
    }

    @OnClick(R.id.button_complete)
    public void onCompleteClick(View v) {
        Intent intent = new Intent(getApplicationContext(), PDFReportActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_confirm_engineer)
    public void onConfirmEngineerClick(View v) {
        currentButtonClicked = ENGINEER_BUTTON;
        onConfirmClicked();
    }

    @OnClick(R.id.button_confirm_client)
    public void onConfirmClientClick(View v) {
        currentButtonClicked = CLIENT_BUTTON;
        onConfirmClicked();
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
        String signatureFileName;
        switch (currentButtonClicked) {
            case ENGINEER_BUTTON:
                currentDrawingView = engineerDrawingView;
                description = "Engineer's signature";
                currentToggleButton = buttonConfirmEngineer;
                signatureFileName = "signature_engineer.png";
                break;
            case CLIENT_BUTTON:
            default:
                currentDrawingView = clientDrawingView;
                description = "Client's signature";
                currentToggleButton = buttonConfirmClient;
                signatureFileName = "client_engineer.png";
                break;
        }


        if (currentDrawingView.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please, draw signature.", Toast.LENGTH_SHORT).show();
            currentToggleButton.setChecked(false);
            currentButtonClicked = 0;
            return;
        }

        Session.setCurrentSignatureFolder(Utils.getPrivateDocumentsStorageDir(
                getApplicationContext(),
                Session.getCurrentOrder() + "/" + BuildConfig.SINGNATURE_FOLDER));

        final File signatureFile = new File(Session.getCurrentSignatureFolder(), signatureFileName);
        final Bitmap signatureBitmap = currentDrawingView.getDrawingCache();

//        try (FileOutputStream fileOutputStream = new FileOutputStream(signatureFile)) {
//            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//        } catch (IOException e) {
//            if (BuildConfig.IS_LOGGING_ON)
//                Session.addToSessionLog("ERROR writing: " + signatureFile + e.toString());
//            Toast.makeText(getApplicationContext(), "Signature could not be saved", Toast.LENGTH_SHORT).show();
//            currentToggleButton.setChecked(false);
//        }


//        currentDrawingView.setDrawingCacheEnabled(true);
//        String signature = insertImage(
//                getContentResolver(),
//                currentDrawingView.getDrawingCache(),
//                UUID.randomUUID().toString() + ".png",
//                description
//        );
//
//        if (signature == null) {
//            Toast.makeText(getApplicationContext(), "Signatures could not be saved", Toast.LENGTH_SHORT).show();
//            currentToggleButton.setChecked(false);
//        }


        currentToggleButton.setChecked(true);
        currentDrawingView.destroyDrawingCache();
        buttonComplete.setEnabled(buttonConfirmEngineer.isChecked() & buttonConfirmClient.isChecked());
        currentButtonClicked = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.REQUEST_WRITE_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                onConfirmClicked();
            }
        }
    }
}