package com.bionic.kvt.serviceapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.db.LMRAItem;

/*
LMRA Dialog to show by clicking the plus
*/
public class LMRADialog extends AppCompatDialogFragment {
    public static final String TAG = LMRADialog.class.getName();
    View view = null;
    Integer titleId = R.string.new_lmra_template;
    boolean isEdit = false;

    public Integer getTitleId() {
        return titleId;
    }

    public void setTitleId(Integer titleId) {
        this.titleId = titleId;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public LMRADialog () {};

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_lmra, null);

        builder.setView(view)
                .setTitle(titleId)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {

                    //boolean cancel = false;
                    //View focusView = null;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This is going to be handled within onStart method
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LMRADialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null){
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);

            final EditText mLmraNameView = (EditText) view.findViewById(R.id.title_lmra_add);
            final EditText mLmraDescriptionView = (EditText) view.findViewById(R.id.description_lmra_add);

            if (isEdit){
                LMRAItem lmraItem = DbUtils.getLMRAfromDB(LMRAActivity.currentLMRAID);
                if (lmraItem != null){
                    mLmraNameView.setText(lmraItem.getLmraName());
                    mLmraDescriptionView.setText(lmraItem.getLmraDescription());
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.lmra_edit));
                }
            }
            positiveButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    boolean cancel = false;
                    View focusView = null;

                    mLmraNameView.setError(null);
                    mLmraDescriptionView.setError(null);

                    if (TextUtils.isEmpty(mLmraNameView.getText().toString())){
                        mLmraNameView.setError(getString(R.string.error_field_required));
                        focusView = mLmraNameView;
                        cancel = true;
                    } else if (TextUtils.isEmpty(mLmraDescriptionView.getText().toString())) {
                        mLmraDescriptionView.setError(getString(R.string.error_field_required));
                        focusView = mLmraDescriptionView;
                        cancel = true;
                    }

                    if (!cancel){
                        if (!isEdit){
                            DbUtils.createNewLMRAInDB(mLmraNameView.getText().toString(), mLmraDescriptionView.getText().toString());
                            DbUtils.updateLMRAList(LMRAActivity.lmraList);
                            LMRAActivity.lmraAdapter.notifyDataSetChanged();
                        } else {
                            DbUtils.updateLMRAInDB(LMRAActivity.currentLMRAID,
                                    mLmraNameView.getText().toString(),
                                    mLmraDescriptionView.getText().toString());
                            LMRAActivity.lmraAdapter.notifyDataSetChanged();
                        }

                        dialog.dismiss();
                    } else {
                        focusView.requestFocus();
                    }
                }
            });
        }
    }
}
