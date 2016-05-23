package com.bionic.kvt.serviceapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.api.User;
import com.bionic.kvt.serviceapp.db.BackgroundService;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.Order;
import com.google.gson.JsonElement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.bionic.kvt.serviceapp.GlobalConstants.LMRA_PHOTO_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_REPORT_PREVIEW_FILE_NAME;
import static com.bionic.kvt.serviceapp.GlobalConstants.PDF_TEMPLATE_FILENAME_EN;
import static com.bionic.kvt.serviceapp.GlobalConstants.UPDATE_SERVICE_MSG;

public class Utils {
    public static final int REQUEST_WRITE_CODE = 1;
    private final static SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
    private final static SimpleDateFormat dateAndTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMANY);
    private final static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
    private static final String TAG = Utils.class.getName();

    public static String nullStringToEmpty(@Nullable final String inString) {
        return (inString == null) ? "" : inString.trim();
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 1;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void requestWritePermissionsIfNeeded(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Utils.REQUEST_WRITE_CODE);
        }
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

    public static void deleteRecursive(final File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Nullable
    public static File getOrderDir(final long orderNumber) {
        final File dir = new File(Session.getCurrentAppDir(), "" + orderNumber);

        if (dir.exists() || dir.mkdirs()) {
            return dir;
        }
        AppLog.serviceE(true, orderNumber, "Problem with creating order dir: " + dir.toString());
        return null; //Directory is not exist and fail to create
    }

    @Nullable
    public static File getAppExternalPrivateDir() {
        final File currentAppExternalPrivateDir = Session.getAppExternalPrivateDir();
        if (currentAppExternalPrivateDir == null) return null;

        if (currentAppExternalPrivateDir.exists() || currentAppExternalPrivateDir.mkdirs()) {
            return currentAppExternalPrivateDir;
        }

        AppLog.serviceE(true, -1, "Problem with creating dir: " + currentAppExternalPrivateDir.toString());
        return null; //Directory is not exist and fail to create
    }

    //TODO IMPLEMENT LANGUAGE SUPPORT
    @Nullable
    public static File getPDFTemplateFile(final Context context) {
        final File pdfTemplate = new File(Session.getCurrentAppDir(), PDF_TEMPLATE_FILENAME_EN);

        if (pdfTemplate.exists()) return pdfTemplate;

        try (InputStream inputStream = context.getAssets().open(PDF_TEMPLATE_FILENAME_EN);
             FileOutputStream outputStream = new FileOutputStream(pdfTemplate)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (IOException e) {
            AppLog.serviceE(true, -1, "Error getting PDF template from assets: " + e.toString());
            return null;
        }
        return pdfTemplate;
    }

    public static File getPDFReportFileName(final long orderNumber, final boolean preview) {
        if (preview) {
            return new File(getOrderDir(orderNumber), PDF_REPORT_PREVIEW_FILE_NAME + orderNumber + ".pdf");
        } else {
            return new File(getOrderDir(orderNumber), PDF_REPORT_FILE_NAME + orderNumber + ".pdf");
        }
    }

    public static void runBackgroundServiceIntent(final Context context, @GlobalConstants.ServiceMessage final int intentType) {
        Intent updateService = new Intent(context, BackgroundService.class);
        updateService.putExtra(UPDATE_SERVICE_MSG, intentType);
        context.startService(updateService);
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

    public static void showPDFReport(@NonNull final Activity activity,
                                     @NonNull final File pdfReportFile,
                                     @NonNull final ImageView pdfView) {

        if (!pdfReportFile.exists()) {
            AppLog.E(activity, "PDF report file not found: " + pdfReportFile);
            return;
        }

        final int zoomFactor = 2;

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
            AppLog.E(activity, "PDF file render problem: " + e.toString());
        }
    }

    public static String getFileMD5Sum(final File file) {
        final MessageDigest messageDigestMD5;
        try {
            messageDigestMD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            AppLog.serviceI(e.toString());
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
            AppLog.serviceI(e.toString());
            return "";
        }
    }

    @Nullable
    public static File createImageFile(final long orderNumber) {
        final String imageFileName = LMRA_PHOTO_FILE_NAME + orderNumber + "_";
        final File appExternalPrivateDir = getAppExternalPrivateDir();
        if (appExternalPrivateDir == null) return null;
        try {
            return File.createTempFile(imageFileName, ".jpg", appExternalPrivateDir);
        } catch (IOException e) {
            AppLog.serviceE(true, orderNumber, "Error on creating LMRA file: " + e.toString());
            return null;
        }
    }

    public static void copyFile(final File srcFile, final File destFile) {
        try (FileChannel inChannel = new FileInputStream(srcFile).getChannel();
             FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        ) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            AppLog.serviceE(true, -1, "Error on copy file: " + e.toString());
        }
    }

    public static boolean zipXMLReportFiles(final String[] XMLFiles, final String zipFile) {
        try (ZipOutputStream zipOutputStream =
                     new ZipOutputStream(
                             new BufferedOutputStream(
                                     new FileOutputStream(zipFile)))) {

            byte data[] = new byte[2048];
            String fileName;
            int count;
            for (String XMLFile : XMLFiles) {
                if (XMLFile == null) continue; // Empty string
                if (!(new File(XMLFile).exists())) continue; // No such file
                try (BufferedInputStream originFileBufferedInputStream =
                             new BufferedInputStream(new FileInputStream(XMLFile), 2048)) {
                    fileName = XMLFile.substring(XMLFile.lastIndexOf("/") + 1);
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOutputStream.putNextEntry(zipEntry);
                    while ((count = originFileBufferedInputStream.read(data, 0, 2048)) != -1)
                        zipOutputStream.write(data, 0, count);
                } catch (IOException e) {
                    AppLog.serviceE(true, -1, "Error saving XML to ZIP report file: " + e.toString());
                    return false;
                }
            }

        } catch (IOException e) {
            AppLog.serviceE(true, -1, "Error saving ZIP report file: " + e.toString());
            return false;
        }
        return true;
    }

    public static void updateOrderStatusOnServer(final long orderNumber) {
        try (final Realm realm = Realm.getDefaultInstance()) {
            final Order order = realm.where(Order.class).equalTo("number", orderNumber).findFirst();

            if (order == null) {
                AppLog.serviceE(true, orderNumber, "Updating order status on server: No such order!");
                return;
            }

            final String email = order.getEmployeeEmail();
            final long lastAndroidChangeDate = order.getLastAndroidChangeDate().getTime();
            final int orderStatus = order.getOrderStatus();

            final Call<ResponseBody> updateOrderRequest =
                    Session.getServiceConnection().updateOrder(orderNumber, email, lastAndroidChangeDate, orderStatus);

            AppLog.serviceI(false, orderNumber, "Updating server order status: " + updateOrderRequest.request());

            updateOrderRequest.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        AppLog.serviceE(true, orderNumber, "Update server order status fail: " + response.code());
                        return;
                    }

                    AppLog.serviceI("Update server order status successful.");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppLog.serviceE(true, orderNumber, "Update server order status fail: " + t.toString());
                }
            });
        }
    }


    // TODO REDESIGN RETURN LOGIC
    public static ServerRequestResult getUserFromServer(final String email) {
        final Call<User> userRequest = Session.getServiceConnection().getUser(email);
        AppLog.serviceI("Connecting to server: " + userRequest.request());

        final Response<User> userResponse;
        try {
            userResponse = userRequest.execute();
        } catch (IOException e) {
            AppLog.serviceE(false, -1, "User request fail: " + e.toString());
            return new ServerRequestResult(false, "User request fail: " + e.toString());
        }

        if (!userResponse.isSuccessful()) { // Request unsuccessful
            AppLog.serviceE(false, -1, "Error connecting to server: " + userResponse.code());
            return new ServerRequestResult(false, "Error connecting to server: " + userResponse.code());
        }

        if (userResponse.body() == null) {
            AppLog.serviceE(false, -1, "Connection successful. Empty response.");
            return new ServerRequestResult(false, "Connection successful. Empty response.");
        }

        if (userResponse.body().getEmail() == null) { // No such user on server
            DbUtils.deleteUser(email); // Deleting if we have local user
            AppLog.serviceI("Connection successful. No user found: " + email);
            return new ServerRequestResult(false, "The entered e-mail address is not known.\nIf you are sure, please call the administrator.");
        }

        // We have this user on server
        DbUtils.updateUserFromServer(userResponse.body());
        AppLog.serviceI("Connection successful. User found: " + email);
        return new ServerRequestResult(true, "Connection successful. User found.");
    }

    // TODO REDESIGN RETURN LOGIC
    public static ServerRequestResult requestPasswordReset(final String email) {
        final String userHash = DbUtils.getUserHash(email);
        if (userHash == null) {
            AppLog.serviceI("Password reset. No such user found!");
            return new ServerRequestResult(false, "No such user found. Please, call administrator.");
        }

        final Call<ResponseBody> resetPasswordRequest = Session.getServiceConnection().passwordReset(email, userHash);
        AppLog.serviceI("Connecting to server: " + resetPasswordRequest.request());

        final Response<ResponseBody> resetPasswordResponse;
        try {
            resetPasswordResponse = resetPasswordRequest.execute();
        } catch (IOException e) {
            AppLog.serviceE(false, -1, "Reset password request fail: " + e.toString());
            return new ServerRequestResult(false, "Reset password request fail: " + e.toString());
        }

        if (!resetPasswordResponse.isSuccessful()) { // Request unsuccessful
            AppLog.serviceE(false, -1, "Error connecting to server: " + resetPasswordResponse.code());
            return new ServerRequestResult(false, "Error connecting to server: " + resetPasswordResponse.code());
        }

        AppLog.serviceI("New password request send. Check email <" + email + "> for new password.");
        return new ServerRequestResult(true, "New password request send. Check email <" + email + "> for new password.");
    }

    public static class ServerRequestResult {
        private boolean isSuccessful;
        private String message;

        public ServerRequestResult(boolean isSuccessful, String message) {
            this.isSuccessful = isSuccessful;
            this.message = message;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public String getMessage() {
            return message;
        }
    }
}