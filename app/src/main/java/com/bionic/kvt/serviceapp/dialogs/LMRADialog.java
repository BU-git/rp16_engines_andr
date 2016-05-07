package com.bionic.kvt.serviceapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;

/*
LMRA Dialog to show by clicking the plus
*/
public class LMRADialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_lmra, null);

        builder.setView(view)
                .setTitle(R.string.new_lmra_template)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    boolean cancel = false;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText mLmraNameView = (EditText) view.findViewById(R.id.title_lmra_add);
                        EditText mLmraDescriptionView = (EditText) view.findViewById(R.id.description_lmra_add);

                        DbUtils.createNewLMRAInDb(mLmraNameView.getText().toString(), mLmraDescriptionView.getText().toString());

                        DbUtils.updateLMRAList(LMRAActivity.lmraList);
                        LMRAActivity.lmraAdapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LMRADialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
