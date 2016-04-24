package com.bionic.kvt.serviceapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.Order;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

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
    private File pdfTemplate;
    private int zoomFactor = 2;

    @Bind(R.id.pdf_report_send_button)
    Button sendButton;

    @Bind(R.id.pdf_report_header)
    TextView pdfReportHeaderTextView;

    @Bind(R.id.pdf_text_status)
    TextView pdfTextLog;

    @Bind(R.id.zoomControls)
    ZoomControls zoomControls;

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

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomFactor == 4) return;
                zoomFactor++;
                showPDFReport(pdfReportFile);
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomFactor == 1) return;
                zoomFactor--;
                showPDFReport(pdfReportFile);
            }
        });


        String pdfReportHeader = getText(R.string.pdf_report).toString() + orderNumber;
        pdfReportHeaderTextView.setText(pdfReportHeader);

        String pdfReportFileName = getText(R.string.generating_pdf_document).toString()
                + " " + PDF_REPORT_FILE_NAME + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFileName);

        pdfReportFile = Utils.getPDFReportFileName();
        if (pdfReportFile.exists()) { // We have report.
            showPDFReport(pdfReportFile);
        } else { // No report. Generating...

            if (!Utils.isExternalStorageWritable()) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Can not write report file to external storage!");
                Toast.makeText(getApplicationContext(),
                        "ERROR: Can not write report file to external storage!", Toast.LENGTH_SHORT).show();
                return;
            }

            pdfTemplate = Utils.getPDFTemplateFile(getApplicationContext());
            if (pdfTemplate == null || !pdfTemplate.exists()) {
                if (BuildConfig.IS_LOGGING_ON)
                    Session.addToSessionLog("Can not get pdf template!");
                Toast.makeText(getApplicationContext(),
                        "ERROR: Can not get pdf template!", Toast.LENGTH_SHORT).show();
                return;
            }

            getSupportLoaderManager().initLoader(PDF_LOADER_ID, null, this);

        }
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (id == PDF_LOADER_ID)
            return new GeneratePDFReportFile(this, pdfReportFile, pdfTemplate);
        if (id == MAIL_LOADER_ID)
            return new MailHelper.SendMail(this, mailHelper);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        switch (loader.getId()) {
            case PDF_LOADER_ID:
                showPDFReport(pdfReportFile);
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
        private int pdfPageCount = 1;
        private final File pdfReportFile;
        private final File pdfTemplate;

        public GeneratePDFReportFile(Context context, File pdfReportFile, File pdfTemplate) {
            super(context);
            this.pdfReportFile = pdfReportFile;
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
            String pdfRelation = currentOrder.getRelation().getName() + "\n";
            String pdfRelationTown = currentOrder.getRelation().getTown() + "\n";
            String pdfPerson = currentOrder.getRelation().getContactPerson() + "\n";
            String pdfRelationTelephone = currentOrder.getRelation().getTelephone() + "\n";
            String pdfEmployee = currentOrder.getEmployee().getName();

            String pdfDate = Utils.getDateStringFromDate(currentOrder.getDate()) + "\n";
            String pdfReference = currentOrder.getReference() + "\n";
            String pdfInstallation = currentOrder.getInstallation().getName() + "\n";
            String pdfInstallationAddress = currentOrder.getInstallation().getAddress() + "\n";
            String pdfInstallationTown = currentOrder.getInstallation().getTown() + "\n";
            String pdfWorkingHours = "?????????????";

            String pdfTask = currentOrder.getTasks().first().getLtxa1();

            realm.close();

            try {
                final PdfReader pdfReader = new PdfReader(pdfTemplate.toString());
                final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfReportFile));
                final Font font = new Font(Font.HELVETICA, 11, Font.NORMAL);
                final PdfContentByte contentByte = pdfStamper.getOverContent(pdfPageCount);
                final ColumnText columnText = new ColumnText(contentByte);

                Phrase orderText = new Phrase(pdfOrderNumber +
                        pdfRelation +
                        pdfRelationTown +
                        pdfPerson +
                        pdfRelationTelephone +
                        pdfEmployee,
                        font
                );

                int x = 130;
                int y = 505;
                columnText.setSimpleColumn(orderText, x, y, x + 180, y + 150, 22, Element.ALIGN_LEFT);
                columnText.go();

                orderText = new Phrase(pdfDate +
                        pdfReference +
                        pdfInstallation +
                        pdfInstallationAddress +
                        pdfInstallationTown +
                        pdfWorkingHours,
                        font
                );
                x = 420;
                y = 505;
                columnText.setSimpleColumn(orderText, x, y, x + 150, y + 150, 22, Element.ALIGN_LEFT);
                columnText.go();


                orderText = new Phrase(pdfTask, font);
                x = 130;
                y = 483;
                columnText.setSimpleColumn(orderText, x, y, x + 400, y + 25, 22, Element.ALIGN_LEFT);
                columnText.go();

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

                orderText = new Phrase(pdfEmployee, font);
                x = 355;
                y = 113;
                columnText.setSimpleColumn(orderText, x, y, x + 150, y + 25, 22, Element.ALIGN_LEFT);
                columnText.go();

                orderText = new Phrase(pdfPerson, font);
                x = 120;
                y = 113;
                columnText.setSimpleColumn(orderText, x, y, x + 150, y + 25, 22, Element.ALIGN_LEFT);
                columnText.go();


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

    private void showPDFReport(@NonNull final File pdfReport) {
        if (!pdfReport.exists()) {
            Toast.makeText(getApplicationContext(), "ERROR: PDF report file not found!", Toast.LENGTH_SHORT).show();
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("ERROR: PDF report file not found: " + pdfReport);
            return;
        }

        sendButton.setEnabled(true);

        try {
            ParcelFileDescriptor mFileDescriptor = ParcelFileDescriptor.open(pdfReport, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer mPdfRenderer = new PdfRenderer(mFileDescriptor);
            PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);

            int pageHeight = mCurrentPage.getHeight() * zoomFactor;
            int pageWidth = mCurrentPage.getWidth() * zoomFactor;

            Bitmap bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            ImageView pdfView = (ImageView) findViewById(R.id.pdf_bitmap);
            pdfView.setImageBitmap(bitmap);

            mCurrentPage.close();
            mPdfRenderer.close();
            mFileDescriptor.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Some error during PDF file open", Toast.LENGTH_SHORT).show();
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("ERROR: PDF file render problem: " + e.toString());
        }
    }

    @OnClick(R.id.pdf_button_done)
    public void onDoneClick(View v) {
        DbUtils.setOrderMaintenanceTime(Session.getCurrentOrder(), ORDER_MAINTENANCE_END_TIME, new Date());
        DbUtils.setOrderStatus(Session.getCurrentOrder(), ORDER_STATUS_COMPLETE);

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



