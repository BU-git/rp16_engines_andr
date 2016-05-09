package com.bionic.kvt.serviceapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.Session;
import com.bionic.kvt.serviceapp.activities.LMRAActivity;
import com.bionic.kvt.serviceapp.db.DbUtils;
import com.bionic.kvt.serviceapp.dialogs.LMRADialog;
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

    private static class LMRAViewHolder implements ViewSwitcher.ViewFactory {
        private Context context;
        private long lmraId;
        private TextView lmraName;
        private TextView lmraDescription;
        private List<File> listLMRAPhotos;
        private Button lmraDeleteButton;

        private ImageSwitcher lmraImageSwitcher;
        private Button lmraPrevButton;
        private TextView lmra_photo_count;
        private Button lmraNetxButton;
        private Button lmraCameraButton;
        private Button lmraDeletePhotoButton;


        private int currentPhotoPosition = 0;

        private void setNextPhoto() {
            if (listLMRAPhotos != null && !listLMRAPhotos.isEmpty()) {
                currentPhotoPosition++;
                if (currentPhotoPosition > listLMRAPhotos.size() - 1) currentPhotoPosition = 0;

                lmraImageSwitcher.setImageURI(Uri.fromFile(listLMRAPhotos.get(currentPhotoPosition)));
                lmra_photo_count.setText((currentPhotoPosition + 1) + " of " + listLMRAPhotos.size());
            } else {
                lmra_photo_count.setText("0 of 0");
            }
        }

        private void setPrevPhoto() {
            if (listLMRAPhotos != null && !listLMRAPhotos.isEmpty()) {
                currentPhotoPosition--;
                if (currentPhotoPosition < 0) currentPhotoPosition = listLMRAPhotos.size() - 1;

                lmraImageSwitcher.setImageURI(Uri.fromFile(listLMRAPhotos.get(currentPhotoPosition)));
                lmra_photo_count.setText((currentPhotoPosition + 1) + " of " + listLMRAPhotos.size());
            } else {
                lmra_photo_count.setText("0 of 0");
                lmraImageSwitcher.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_lmra_no_photo_24dp));
            }
        }

        private void setLastPhoto() {
            if (listLMRAPhotos != null && !listLMRAPhotos.isEmpty()) {
                currentPhotoPosition = listLMRAPhotos.size() - 2;
                setNextPhoto();
            } else {
                lmra_photo_count.setText("0 of 0");
                lmraImageSwitcher.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_lmra_no_photo_24dp));
            }
        }


        @Override
        public View makeView() {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new
                    ImageSwitcher.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(0xFFFFFFFF);
            return imageView;
        }
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
            lmraViewHolder.context = context;
            lmraViewHolder.lmraName = (TextView) convertView.findViewById(R.id.title_lmra);
            lmraViewHolder.lmraDescription = (TextView) convertView.findViewById(R.id.description_lmra);
            lmraViewHolder.lmraDeleteButton = (Button) convertView.findViewById(R.id.button_lmra_delete);

            lmraViewHolder.lmraImageSwitcher = (ImageSwitcher) convertView.findViewById(R.id.lmra_imageSwitcher);

            lmraViewHolder.lmraImageSwitcher.setFactory(lmraViewHolder);
            Animation inAnimation = new AlphaAnimation(0, 1);
            inAnimation.setDuration(500);
            Animation outAnimation = new AlphaAnimation(1, 0);
            outAnimation.setDuration(500);
            lmraViewHolder.lmraImageSwitcher.setInAnimation(inAnimation);
            lmraViewHolder.lmraImageSwitcher.setOutAnimation(outAnimation);

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

        // Setting first photo if exist
        lmraViewHolder.setLastPhoto();

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
            }
        });

        lmraViewHolder.lmraNetxButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lmraViewHolder.setNextPhoto();
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
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    final File photoFile = Utils.createImageFile(Session.getCurrentOrder());
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        LMRAActivity.currentLMRAID = lmraViewHolder.lmraId;
                        LMRAActivity.currentLMRAProtoFile = photoFile;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });


        lmraViewHolder.lmraDeletePhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lmraViewHolder.listLMRAPhotos != null && lmraViewHolder.listLMRAPhotos.size() > 0){
                    DbUtils.removeLMRAPhoto(lmraViewHolder.lmraId,
                            lmraViewHolder.listLMRAPhotos.get(lmraViewHolder.currentPhotoPosition).toString());
                    DbUtils.updateLMRAList(LMRAActivity.lmraList);
                    notifyDataSetChanged();
                }
            }
        });



        return convertView;
    }

    private void triggerChange(LMRAModel lmraModel) {
        LMRADialog lmraDialog = new LMRADialog();
        lmraDialog.setTitleId(R.string.none);
        lmraDialog.setEdit(true);
        LMRAActivity.currentLMRAID = lmraModel.getLmraId();
        lmraDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "Modified dialog");
    }

}
