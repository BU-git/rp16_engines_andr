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
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PDFReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Void> {
    File pdfFile;

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

        File publicDocumentsStorageDir = Utils.getPublicDirectoryStorageDir(Environment.DIRECTORY_DOCUMENTS, "KVTReports");
        if (!publicDocumentsStorageDir.exists()) {
            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Session.getCurrentOrder() == null) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        final long orderNumber = Session.getCurrentOrder().getNumber();
        pdfFile = new File(publicDocumentsStorageDir, "Report_" + orderNumber + ".pdf");

        String pdfReportHeader = getResources().getString(R.string.pdf_report) + orderNumber;
        pdfReportHeaderTextView.setText(pdfReportHeader);

        String pdfReportFullPath = getResources().getString(R.string.generating_pdf_document)
                + " Report_" + orderNumber + ".pdf";
        pdfTextLog.setText(pdfReportFullPath);

        getSupportLoaderManager().initLoader(1, null, this);
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
        private File pdfFile;
        private Context context;

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
            PrintAttributes printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                    setResolution(new PrintAttributes.Resolution("300DPI", PRINT_SERVICE, 300, 300)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();

            PdfDocument orderPdfDocument = new PrintedPdfDocument(context, printAttrs);

            int pageHeight = printAttrs.getMediaSize().getHeightMils() / 1000 * 72;
            int pageWidth = printAttrs.getMediaSize().getWidthMils() / 1000 * 72;

            PdfDocument.PageInfo newPage =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfPageCount).create();

            PdfDocument.Page page = orderPdfDocument.startPage(newPage);
            drawPDFPage(page);
            orderPdfDocument.finishPage(page);

            try {
                orderPdfDocument.writeTo(new FileOutputStream(pdfFile));
            } catch (IOException e) {
                throw new RuntimeException("Error generating file", e);
            } finally {
                orderPdfDocument.close();
            }
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
            canvas.drawText("" + PrintAttributes.MediaSize.ISO_A4.getWidthMils() / 1000 * 72 * 2 + "/"
                    + PrintAttributes.MediaSize.ISO_A4.getHeightMils() / 1000 * 72 * 2, leftMargin, titleBaseLine + 35, paint);

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
            e.printStackTrace();
        }
        PdfRenderer mPdfRenderer = null;
        try {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth() * 2, mCurrentPage.getHeight() * 2, Bitmap.Config.ARGB_8888);

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


//    public File getPrivateDocumentsStorageDir(Context context, String pdfFolder) {
//        File file = new File(context.getExternalFilesDir(
//                Environment.DIRECTORY_DOCUMENTS), pdfFolder);
//        if (!file.mkdirs()) {
//            Log.e(getLocalClassName(), "Directory not created: " + file.toString());
//        }
//        return file;
//    }

}


