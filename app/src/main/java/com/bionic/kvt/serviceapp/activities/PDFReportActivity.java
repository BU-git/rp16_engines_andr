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

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.helpers.MailHelper;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_MAINTENANCE_END_TIME;
import static com.bionic.kvt.serviceapp.GlobalConstants.ORDER_STATUS_COMPLETE;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;

public class PDFReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    private static final String ROTATION_FLAG = "ROTATION_FLAG";
    private static final int PDF_LOADER_ID = 1;
    private static final int MAIL_LOADER_ID = 2;
    private MailHelper mailHelper;
    private File pdfReportFile;
    private File pdfReportPreviewFile;

    private String engineerName;
    private String clientName;

    private AlertDialog enterEmailDialog;

    @BindView(R.id.pdf_report_send_button)
    Button sendButton;

    @BindView(R.id.pdf_text_log)
    TextView pdfTextLog;

    @BindView(R.id.pdf_report_bottom)
    LinearLayout reportBottomLayout;

    @BindView(R.id.pdf_bitmap)
    ImageView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            engineerName = extras.getString("ENGINEER_NAME");
            clientName = extras.getString("CLIENT_NAME");
        }

        final long orderNumber = Session.getCurrentOrder();

        // Exit if Session is empty
        if (orderNumber == 0L) {
            Toast.makeText(getApplicationContext(), "No order number to show PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.pdf_report));

        String pdfReportFileName = getText(R.string.generating_pdf_document).toString()
                + " " + PDF_REPORT_FILE_NAME + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFileName);


        pdfReportFile = Utils.getPDFReportFileName(Session.getCurrentOrder(), false);
        if (DbUtils.getOrderStatus(orderNumber) == ORDER_STATUS_COMPLETE) {
            reportBottomLayout.setVisibility(View.GONE);
            if (pdfReportFile.exists()) { // We have report.
                sendButton.setEnabled(true);
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, pdfView);
            } else {
                Session.addToSessionLog("No PDF Report file: " + pdfReportFile.toString());
                Toast.makeText(getApplicationContext(), "ERROR: Can not find PDF Report file!", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Check rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ROTATION_FLAG)) {
                // Device is rotated. No need to generate. Just show.
                sendButton.setEnabled(true);
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, pdfView);
                return;
            }
        }

        // Generating...
        if (!Utils.isExternalStorageWritable()) {
            Session.addToSessionLog("Can not write report file to external storage!");
            Toast.makeText(getApplicationContext(), "ERROR: Can not write report file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pdfReportFile.exists()) { // Deleting old Report if exist
            pdfReportFile.delete();
        }

        pdfReportPreviewFile = Utils.getPDFReportFileName(Session.getCurrentOrder(), true);
        if (!pdfReportPreviewFile.exists()) {
            Session.addToSessionLog("Can not get pdf preview file!");
            Toast.makeText(getApplicationContext(), "ERROR: Can not get PDF preview file!", Toast.LENGTH_SHORT).show();
            return;
        }

        getSupportLoaderManager().initLoader(PDF_LOADER_ID, null, this);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (id == PDF_LOADER_ID)
            return new GeneratePDFReportFile(this, pdfReportFile, pdfReportPreviewFile, engineerName, clientName);
        if (id == MAIL_LOADER_ID)
            return new MailHelper.SendMail(this, mailHelper);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        switch (loader.getId()) {
            case PDF_LOADER_ID:
                sendButton.setEnabled(true);
                Utils.showPDFReport(getApplicationContext(), pdfReportFile, pdfView);
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
        private final String engineerName;
        private final String clientName;

        public GeneratePDFReportFile(final Context context,
                                     final File pdfReportFile,
                                     final File pdfReportPreviewFile,
                                     final String engineerName,
                                     final String clientName) {
            super(context);
            this.pdfReportFile = pdfReportFile;
            this.pdfReportPreviewFile = pdfReportPreviewFile;
            this.engineerName = engineerName;
            this.clientName = clientName;
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
                pdfStamper.setEncryption(null,
                        BuildConfig.PDF_PASSWORD.getBytes(),
                        PdfWriter.ALLOW_PRINTING,
                        PdfWriter.STANDARD_ENCRYPTION_128);
                final Font font = new Font(Font.HELVETICA, 11, Font.NORMAL);
                final PdfContentByte contentByte = pdfStamper.getOverContent(pdfPageCount);
                final ColumnText columnText = new ColumnText(contentByte);

                Phrase orderText7 = new Phrase(engineerName, font);
                int x = 355;
                int y = 58;
                columnText.setSimpleColumn(orderText7, x, y, x + 150, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText.go();

                Phrase orderText8 = new Phrase(clientName, font);
                x = 120;
                y = 58;
                columnText.setSimpleColumn(orderText8, x, y, x + 150, y + 25, 21.8f, Element.ALIGN_LEFT);
                columnText.go();

                Image signatureEngineer = Image.getInstance(Session.getByteArrayEngineerSignature());
                signatureEngineer.setAbsolutePosition(325f, 80f);
                signatureEngineer.scaleAbsolute(192, 74);
                contentByte.addImage(signatureEngineer);

                Image signatureClient = Image.getInstance(Session.getByteArrayClientSignature());
                signatureClient.setAbsolutePosition(102f, 80f);
                signatureClient.scaleAbsolute(192, 74);
                contentByte.addImage(signatureClient);

                pdfStamper.close();
            } catch (FileNotFoundException e) {
                Session.addToSessionLog("ERROR: 1" + e.toString());
            } catch (IOException e) {
                Session.addToSessionLog("ERROR: 2" + e.toString());
            } catch (DocumentException e) {
                Session.addToSessionLog("ERROR: 3" + e.toString());
            }

            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ROTATION_FLAG, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (enterEmailDialog != null && enterEmailDialog.isShowing()) enterEmailDialog.dismiss();
    }

    @OnClick(R.id.pdf_button_complete_order)
    public void onDoneClick(View v) {
        DbUtils.setOrderMaintenanceTime(Session.getCurrentOrder(), ORDER_MAINTENANCE_END_TIME, new Date());
        DbUtils.setOrderStatus(Session.getCurrentOrder(), ORDER_STATUS_COMPLETE);
        if (pdfReportPreviewFile != null) pdfReportPreviewFile.delete();

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

        enterEmailDialog = dialogBuilder.create();
        enterEmailDialog.show();
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



