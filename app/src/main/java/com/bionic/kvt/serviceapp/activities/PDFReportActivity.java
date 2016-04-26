package com.bionic.kvt.serviceapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.MailHelper;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_END_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_CLIENT;
import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_ENGINEER;

public class PDFReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    private static final int PDF_LOADER_ID = 1;
    private static final int MAIL_LOADER_ID = 2;
    private MailHelper mailHelper;
    private File pdfReportFile;
    private File pdfReportPreviewFile;
    private int zoomFactor = 2;

    @Bind(R.id.pdf_report_send_button)
    Button sendButton;

    @Bind(R.id.pdf_text_log)
    TextView pdfTextLog;

    @Bind(R.id.zoomControls)
    ZoomControls zoomControls;

    @Bind(R.id.pdf_report_bottom)
    LinearLayout reportBottomLayout;

    @Bind(R.id.pdf_bitmap)
    ImageView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report);
        ButterKnife.bind(this);

        final long orderNumber = Session.getCurrentOrder();

        // Exit if Session is empty
        if (orderNumber == 0L) {
            Toast.makeText(getApplicationContext(),
                    "No order number to show PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.pdf_report));

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomFactor == 4) return;
                zoomFactor++;
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, zoomFactor, pdfView);
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomFactor == 1) return;
                zoomFactor--;
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, zoomFactor, pdfView);
            }
        });

        String pdfReportFileName = getText(R.string.generating_pdf_document).toString()
                + " " + PDF_REPORT_FILE_NAME + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFileName);


        if (DbUtils.getOrderStatus(orderNumber) == ORDER_STATUS_COMPLETE) {
            reportBottomLayout.setVisibility(View.GONE);
        }

        pdfReportFile = Utils.getPDFReportFileName(false);
        if (pdfReportFile.exists()) { // We have report.
            sendButton.setEnabled(true);
            Utils.showPDFReport(getApplicationContext(), pdfReportFile, zoomFactor, pdfView);
        } else { // No report. Generating...

            if (!Utils.isExternalStorageWritable()) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Can not write report file to external storage!");
                Toast.makeText(getApplicationContext(),
                        "ERROR: Can not write report file to external storage!", Toast.LENGTH_SHORT).show();
                return;
            }

            pdfReportPreviewFile = Utils.getPDFReportFileName(true);
            if (!pdfReportPreviewFile.exists()) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Can not get pdf preview file!");
                Toast.makeText(getApplicationContext(),
                        "ERROR: Can not get PDF preview file!", Toast.LENGTH_SHORT).show();
                return;
            }

            getSupportLoaderManager().initLoader(PDF_LOADER_ID, null, this);

        }
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (id == PDF_LOADER_ID)
            return new GeneratePDFReportFile(this, pdfReportFile, pdfReportPreviewFile);
        if (id == MAIL_LOADER_ID)
            return new MailHelper.SendMail(this, mailHelper);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        switch (loader.getId()) {
            case PDF_LOADER_ID:
                sendButton.setEnabled(true);
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, zoomFactor, pdfView);
                break;
            case MAIL_LOADER_ID:
                if (data) {
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.success_email_toast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.error_email_toast), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        // NOOP
    }

    public static class GeneratePDFReportFile extends AsyncTaskLoader<Boolean> {
        private final int pdfPageCount = 1;
        private final File pdfReportFile;
        private final File pdfReportPreviewFile;

        public GeneratePDFReportFile(Context context, File pdfReportFile, File pdfReportPreviewFile) {
            super(context);
            this.pdfReportFile = pdfReportFile;
            this.pdfReportPreviewFile = pdfReportPreviewFile;
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
            try {
                final PdfReader pdfReader = new PdfReader(pdfReportPreviewFile.toString());
                final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfReportFile));
                final PdfContentByte contentByte = pdfStamper.getOverContent(pdfPageCount);

                String signatureFileName = SIGNATURE_FILE_ENGINEER;
                String signaturePath = new File(Utils.getCurrentOrderDir(), signatureFileName).toString();
                Image signatureEngineer = Image.getInstance(signaturePath);
                signatureEngineer.setAbsolutePosition(325f, 135f);
                signatureEngineer.scaleAbsolute(192, 74);
                contentByte.addImage(signatureEngineer);

                signatureFileName = SIGNATURE_FILE_CLIENT;
                signaturePath = new File(Utils.getCurrentOrderDir(), signatureFileName).toString();
                Image signatureClient = Image.getInstance(signaturePath);
                signatureClient.setAbsolutePosition(102f, 135f);
                signatureClient.scaleAbsolute(192, 74);
                contentByte.addImage(signatureClient);

                pdfStamper.close();
            } catch (FileNotFoundException e) {
                Session.addToSessionLog("ERROR: 1" + e.toString());
            } catch (IOException e) {
                Session.addToSessionLog("ERROR: 2" + e.toString());
            } catch (NullPointerException e) {
                Session.addToSessionLog("ERROR: 3" + e.toString());
            } catch (DocumentException e) {
                Session.addToSessionLog("ERROR: 4" + e.toString());
            }

            //TODO EXEPTION REVISE
            Utils.cleanSignatureFile(SIGNATURE_FILE_ENGINEER);
            Utils.cleanSignatureFile(SIGNATURE_FILE_CLIENT);
            return false;
        }

    }

    @OnClick(R.id.pdf_button_complete_order)
    public void onDoneClick(View v) {
        DbUtils.setOrderMaintenanceTime(Session.getCurrentOrder(), ORDER_MAINTENANCE_END_TIME, new Date());
        DbUtils.setOrderStatus(Session.getCurrentOrder(), ORDER_STATUS_COMPLETE);
        pdfReportPreviewFile.delete();

        Intent intent = new Intent(getApplicationContext(), OrderPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.pdf_report_send_button)
    public void onSendClick(View v) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getText(R.string.email_dialog_title));
        final EditText emailEdit = new EditText(this);
        emailEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEdit.setText(Session.getEngineerEmail());
        dialogBuilder.setView(emailEdit);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmailWithPDF(emailEdit.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    private void sendEmailWithPDF(final String email) {
        mailHelper = new MailHelper();
        mailHelper.setRecipient(email);
        mailHelper.setMessageBody("This is a PDF report");
        mailHelper.setSubject("PDF_Report");
        mailHelper.setFullFileName(pdfReportFile.toString());

        getSupportLoaderManager().restartLoader(MAIL_LOADER_ID, null, this);
    }
}



