package com.bionic.kvt.serviceapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.bionic.kvt.serviceapp.BuildConfig;
import com.bionic.kvt.serviceapp.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class Utils {
    public static final int REQUEST_WRITE_CODE = 1;

    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean needRequestWritePermission(Context context, AppCompatActivity activity) {
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

//    public static File getPublicDirectoryStorageDir(String directory, String folder) {
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(directory), folder);
//        if (!storageDir.exists()) {
//            storageDir.mkdirs();
//        }
//        return storageDir;
//    }

    // Return Private Path to folder BuildConfig.ORDERS_FOLDER
    public static File getPrivateDocumentsStorageDir(Context context, String folder) {
        File fileDir = new File(context.getExternalFilesDir(BuildConfig.ORDERS_FOLDER), folder);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }


    public static File getCurrentOrderFolder(Context context) {
        return getPrivateDocumentsStorageDir(context, "" + Session.getCurrentOrder());
    }

    public static String getUserIdFromEmail(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static File getPDFTemplateFile(Context context) {
        File pdfTemplate = new File(context.getExternalFilesDir(""), "pdfTemplate_en.pdf");

        if (pdfTemplate.exists()) return pdfTemplate;

        try (InputStream inputStream = context.getAssets().open("pdfTemplate_en.pdf");
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
}
