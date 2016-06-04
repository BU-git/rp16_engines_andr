package com.bionic.kvt.serviceapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.activities.LMRAImageActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.dialogs.LMRADialog;
import com.bionic.kvt.serviceapp.models.LMRAModel;
import com.bionic.kvt.serviceapp.utils.AppLog;
import com.bionic.kvt.serviceapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * LMRA Adapter.<br>
 * User by {@link LMRAActivity} fro showing LMRA items.<br>
 * Start external intent {@link MediaStore#ACTION_IMAGE_CAPTURE} for taking photo <br>
 * and storing it in  application external directory {@link Utils#createImageFile(long)}.<br>
 * Once executed, photo moved to application internal directory.
 */
public class LMRAAdapter extends ArrayAdapter<LMRAModel> {
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Context context;

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
            lmraViewHolder.context = context;
            lmraViewHolder.lmraName = (TextView) convertView.findViewById(R.id.title_lmra);
            lmraViewHolder.lmraDescription = (TextView) convertView.findViewById(R.id.description_lmra);
            lmraViewHolder.lmraDeleteButton = (Button) convertView.findViewById(R.id.button_lmra_delete);
            lmraViewHolder.lmraImageView = (ImageView) convertView.findViewById(R.id.lmra_imageSwitcher);

            lmraViewHolder.lmraPrevButton = (Button) convertView.findViewById(R.id.lmra_button_prev);
            lmraViewHolder.lmra_photo_count = (TextView) convertView.findViewById(R.id.lmra_photo_count);
            lmraViewHolder.lmraNetxButton = (Button) convertView.findViewById(R.id.lmra_button_next);
            lmraViewHolder.lmraCameraButton = (Button) convertView.findViewById(R.id.button_lmra_camera);
            lmraViewHolder.lmraDeletePhotoButton = (Button) convertView.findViewById(R.id.button_lmra_delete_photo);

            convertView.setTag(lmraViewHolder);
        } else {
            lmraViewHolder = (LMRAViewHolder) convertView.getTag();
        }

        lmraViewHolder.lmraId = lmraModel.getLmraId();
        lmraViewHolder.lmraName.setText(lmraModel.getLmraName());
        lmraViewHolder.lmraDescription.setText(lmraModel.getLmraDescription());
        lmraViewHolder.listLMRAPhotos = lmraModel.getListLMRAPhotos();

        // Setting last photo if exist
        lmraViewHolder.setLastPhoto();
        lmraViewHolder.showPhoto();

        lmraViewHolder.lmraDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DbUtils.removeLMRAFromDB(lmraModel.getLmraId());
                DbUtils.updateLMRAList(LMRAActivity.lmraList);
                notifyDataSetChanged();
            }
        });

        lmraViewHolder.lmraPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lmraViewHolder.setPrevPhoto();
                lmraViewHolder.showPhoto();
            }
        });

        lmraViewHolder.lmraNetxButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lmraViewHolder.setNextPhoto();
                lmraViewHolder.showPhoto();
            }
        });

        lmraViewHolder.lmraName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerChange(lmraModel);
            }
        });

        lmraViewHolder.lmraDescription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerChange(lmraModel);
            }
        });

        lmraViewHolder.lmraCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = (Activity) context;
                final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                LMRAActivity.currentLMRAID = 0;
                LMRAActivity.currentLMRAProtoFile = null;

                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) == null) return;

                final File photoFile = Utils.createImageFile(Session.getCurrentOrder());
                if (photoFile == null) {
                    AppLog.serviceE(true, Session.getCurrentOrder(), "Can not create image file.");
                    return;
                }

                LMRAActivity.currentLMRAID = lmraViewHolder.lmraId;
                LMRAActivity.currentLMRAProtoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        });


        lmraViewHolder.lmraDeletePhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lmraViewHolder.listLMRAPhotos == null || lmraViewHolder.listLMRAPhotos.size() == 0)
                    return;
                DbUtils.removeLMRAPhoto(lmraViewHolder.lmraId,
                        lmraViewHolder.listLMRAPhotos.get(lmraViewHolder.currentPhotoPosition).toString());
                DbUtils.updateLMRAList(LMRAActivity.lmraList);
                notifyDataSetChanged();
            }
        });

        lmraViewHolder.lmraImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lmraViewHolder.listLMRAPhotos == null || lmraViewHolder.listLMRAPhotos.size() == 0)
                    return;
                final File currentPhotoFile = lmraViewHolder.listLMRAPhotos.get(lmraViewHolder.currentPhotoPosition);
                final Intent intent = new Intent(context, LMRAImageActivity.class);
                intent.putExtra("imageFile", currentPhotoFile.toString());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void triggerChange(LMRAModel lmraModel) {
        LMRADialog lmraDialog = new LMRADialog();
        lmraDialog.setEdit(true);
        LMRAActivity.currentLMRAID = lmraModel.getLmraId();
        lmraDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "Modified dialog");
    }

    private class LMRAViewHolder {
        private Context context;

        private long lmraId;
        private List<File> listLMRAPhotos;
        private int currentPhotoPosition = 0;

        private TextView lmraName;
        private TextView lmraDescription;
        private Button lmraDeleteButton;

        private ImageView lmraImageView;
        private Button lmraPrevButton;
        private TextView lmra_photo_count;
        private Button lmraNetxButton;
        private Button lmraCameraButton;
        private Button lmraDeletePhotoButton;

        private void setNextPhoto() {
            if (listLMRAPhotos == null || listLMRAPhotos.isEmpty()) return;

            currentPhotoPosition++;
            if (currentPhotoPosition > listLMRAPhotos.size() - 1) currentPhotoPosition = 0;
        }

        private void setPrevPhoto() {
            if (listLMRAPhotos == null || listLMRAPhotos.isEmpty()) return;

            currentPhotoPosition--;
            if (currentPhotoPosition < 0) currentPhotoPosition = listLMRAPhotos.size() - 1;
        }

        private void setLastPhoto() {
            if (listLMRAPhotos == null || listLMRAPhotos.isEmpty()) return;

            currentPhotoPosition = listLMRAPhotos.size() - 1;
        }

        private void showPhoto() {
            if (listLMRAPhotos == null || listLMRAPhotos.isEmpty()) {
                lmra_photo_count.setText(context.getText(R.string.lmra_no_photos));
                lmraImageView.setImageDrawable(ContextCompat.getDrawable(lmraImageView.getContext(), R.drawable.ic_lmra_no_photo_24dp));
                return;
            }

            Picasso.with(lmraImageView.getContext())
                    .load(Uri.fromFile(listLMRAPhotos.get(currentPhotoPosition)))
                    .resize(200, 200)
                    .centerCrop()
                    .into(lmraImageView);

            final String text = (currentPhotoPosition + 1) + " " + context.getText(R.string.lmra_of_photos).toString() + " " + listLMRAPhotos.size();
            lmra_photo_count.setText(text);
        }
    }

}
