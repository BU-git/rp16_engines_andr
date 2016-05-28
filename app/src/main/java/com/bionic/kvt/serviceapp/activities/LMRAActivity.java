package com.bionic.kvt.serviceapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.adapters.LMRAAdapter;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.LMRAItem;
import com.bionic.kvt.serviceapp.dialogs.LMRADialog;
import com.bionic.kvt.serviceapp.models.LMRAModel;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.AppLogItem;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * An activity for register dangerous situations (LMRA) information.<br>
 * Started by {@link OrderWorkActivity}.<br>
 * Next activity {@link OrderWorkActivity}.
 * <p/>
 * Allow to register dangerous situations, make LMRA photos, preview and manage them.<br>
 * Required android {@code WRITE_EXTERNAL_STORAGE}
 * permission and will request it if it is not granted.<br>
 * Photos stored  in current order folder: {@link Utils#getOrderDir(long)}.
 * <p/>
 * Use {@link LMRADialog} for creating new LMRA.<br>
 * Use {@link LMRAAdapter} for LMRA {@link ListView}.<br>
 */

public class LMRAActivity extends BaseActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;

    @BindView(R.id.activity_lmra_list)
    ListView listViewLMRA;

    public static List<LMRAModel> lmraList;
    public LMRAAdapter lmraAdapter;

    /**
     * Need for onActivityResult
     */
    public static long currentLMRAID;
    /**
     * Need for onActivityResult
     */
    public static File currentLMRAProtoFile;

    private Realm monitorRealm;
    private RealmChangeListener<RealmResults<LMRAItem>> lmraItemListener;
    private RealmResults<LMRAItem> lmraItemsInDB;


    // App Log monitor
    private Realm monitorLogRealm = Session.getLogRealm();
    private RealmChangeListener<RealmResults<AppLogItem>> logListener;
    private RealmResults<AppLogItem> logsWithNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmra);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(getText(R.string.lmra_title));

        // Setting App log listener
        logListener = AppLog.setLogListener(LMRAActivity.this, monitorLogRealm);
        logsWithNotification = AppLog.addListener(monitorLogRealm, logListener);

        // Exit if Session is empty
        if (Session.getCurrentOrder() <= 0L) {
            AppLog.E(this, "No order number.");
            // Give time to read message
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    final Intent intent = new Intent(LMRAActivity.this, OrderPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);
            return;
        }

        AppLog.serviceI(false, Session.getCurrentOrder(), "Create activity: " + LMRAActivity.class.getSimpleName());

        if (!Utils.isExternalStorageWritable()) {
            AppLog.E(this, "Can not write photos file to external storage.");
            return;
        }

        Utils.requestWritePermissionsIfNeeded(this);

        lmraList = new ArrayList<>();
        DbUtils.updateLMRAList(lmraList);
        lmraAdapter = new LMRAAdapter(this, lmraList);
        listViewLMRA.setAdapter(lmraAdapter);

        // Creating LMRA callback
        monitorRealm = Realm.getDefaultInstance();
        lmraItemListener = new RealmChangeListener<RealmResults<LMRAItem>>() {
            @Override
            public void onChange(RealmResults<LMRAItem> lmraItems) {
                DbUtils.updateLMRAList(lmraList);
                lmraAdapter.notifyDataSetChanged();
            }
        };

        lmraItemsInDB = monitorRealm.where(LMRAItem.class)
                .equalTo("number", Session.getCurrentOrder())
                .findAll();

        lmraItemsInDB.addChangeListener(lmraItemListener);
    }

    //Add + as a menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_lmra, menu);
        return true;
    }

    //Fire up the Dialog
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_lmra_add:
                AppCompatDialogFragment lmraDialog = new LMRADialog();
                lmraDialog.show(getSupportFragmentManager(), "New LMRA");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (currentLMRAID != 0 && currentLMRAProtoFile != null) {
                    final File privatePhotoFile = new File(Utils.getOrderDir(Session.getCurrentOrder()), currentLMRAProtoFile.getName());
                    Utils.copyFile(currentLMRAProtoFile, privatePhotoFile);
                    currentLMRAProtoFile.delete();

                    if (!privatePhotoFile.exists()) {
                        LMRAActivity.currentLMRAID = 0;
                        LMRAActivity.currentLMRAProtoFile = null;
                        return;
                    }

                    DbUtils.saveLMRAPhotoInDB(currentLMRAID, privatePhotoFile);
                    DbUtils.updateLMRAList(lmraList);
                    lmraAdapter.notifyDataSetChanged();
                }
            } else {

                LMRAActivity.currentLMRAID = 0;
                LMRAActivity.currentLMRAProtoFile = null;
            }
        }
    }

    @Nullable
    public static LMRAModel getLMRAModelByID(final long lmraId) {
        for (LMRAModel lmraModel : lmraList) {
            if (lmraModel.getLmraId() == lmraId) return lmraModel;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lmraItemsInDB.removeChangeListener(lmraItemListener);
        monitorRealm.close();
        AppLog.removeListener(monitorLogRealm, logsWithNotification, logListener);
        lmraList = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Utils.REQUEST_WRITE_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
            } else {
                // permission denied
                AppLog.E(this, "Permissions not granted!");

                // Give time to read message
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        LMRAActivity.this.finish();
                    }
                }, 3000);

            }
        }
    }
}
