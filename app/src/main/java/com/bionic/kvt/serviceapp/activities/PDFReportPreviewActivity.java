package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.db.OrderReportJobRules;
import com.bionic.kvt.serviceapp.db.OrderReportMeasurements;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.AppLogItem;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * An activity for PDF Report preview.<br>
 * Started by {@link JobRulesActivity}.<br>
 * Next activity {@link SignaturesActivity}.<br>
 * <p/>
 * Use PDF template for current language from application asset. <br>
 * Generate PDF Report preview file in current order folder: {@link Utils#getOrderDir(long)}.
 */

public class PDFReportPreviewActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    private static final String ROTATION_FLAG = "ROTATION_FLAG";
    private static final int PDF_LOADER_ID = 1;
    private File pdfReportPreviewFile;
    private File pdfTemplate;

    // App Log monitor
    private Realm monitorLogRealm = Session.getLogRealm();
    private RealmChangeListener<RealmResults<AppLogItem>> logListener;
    private RealmResults<AppLogItem> logsWithNotification;

    @BindView(R.id.pdf_preview_text_log)
    TextView pdfTextLog;

    @BindView(R.id.pdf_preview_bitmap)
    ImageView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report_preview);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.pdf_report_preview));

        // Setting App log listener
        logListener = AppLog.setLogListener(PDFReportPreviewActivity.this, monitorLogRealm);
        logsWithNotification = AppLog.addListener(monitorLogRealm, logListener);

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(PDFReportPreviewActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + PDFReportPreviewActivity.class.getSimpleName());

        final long orderNumber = Session.getCurrentOrder();


        pdfReportPreviewFile = Utils.getPDFReportFileName(orderNumber, true);

        final String pdfReportFileName = getText(R.string.generating_pdf_preview_document).toString()
                + " " + pdfReportPreviewFile.getName();
        pdfTextLog.setText(pdfReportFileName);

        // Check rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ROTATION_FLAG)) {
                // Device is rotated. No need to generate. Just show.
                Utils.showPDFReport(this, pdfReportPreviewFile, pdfView);
                return;
            }
        }

        if (pdfReportPreviewFile.exists()) { // We have old report preview
            pdfReportPreviewFile.delete();
        }

        // Generating...
        pdfTemplate = Utils.getPDFTemplateFile(this);
        if (pdfTemplate == null || !pdfTemplate.exists()) {
            AppLog.E(this, "Can not get pdf template.");
            return;
        }

        getSupportLoaderManager().initLoader(PDF_LOADER_ID, null, this);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (id == PDF_LOADER_ID)
            return new GeneratePDFReportFile(this, pdfReportPreviewFile, pdfTemplate);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        switch (loader.getId()) {
            case PDF_LOADER_ID:
                Utils.showPDFReport(this, pdfReportPreviewFile, pdfView);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        // NOOP
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ROTATION_FLAG, true);
    }

    public static class GeneratePDFReportFile extends AsyncTaskLoader<Boolean> {
        private final int pdfPageCount = 1;
        private final File pdfReportPreviewFile;
        private final File pdfTemplate;

        public GeneratePDFReportFile(Context context, File pdfReportPreviewFile, File pdfTemplate) {
            super(context);
            this.pdfReportPreviewFile = pdfReportPreviewFile;
            this.pdfTemplate = pdfTemplate;
        }

        @Override
        public void forceLoad() {
            super.forceLoad();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
        }

        @Override
        public void deliverResult(Boolean data) {
            super.deliverResult(data);
        }

        @Override
        public Boolean loadInBackground() {
            final long orderNumber = Session.getCurrentOrder();
            final Realm realm = Realm.getDefaultInstance();
            final Order currentOrder = realm.where(Order.class).equalTo("number", orderNumber).findFirst();
            if (currentOrder == null) return null;

            // Getting Order data for pdf
            String pdfOrderNumber = orderNumber + "\n";
            String pdfRelation = currentOrder.getRelation().getName();
            // Temp solution - cutting to long string
            pdfRelation = pdfRelation.length() > 25 ? pdfRelation.substring(0, 25) : pdfRelation;
            pdfRelation += "\n";
            String pdfRelationTown = currentOrder.getRelation().getTown() + "\n";
            String pdfPerson = currentOrder.getRelation().getContactPerson() + "\n";
            String pdfRelationTelephone = currentOrder.getRelation().getTelephone() + "\n";
            String pdfEmployee = currentOrder.getEmployee().getName();

            String pdfDate = Utils.getDateStringFromDate(currentOrder.getDate()) + "\n";
            String pdfReference = currentOrder.getReference() + "\n";
            String pdfInstallation = currentOrder.getInstallation().getName() + "\n";
            String pdfInstallationAddress = currentOrder.getInstallation().getAddress() + "\n";
            String pdfInstallationTown = currentOrder.getInstallation().getTown() + "\n";

            String pdfMaintenanceStartTime = Utils.getDateTimeStringFromDate(currentOrder.getMaintenanceStartTime());
            String pdfMaintenanceEndTime = Utils.getDateTimeStringFromDate(currentOrder.getMaintenanceEndTime());

            String pdfRunningHours = " ";
            final OrderReportMeasurements currentMeasurements = realm.where(OrderReportMeasurements.class)
                    .equalTo("number", orderNumber).findFirst();
            if (currentMeasurements != null)
                pdfRunningHours = currentMeasurements.getRunningHours();

            String pdfTask = currentOrder.getTasks().first().getLtxa1();

            String pdfScore = String.valueOf(currentOrder.getScore());

            String remainingWork = getContext().getText(R.string.no).toString();
            String externalRemarksText = "";
            final OrderReportJobRules currentJobRules = realm.where(OrderReportJobRules.class)
                    .equalTo("number", orderNumber).findFirst();
            if (currentJobRules != null) {
                remainingWork = currentJobRules.isRemainingWork() ?
                        getContext().getText(R.string.yes).toString() :
                        getContext().getText(R.string.no).toString();
                externalRemarksText = currentJobRules.getExternalRemarksText();
            }

            realm.close();

            try {
                final PdfReader pdfReader = new PdfReader(pdfTemplate.toString());
                final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfReportPreviewFile));
                final Font font = new Font(Font.HELVETICA, 11, Font.NORMAL);
                final PdfContentByte contentByte = pdfStamper.getOverContent(pdfPageCount);
                final ColumnText columnText = new ColumnText(contentByte);

                Phrase orderText1 = new Phrase(pdfOrderNumber +
                        pdfRelation +
                        pdfRelationTown +
                        pdfPerson +
                        pdfRelationTelephone +
                        pdfEmployee,
                        font
                );

                int x = 150;
                int y = 515;
                columnText.setSimpleColumn(orderText1, x, y, x + 200, y + 150, 21.8f, Element.ALIGN_LEFT);
                columnText.go();

                Phrase orderText2 = new Phrase(pdfDate +
                        pdfReference +
                        pdfInstallation +
                        pdfInstallationAddress +
                        pdfInstallationTown +
                        pdfRunningHours,
                        font
                );
                x = 425;
                y = 515;
                columnText.setSimpleColumn(orderText2, x, y, x + 150, y + 150, 21.8f, Element.ALIGN_LEFT);
                columnText.go();

                Phrase orderText3 = new Phrase(pdfTask, font);
                x = 150;
                y = 495;
                columnText.setSimpleColumn(orderText3, x, y, x + 400, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText.go();

                Phrase orderText4 = new Phrase(pdfScore, font);
                x = 150;
                y = 477;
                ColumnText columnText1 = new ColumnText(contentByte);
                columnText1.setSimpleColumn(orderText4, x, y, x + 490, y + 25, 21.8f, Element.ALIGN_TOP);
                columnText1.go();

                Phrase orderText5 = new Phrase(remainingWork, font);
                x = 150;
                y = 459;
                ColumnText columnText2 = new ColumnText(contentByte);
                columnText2.setSimpleColumn(orderText5, x, y, x + 400, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText2.go();

                Phrase orderText6 = new Phrase(pdfMaintenanceStartTime, font);
                if (Utils.isCurrentLangDutch(getContext())) x = 150;
                else x = 183;
                y = 441;
                ColumnText columnText3 = new ColumnText(contentByte);
                columnText3.setSimpleColumn(orderText6, x, y, x + 400, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText3.go();

                Phrase orderText7 = new Phrase(pdfMaintenanceEndTime, font);
                x = 430;
                y = 441;
                ColumnText columnText4 = new ColumnText(contentByte);
                columnText4.setSimpleColumn(orderText7, x, y, x + 400, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText4.go();

                Phrase orderText8 = new Phrase(externalRemarksText, font);
                x = 60;
                y = 321;
                ColumnText columnText5 = new ColumnText(contentByte);
                columnText5.setSimpleColumn(orderText8, x, y, x + 490, y + 100, 15f, Element.ALIGN_LEFT);
                columnText5.go();

                pdfStamper.close();
            } catch (IOException | DocumentException e) {
                AppLog.serviceE(true, orderNumber, "Error while generating PDF: " + e.toString());
            }

            return false;
        }

    }

    @OnClick(R.id.pdf_preview_button_next)
    public void onDoneClick(View v) {
        final Intent intent = new Intent(getApplicationContext(), SignaturesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLog.removeListener(monitorLogRealm, logsWithNotification, logListener);
    }
}



