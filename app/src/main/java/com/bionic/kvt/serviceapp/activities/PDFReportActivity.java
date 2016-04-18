package com.bionic.kvt.serviceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
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
import com.bionic.kvt.serviceapp.utils.Utils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PDFReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Void> {
    private File pdfFile;

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

        if (!Utils.isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

//        File publicDocumentsStorageDir = Utils.getPublicDirectoryStorageDir(Environment.DIRECTORY_DOCUMENTS, "KVTReports");
//        if (!publicDocumentsStorageDir.exists()) {
//            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (Session.getCurrentOrder() == 0L) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        final long orderNumber = Session.getCurrentOrder();
//        pdfFile = new File(publicDocumentsStorageDir, "Report_" + orderNumber + ".pdf");

        pdfFile = new File(Utils.getCurrentOrderFolder(getApplicationContext()), "Report_" + orderNumber + ".pdf");


        String pdfReportHeader = getResources().getString(R.string.pdf_report) + orderNumber;
        pdfReportHeaderTextView.setText(pdfReportHeader);

        String pdfReportFullPath = getResources().getString(R.string.generating_pdf_document)
                + " Report_" + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFullPath);

        getSupportLoaderManager().initLoader(1, null, this);

        File pdfTemlate = new File(getApplicationContext().getExternalFilesDir(""), "PDFBon.pdf");

//        Utils.copyFile(pdfTemlate, pdfFile);

        try  {
            PdfReader pdfReader = new PdfReader(pdfTemlate.toString());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pdfFile));

            PdfContentByte cb = pdfStamper.getOverContent(1);
            ColumnText ct = new ColumnText(cb);
            ct.setSimpleColumn(120f, 48f, 200f, 600f);
            Font f = new Font();
            Paragraph pz = new Paragraph(new Phrase(20, "Hello World!", f));
            ct.addElement(pz);
            ct.go();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "Cp1252", BaseFont.EMBEDDED);
            f = new Font(bf, 13);
            ct = new ColumnText(cb);
            ct.setSimpleColumn(120f, 48f, 200f, 700f);
            pz = new Paragraph ("Hello World!", f);
            ct.addElement(pz);
            ct.go();

            pdfStamper.close();
            pdfReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


//        try (OutputStream output = new FileOutputStream(pdfFile)) {
//            Document document = new Document(PageSize.A4);
//            PdfWriter writer = PdfWriter.getInstance(document, output);
//            document.open();
//            PdfReader reader = new PdfReader(pdfTemlate.toString());
//            writer.getImportedPage(reader, 1);
////            document.add(new Rectangle(100, 100, 1000, 1000));
//            document.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new GeneratePDFReportFile(this, pdfFile);
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
        private final Context context;

        public GeneratePDFReportFile(Context context, File pdfFile) {
            super(context);
            this.context = context;
            this.pdfFile = pdfFile;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public Void loadInBackground() {
//            PrintAttributes printAttrs = new PrintAttributes.Builder().
//                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
//                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
//                    setResolution(new PrintAttributes.Resolution("300 DPI", PRINT_SERVICE, 300, 300)).
//                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
//                    build();
//
//            PdfDocument orderPdfDocument = new PrintedPdfDocument(context, printAttrs);
//
//            int pageHeight = printAttrs.getMediaSize().getHeightMils() / 1000 * 72;
//            int pageWidth = printAttrs.getMediaSize().getWidthMils() / 1000 * 72;
//
//            if (BuildConfig.IS_LOGGING_ON)
//                Session.addToSessionLog("PDF page size on creating: " + pageHeight + "/" + pageWidth);
//
//            PdfDocument.PageInfo newPage =
//                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfPageCount).create();
//
//            PdfDocument.Page page = orderPdfDocument.startPage(newPage);
//            drawPDFPage(page);
//            orderPdfDocument.finishPage(page);
//
//            try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
//                orderPdfDocument.writeTo(fileOutputStream);
//                orderPdfDocument.close();
//                fileOutputStream.flush();
//                fileOutputStream.close();
//                if (BuildConfig.IS_LOGGING_ON)
//                    Session.addToSessionLog("PDF file saved: " + pdfFile);
//            } catch (IOException e) {
//                if (BuildConfig.IS_LOGGING_ON)
//                    Session.addToSessionLog("ERROR writing: " + pdfFile + e.toString());
//            }
            return null;
        }

        private void drawPDFPage(PdfDocument.Page page) {
            Canvas canvas = page.getCanvas();

            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText("Test Print Document Page " + pdfPageCount, leftMargin, titleBaseLine, paint);

            paint.setTextSize(14);
            canvas.drawText("klvhkjhv", leftMargin, titleBaseLine + 35, paint);
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("PDF page size on canvas: " + canvas.getHeight() + "/" + canvas.getWidth());
            paint.setColor(Color.RED);
            PdfDocument.PageInfo pageInfo = page.getInfo();
            canvas.drawCircle(pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() / 2, 150, paint);
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


