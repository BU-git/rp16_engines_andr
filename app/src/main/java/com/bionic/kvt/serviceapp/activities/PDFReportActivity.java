package com.bionic.kvt.serviceapp.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFReportActivity extends AppCompatActivity {
    File pdfFile;
    Button sendButton;
    TextView pdfTextLog;

    public boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public File getPublicDocumentsStorageDir(String pdfFolder) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), pdfFolder);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_report);

        if (!isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

        File publicDocumentsStorageDir = getPublicDocumentsStorageDir("KVTReports");
        if (!publicDocumentsStorageDir.exists()) {
            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderNumber = ((Session) getApplication()).getOrderNumber();
        if (orderNumber == null) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        pdfFile = new File(publicDocumentsStorageDir, "Report_" + orderNumber + ".pdf");

        String pdfReportHeader = getResources().getString(R.string.pdf_report) + orderNumber;
        ((TextView) findViewById(R.id.pdf_report_header)).setText(pdfReportHeader);

        pdfTextLog = ((TextView) findViewById(R.id.pdf_text_log));
        String pdfReportFullPath = getResources().getString(R.string.generating_pdf_document) +
                "\n" + pdfFile.toString();
        pdfTextLog.setText(pdfReportFullPath);

        sendButton = (Button) findViewById(R.id.pdf_report_send_button);

        new GeneratePDFReportFile().execute();
    }

    private class GeneratePDFReportFile extends AsyncTask<Void, Void, Void> {
        private int pdfPageCount = 1;

        @Override
        protected Void doInBackground(Void... urls) {
            PrintAttributes printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                    setResolution(new PrintAttributes.Resolution("300DPI", PRINT_SERVICE, 300, 300)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();

            PdfDocument orderPdfDocument = new PrintedPdfDocument(getApplicationContext(), printAttrs);

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
            this.publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            sendButton.setEnabled(true);
            String logText = pdfTextLog.getText().toString() + "\nDone!";
            pdfTextLog.setText(logText);
            showPDFReport(pdfFile);
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
            canvas.drawText("Demm PDF document!", leftMargin, titleBaseLine + 35, paint);

            paint.setColor(Color.RED);
            PdfDocument.PageInfo pageInfo = page.getInfo();
            canvas.drawCircle(pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() / 2, 150, paint);
        }
    }

    private void showPDFReport(File pdfReport) {
        String logText = pdfTextLog.getText().toString() + "\nOpen file for preview.";
        pdfTextLog.setText(logText);

        if (pdfReport.exists()) {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(pdfReport), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open PDF Report file");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No app found
            }
        } else
            Toast.makeText(getApplicationContext(), "File path is incorrect.", Toast.LENGTH_LONG).show();


//        ParcelFileDescriptor mFileDescriptor = null;
//        try {
//            mFileDescriptor = ParcelFileDescriptor.open(pdfReport, ParcelFileDescriptor.MODE_READ_ONLY);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        PdfRenderer mPdfRenderer = null;
//        try {
//            mPdfRenderer = new PdfRenderer(mFileDescriptor);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(1);
//        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);
//
//        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//        ImageView pdfView = (ImageView) findViewById(R.id.pdf_bitmap);
//        pdfView.setImageBitmap(bitmap);
//
//        mCurrentPage.close();
//        mPdfRenderer.close();
//        try {
//            mFileDescriptor.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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


