package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.util.HashMap;

/**
 * Created by dani on 14-11-12.
 */
@EFragment
public abstract class BaseEntryFragment extends Fragment {

    protected static final String ARG_LAT = "lat";
    protected static final String ARG_LON = "lon";
    protected static final Intent INTENT_TAKE_PICTURE = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    protected static final Intent INTENT_VIEW_PICTURE = new Intent(Intent.ACTION_VIEW);
    protected static final int REQUEST_TAKE_PICTURE = 2;

    @FragmentArg(ARG_LAT)
    protected double lat;
    @FragmentArg(ARG_LON)
    protected double lon;

    @ViewById(R.id.picture)
    protected ImageView picture;

    @Bean
    protected EEventBus eventBus;

    protected String imageFileName = null;
    protected String imagePath;
    protected Uri imageUri;

    abstract EntryType getEntryType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @OptionsItem(R.id.action_submit)
    void onSubmitClicked(MenuItem item) {
        FormUtils.FormModel form = FormUtils.traverseForm(getView());
        if (form.validateFields()) {
            item.setEnabled(false);
            HashMap<String, String> data = form.serialize();
            data.put("Lat", Double.toString(lat));
            data.put("Long", Double.toString(lon));
            data.put("Picture", imageFileName != null ? imageFileName : "");
            eventBus.post(new EntrySubmitted(data, getEntryType()));
        }
    }

    @OptionsItem(R.id.take_picture)
    void onTakePicture(MenuItem item) {
        item.setEnabled(false);
        if (INTENT_TAKE_PICTURE.resolveActivity(getActivity().getPackageManager()) != null) {
            eventBus.post(new CreateImageFile());
        }
    }

    public void onEvent(ImageFileCreated event) {
        imageFileName = event.imageFileName;
        imagePath = event.imagePath;
        imageUri = event.uri;
        Intent intent = new Intent(INTENT_TAKE_PICTURE).putExtra(MediaStore.EXTRA_OUTPUT, event.uri);
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
    }

    @OnActivityResult(REQUEST_TAKE_PICTURE)
    void onTakePictureResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            // Get the dimensions of the View
            DisplayMetrics displayMetrics = picture.getResources().getDisplayMetrics();
            int targetW = (int) Math.round(320.0 / displayMetrics.density);
            int targetH = (int) Math.round(320.0 / displayMetrics.density);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
            picture.setImageBitmap(bitmap);
        } else {
            imageFileName = null;
        }
    }

    @Click(R.id.picture)
    void onPictureClick() {
        Intent intent = new Intent(INTENT_VIEW_PICTURE).setDataAndType(imageUri, "image/jpg");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
