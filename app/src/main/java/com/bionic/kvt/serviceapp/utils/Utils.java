package com.bionic.kvt.serviceapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.Session;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_PREVIEW_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_TEMPLATE_FILENAME_EN;

public class Utils {
    private final static SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
    private final static SimpleDateFormat dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
    private final static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);

    public static final int REQUEST_WRITE_CODE = 1;

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    public static boolean isStoragePermissionGranted(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean isRequestWritePermissionNeeded(final Context context,
                                                         final AppCompatActivity activity) {
        if (!Utils.isStoragePermissionGranted(context)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Utils.REQUEST_WRITE_CODE);
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isNetworkConnected(final Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static String getDateStringFromDate(final Date date) {
        return dateOnly.format(date);
    }

    public static String getDateTimeStringFromDate(final Date date) {
        return dateAndTime.format(date);
    }

    public static String getTimeStringFromDate(final Date date) {
        return time.format(date);
    }

    @Nullable
    public static File getCurrentOrderDir() {
        File currentDir = Session.getCurrentOrderDir();
        if (currentDir == null) return null;

        if (currentDir.exists() || currentDir.mkdirs()) {
            return Session.getCurrentOrderDir();
        }

        return null; //Directory is not exist and fail to create
    }

    //TODO IMPLEMENT LANGUAGE SUPPORT
    @Nullable
    public static File getPDFTemplateFile(final Context context) {
        final File pdfTemplate = new File(Session.getCurrentAppExternalPrivateDir(), PDF_TEMPLATE_FILENAME_EN);

        if (pdfTemplate.exists()) return pdfTemplate;

        try (InputStream inputStream = context.getAssets().open(PDF_TEMPLATE_FILENAME_EN);
             FileOutputStream outputStream = new FileOutputStream(pdfTemplate)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (IOException e) {
            Session.addToSessionLog("ERROR getting PDF template from assets: " + e.toString());
            return null;
        }
        return pdfTemplate;
    }

    public static File getPDFReportFileName(final long orderNumber, final boolean preview) {
        if (preview) {
            return new File(getCurrentOrderDir(), PDF_REPORT_PREVIEW_FILE_NAME + orderNumber + ".pdf");
        } else {
            return new File(getCurrentOrderDir(), PDF_REPORT_FILE_NAME + orderNumber + ".pdf");
        }
    }

    public static int getSetIndex(Set<Map.Entry<String, JsonElement>> set, Map.Entry<String, JsonElement> value) {
        int result = 0;
        for (Object entry : set) {
            if (entry.equals(value)) return result;
            result++;
        }
        return -1;
    }

    public static String convertByteArrayToHexString(@NonNull byte[] arrayBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuilder.append(Integer.toString((arrayByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuilder.toString();
    }

    public static void showPDFReport(@NonNull final Context context,
                                     @NonNull final File pdfReportFile,
                                     @NonNull final ImageView pdfView) {

        if (!pdfReportFile.exists()) {
            Toast.makeText(context, "ERROR: PDF report file not found!", Toast.LENGTH_SHORT).show();
            Session.addToSessionLog("ERROR: PDF report file not found: " + pdfReportFile);
            return;
        }

        final int zoomFactor = 3;

        try (ParcelFileDescriptor mFileDescriptor =
                     ParcelFileDescriptor.open(pdfReportFile, ParcelFileDescriptor.MODE_READ_ONLY)) {
            final PdfRenderer mPdfRenderer = new PdfRenderer(mFileDescriptor);
            final PdfRenderer.Page mCurrentPage = mPdfRenderer.openPage(0);

            final int pageHeight = mCurrentPage.getHeight() * zoomFactor;
            final int pageWidth = mCurrentPage.getWidth() * zoomFactor;

            final Bitmap bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
            pdfView.setImageBitmap(bitmap);

            new PhotoViewAttacher(pdfView);
            mCurrentPage.close();
            mPdfRenderer.close();
        } catch (IOException e) {
            Toast.makeText(context, "Some error during PDF file open", Toast.LENGTH_SHORT).show();
            Session.addToSessionLog("ERROR: PDF file render problem: " + e.toString());
        }
    }

    public static String getFileMD5Sum(final File file) {
        final MessageDigest messageDigestMD5;
        try {
            messageDigestMD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        byte[] buffer = new byte[8192];
        int read;
        try (InputStream is = new FileInputStream(file)) {
            while ((read = is.read(buffer)) > 0) {
                messageDigestMD5.update(buffer, 0, read);
            }

            return convertByteArrayToHexString(messageDigestMD5.digest());
        } catch (IOException e) {
            return "";
        }
    }

}