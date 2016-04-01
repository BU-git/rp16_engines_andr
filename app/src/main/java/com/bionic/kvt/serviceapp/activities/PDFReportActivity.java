package com.bionic.kvt.serviceapp.activities;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PDFReportActivity extends AppCompatActivity {
    private File publicDocumentsStorageDir;
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreport);

        if (!isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(), "Can not write file to external storage!", Toast.LENGTH_SHORT).show();
            return;
        }

        publicDocumentsStorageDir = getPublicDocumentsStorageDir("KVT_Reports");

        if (!publicDocumentsStorageDir.exists()) {
            Toast.makeText(getApplicationContext(), "Can not create directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderNumber = extras.getString("order_number");
        }

        if (orderNumber == null) {
            Toast.makeText(getApplicationContext(), "No order number to create PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

//        File pdfFile = new File(publicDocumentsStorageDir, "Report_" + orderNumber + ".pdf");

        new Thread(new GeneratePDFReportFile()).start();

    }


    private class GeneratePDFReportFile implements Runnable {
        @Override
        public void run() {
            PrintAttributes printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                    setResolution(new PrintAttributes.Resolution("300DPI", PRINT_SERVICE, 300, 300)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();
            PdfDocument pdfDocument = new PrintedPdfDocument(getApplicationContext(), printAttrs);

            // crate a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 300, 1).create();

            // create a new page from the PageInfo
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // repaint the user's text into the page
            View content = findViewById(R.id.pdf_report);
            content.draw(page.getCanvas());

            // do final processing of the page
            pdfDocument.finishPage(page);

            // Here you could add more pages in a longer doc app, but you'd have
            // to handle page-breaking yourself in e.g., write your own word processor...


            File pdfFile = new File(publicDocumentsStorageDir, "Report_" + orderNumber + ".pdf");


            try {
                OutputStream outputStream = new FileOutputStream(pdfFile);
                pdfDocument.writeTo(outputStream);
                pdfDocument.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error generating file", e);
            }

        }

    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

//    public File getPrivateDocumentsStorageDir(Context context, String pdfFolder) {
//        File file = new File(context.getExternalFilesDir(
//                Environment.DIRECTORY_DOCUMENTS), pdfFolder);
//        if (!file.mkdirs()) {
//            Log.e(getLocalClassName(), "Directory not created: " + file.toString());
//        }
//        return file;
//    }

    public File getPublicDocumentsStorageDir(String pdfFolder) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), pdfFolder);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir;
    }


}


