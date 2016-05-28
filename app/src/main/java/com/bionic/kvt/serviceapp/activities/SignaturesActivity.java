package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.bionic.kvt.serviceapp.views.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * An activity for entering signatures.<br>
 * Started by {@link PDFReportPreviewActivity}.<br>
 * Next activity {@link PDFReportActivity}.<br>
 * <p/>
 * Get signatures for engineer and client. <br>
 * Use custom view {@link SignatureView} for entering signatures.<br>
 * Use {@link Session} as a temporal signature bitmap storage: <br>
 * {@link Session#setByteArrayEngineerSignature(byte[])}},<br>
 * {@link Session#setByteArrayClientSignature(byte[])}.
 */

public class SignaturesActivity extends BaseActivity {
    @BindView(R.id.draw_engineer_signature)
    SignatureView engineerDrawingView;

    @BindView(R.id.draw_client_signature)
    SignatureView clientDrawingView;

    @BindView(R.id.button_complete)
    Button buttonComplete;

    @BindView(R.id.signature_engineer_name)
    EditText engineerName;

    @BindView(R.id.signature_client_name)
    EditText clientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signatures);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.signatures));

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(SignaturesActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + SignaturesActivity.class.getSimpleName());

        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order currentOrder = realm.where(Order.class).equalTo("number", Session.getCurrentOrder()).findFirst();
            if (currentOrder == null) {
                AppLog.E(this, "No order found with number: " + Session.getCurrentOrder());
                return;
            }
            clientName.setText(currentOrder.getRelation().getContactPerson());
            engineerName.setText(currentOrder.getEmployee().getName());
        }

    }

    @OnClick(R.id.button_clear_engineer)
    public void onClearEngineerClick(View v) {
        engineerDrawingView.clear();
    }

    @OnClick(R.id.button_clear_client)
    public void onClearClientClick(View v) {
        clientDrawingView.clear();
    }

    @OnClick(R.id.button_complete)
    public void onCompleteClick(View v) {
        if (engineerDrawingView.isEmpty()) {
            AppLog.W(this, getText(R.string.no_engineer_signature).toString());
            return;
        }

        if (clientDrawingView.isEmpty()) {
            AppLog.W(this, getText(R.string.no_client_signature).toString());
            return;
        }

        // Saving Engineers signature
        engineerDrawingView.setDrawingCacheEnabled(true);
        Bitmap signatureBitmap = engineerDrawingView.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        Session.setByteArrayEngineerSignature(byteArrayOutputStream.toByteArray());
        engineerDrawingView.destroyDrawingCache();

        // Saving Client signature
        clientDrawingView.setDrawingCacheEnabled(true);
        signatureBitmap = clientDrawingView.getDrawingCache();
        byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        Session.setByteArrayClientSignature(byteArrayOutputStream.toByteArray());
        clientDrawingView.destroyDrawingCache();

        final File pdfReportFile = Utils.getPDFReportFileName(Session.getCurrentOrder(), false);
        if (pdfReportFile.exists()) pdfReportFile.delete();

        final Intent intent = new Intent(this, PDFReportActivity.class);
        intent.putExtra("ENGINEER_NAME", engineerName.getText().toString());
        intent.putExtra("CLIENT_NAME", clientName.getText().toString());
        startActivity(intent);
    }

}