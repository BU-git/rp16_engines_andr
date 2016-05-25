package com.bionic.kvt.serviceapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.models.LMRAModel;

/**
 * LMRA Dialog showing by clicking the plus im menu.<br>
 * Allow to create ne LMRA item.
 */
public class LMRADialog extends AppCompatDialogFragment {
    View view = null;
    boolean isEdit = false;

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public LMRADialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_lmra, null);

        builder.setView(view)
                .setTitle(isEdit ? R.string.edit_lmra_template : R.string.new_lmra_template)
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
                        LMRAActivity.currentLMRAID = 0;
                        LMRADialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

            final EditText mLmraNameView = (EditText) view.findViewById(R.id.title_lmra_add);
            final EditText mLmraDescriptionView = (EditText) view.findViewById(R.id.description_lmra_add);

            if (isEdit) {
                LMRAModel lmraModelItem = LMRAActivity.getLMRAModelByID(LMRAActivity.currentLMRAID);

                if (lmraModelItem != null) {
                    mLmraNameView.setText(lmraModelItem.getLmraName());
                    mLmraDescriptionView.setText(lmraModelItem.getLmraDescription());
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.lmra_save));
                }
            }
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isError = false;
                    View focusView = null;

                    mLmraNameView.setError(null);
                    mLmraDescriptionView.setError(null);

                    if (TextUtils.isEmpty(mLmraNameView.getText().toString())) {
                        mLmraNameView.setError(getString(R.string.error_field_required));
                        focusView = mLmraNameView;
                        isError = true;
                    } else if (TextUtils.isEmpty(mLmraDescriptionView.getText().toString())) {
                        mLmraDescriptionView.setError(getString(R.string.error_field_required));
                        focusView = mLmraDescriptionView;
                        isError = true;
                    }

                    if (isError) {
                        focusView.requestFocus();
                        return;
                    }

                    if (isEdit) { // Editing existing
                        DbUtils.updateLMRAInDB(LMRAActivity.currentLMRAID,
                                mLmraNameView.getText().toString(),
                                mLmraDescriptionView.getText().toString());
                        DbUtils.updateLMRAList(LMRAActivity.lmraList);
                        LMRAActivity.lmraAdapter.notifyDataSetChanged();
                        LMRAActivity.currentLMRAID = 0;
                    } else { // Creating new
                        DbUtils.createNewLMRAInDB(mLmraNameView.getText().toString(), mLmraDescriptionView.getText().toString());
                        DbUtils.updateLMRAList(LMRAActivity.lmraList);
                        LMRAActivity.lmraAdapter.notifyDataSetChanged();
                    }

                    dialog.dismiss();

                }
            });
        }
    }
}
