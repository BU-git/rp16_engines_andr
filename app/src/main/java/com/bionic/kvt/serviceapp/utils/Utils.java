package com.bionic.kvt.serviceapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_TEMPLATE_FILENAME_EN;

public class Utils {
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

    @Nullable
    public static File getCurrentOrderDir() {
        File currentDir = Session.getCurrentOrderDir();
        if (currentDir == null) return null;

        if (currentDir.exists() || currentDir.mkdirs()) {
            return Session.getCurrentOrderDir();
        }

        return null; //Directory is not exist and fail to create
    }

    public static String getUserIdFromEmail(@NonNull final String email) {
        return email.substring(0, email.indexOf('@'));
    }

//    public static void cleanCurrentReportFile(final Context context){
//        final File reportFile = new File(Utils.getCurrentOrderFolder(context), signatureFileName);
//        if (signatureFIle.exists()) signatureFIle.delete();
//    }

    public static void cleanSignatureFile(final String signatureFileName) {
        final File signatureFile = new File(getCurrentOrderDir(), signatureFileName);
        if (signatureFile.exists()) signatureFile.delete();
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
            if (BuildConfig.IS_LOGGING_ON)
                Session.addToSessionLog("ERROR getting PDF template from assets: " + e.toString());
            return null;
        }
        return pdfTemplate;
    }

    public static File getPDFReportFileName() {
        return new File(getCurrentOrderDir(), PDF_REPORT_FILE_NAME + Session.getCurrentOrder() + ".pdf");
    }

    public static int getSetIndex(Set<Map.Entry<String,JsonElement>> set, Map.Entry<String,JsonElement> value) {
        int result = 0;
        for (Object entry:set) {
            if (entry.equals(value)) return result;
            result++;
        }
        return -1;
    }
}