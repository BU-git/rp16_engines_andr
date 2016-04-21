package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.db.Order;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_CLIENT;
import static com.bionic.kvt.serviceapp.GlobalConstants.SIGNATURE_FILE_ENGINEER;

public class PDFReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Void> {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File pdfFile;
    private File pdfTemplate;

    @Bind(R.id.pdf_report_send_button)
    Button sendButton;

    @Bind(R.id.pdf_report_header)
    TextView pdfReportHeaderTextView;

    @Bind(R.id.pdf_text_status)
    TextView pdfTextLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report);
        ButterKnife.bind(this);

        // Exit if Session is empty
        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

//        File publicDocumentsStorageDir = Utils.getPublicDirectoryStorageDir(Environment.DIRECTORY_DOCUMENTS, "KVTReports");
//        if (!publicDocumentsStorageDir.exists()) {
//            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        final long orderNumber = Session.getCurrentOrder();

        String pdfReportHeader = getResources().getString(R.string.pdf_report) + orderNumber;
        pdfReportHeaderTextView.setText(pdfReportHeader);

        String pdfReportFileName = getResources().getString(R.string.generating_pdf_document)
                + PDF_REPORT_FILE_NAME + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFileName);


        pdfTemplate = Utils.getPDFTemplateFile(getApplicationContext());
        //TODO CHECK NULL
        pdfFile = new File(Utils.getCurrentOrderFolder(getApplicationContext()), PDF_REPORT_FILE_NAME + orderNumber + ".pdf");

        getSupportLoaderManager().initLoader(1, null, this);

//EMAIL
//        private void emailNote()
//        {
//            Intent email = new Intent(Intent.ACTION_SEND);
//            email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());
//            email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
//            Uri uri = Uri.parse(myFile.getAbsolutePath());
//            email.putExtra(Intent.EXTRA_STREAM, uri);
//            email.setType("message/rfc822");
//            startActivity(email);
//        }
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new GeneratePDFReportFile(this, pdfFile, pdfTemplate);
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        sendButton.setEnabled(true);
        showPDFReport(pdfFile);
    }

    @OnClick(R.id.pdf_button_done)
    public void onDoneClick(View v) {
        Intent intent = new Intent(getApplicationContext(), OrderPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
        // NOOP
    }

    public static class GeneratePDFReportFile extends AsyncTaskLoader<Void> {
        private int pdfPageCount = 1;
        private final File pdfFile;
        private final File pdfTemplate;
        private final Context context;

        public GeneratePDFReportFile(Context context, File pdfFile, File pdfTemplate) {
            super(context);
            this.context = context;
            this.pdfFile = pdfFile;
            this.pdfTemplate = pdfTemplate;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public Void loadInBackground() {
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

            String pdfDate = simpleDateFormat.format(currentOrder.getDate()) + "\n";
            String pdfReference = currentOrder.getReference() + "\n";
            String pdfInstallation = currentOrder.getInstallation().getName() + "\n";
            String pdfInstallationAddress = currentOrder.getInstallation().getAddress() + "\n";
            String pdfInstallationTown = currentOrder.getInstallation().getTown() + "\n";
            String pdfWorkingHours = "?????????????";


            String pdfTask = currentOrder.getTasks().first().getLtxa1();

            realm.close();


            try {
                final PdfReader pdfReader = new PdfReader(pdfTemplate.toString());
                final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfFile));
                final Font font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
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

                int x = 160;
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
                x = 415;
                y = 505;
                columnText.setSimpleColumn(orderText, x, y, x + 150, y + 150, 22, Element.ALIGN_LEFT);
                columnText.go();


                orderText = new Phrase(pdfTask, font);
                x = 150;
                y = 483;
                columnText.setSimpleColumn(orderText, x, y, x + 400, y + 25, 22, Element.ALIGN_LEFT);
                columnText.go();

                String signatureFileName = SIGNATURE_FILE_ENGINEER;
                String signaturePath = new File(Utils.getCurrentOrderFolder(context), signatureFileName).toString();
                Image signatureEngineer = Image.getInstance(signaturePath);
                signatureEngineer.setAbsolutePosition(320f, 135f);
                signatureEngineer.scalePercent(16f);
                contentByte.addImage(signatureEngineer);

                signatureFileName = SIGNATURE_FILE_CLIENT;
                signaturePath = new File(Utils.getCurrentOrderFolder(context), signatureFileName).toString();
                Image signatureClient = Image.getInstance(signaturePath);
                signatureClient.setAbsolutePosition(95f, 135f);
                signatureClient.scalePercent(16f);
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
            }

            return null;
        }

    }

    private void showPDFReport(File pdfReport) {
        ParcelFileDescriptor mFileDescriptor = null;
        try {
            mFileDescriptor = ParcelFileDescriptor.open(pdfReport, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("ERROR: PDF file not found: " + e.toString());
        }
        PdfRenderer mPdfRenderer = null;
        try {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);

        int pageHeight = (int) (mCurrentPage.getHeight() * 2);
//        int pageHeight = 3508;
        int pageWidth = (int) (mCurrentPage.getWidth() * 2);
//        int pageWidth = 2481;

        if (BuildConfig.IS_LOGGING_ON)
            Session.addToSessionLog("PDF page size on viewing: " + pageHeight + "/" + pageWidth);

        Bitmap bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);

        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        ImageView pdfView = (ImageView) findViewById(R.id.pdf_bitmap);
        pdfView.setImageBitmap(bitmap);

        mCurrentPage.close();
        mPdfRenderer.close();
        try {
            mFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
            mFileDescriptor = null;
        }


    }

}


