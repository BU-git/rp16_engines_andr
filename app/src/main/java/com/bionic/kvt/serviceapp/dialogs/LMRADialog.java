package com.bionic.kvt.serviceapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.models.LMRA;

/*
LMRA Dialog to show by clicking the plus
*/
public class LMRADialog extends DialogFragment {

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

                        LMRA lmraNew = new LMRA(mLmraNameView.getText().toString(), mLmraDescriptionView.getText().toString());
                        LMRAActivity.lmraList.add(lmraNew);

                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LMRADialog.this.getDialog().cancel();
                    }
                }); ;

        return builder.create();
    }
}
