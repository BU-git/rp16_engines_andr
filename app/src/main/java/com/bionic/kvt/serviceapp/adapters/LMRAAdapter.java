package com.bionic.kvt.serviceapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.models.LMRAModel;
import com.bionic.kvt.serviceapp.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * LMRA Adapter
 */
public class LMRAAdapter extends ArrayAdapter<LMRAModel> {
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Context context;

    private static class LMRAViewHolder {
        private TextView lmraName;
        private TextView lmraDescription;
        private Button lmraDeleteButton;
        private ImageButton lmraCameraButton;
    }

    public LMRAAdapter(Context context, List<LMRAModel> lmraModelList) {
        super(context, R.layout.template_lmra, lmraModelList);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LMRAModel lmraModel = getItem(position);
        final LMRAViewHolder lmraViewHolder;

        if (convertView == null) {
            lmraViewHolder = new LMRAViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.template_lmra, parent, false);
            lmraViewHolder.lmraName = (TextView) convertView.findViewById(R.id.title_lmra);
            lmraViewHolder.lmraDescription = (TextView) convertView.findViewById(R.id.description_lmra);
            lmraViewHolder.lmraDeleteButton = (Button) convertView.findViewById(R.id.button_lmra_delete);
            lmraViewHolder.lmraCameraButton = (ImageButton) convertView.findViewById(R.id.button_lmra_camera);

            convertView.setTag(lmraViewHolder);
        } else {
            lmraViewHolder = (LMRAViewHolder) convertView.getTag();
        }

        lmraViewHolder.lmraName.setText(lmraModel.getLmraName());
        lmraViewHolder.lmraDescription.setText(lmraModel.getLmraDescription());
        lmraViewHolder.lmraDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DbUtils.removeLMRAFromDb(lmraModel.getLmraId());
                DbUtils.updateLMRAList(LMRAActivity.lmraList);
                notifyDataSetChanged();
            }
        });

        lmraViewHolder.lmraCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = (Activity) context;
                final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    final File photoFile = Utils.createImageFile(Session.getCurrentOrder());
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });

        return convertView;
    }
}
