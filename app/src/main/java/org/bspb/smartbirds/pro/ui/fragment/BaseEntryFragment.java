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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    @ViewsById({R.id.picture1, R.id.picture2, R.id.picture3})
    protected List<ImageView> pictures;

    @Bean
    protected EEventBus eventBus;

    @OptionsMenuItem(R.id.take_picture)
    MenuItem takePicture;

    protected ImageStruct[] images = new ImageStruct[3];
    protected ImageStruct currentImage;
    private int picturesCount = 0;

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
            for (int i=0; i<images.length; i++) {
                data.put("Picture"+i, images[i] != null ? images[i].fileName : "");
            }
            data.put(getResources().getString(R.string.entry_date), Configuration.STORAGE_DATE_FORMAT.format(new Date()));
            data.put(getResources().getString(R.string.entry_time), Configuration.STORAGE_TIME_FORMAT.format(new Date()));
            eventBus.post(new EntrySubmitted(data, getEntryType()));
        }
    }

    @OptionsItem(R.id.take_picture)
    void onTakePicture(MenuItem item) {
        if (INTENT_TAKE_PICTURE.resolveActivity(getActivity().getPackageManager()) != null) {
            item.setEnabled(false);
            eventBus.post(new CreateImageFile());
        }
    }

    public void onEvent(ImageFileCreatedFailed event) {
        Toast.makeText(getActivity(), R.string.image_file_create_error, Toast.LENGTH_SHORT).show();
        takePicture.setEnabled(true);
    }

    public void onEvent(ImageFileCreated event) {
        currentImage = new ImageStruct(event.imageFileName, event.imagePath, event.uri);
        Intent intent = new Intent(INTENT_TAKE_PICTURE).putExtra(MediaStore.EXTRA_OUTPUT, event.uri);
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
    }

    @OnActivityResult(REQUEST_TAKE_PICTURE)
    void onTakePictureResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            // Get the dimensions of the View
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int targetW = (int) Math.round(320.0 / displayMetrics.density);
            int targetH = (int) Math.round(320.0 / displayMetrics.density);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentImage.path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentImage.path, bmOptions);
            images[picturesCount] = currentImage;
            currentImage = null;
            ImageView picture = pictures.get(picturesCount);
            picturesCount++;
            picture.setImageBitmap(bitmap);
            picture.setVisibility(View.VISIBLE);

            takePicture.setEnabled(picturesCount < images.length);
        } else {
            currentImage = null;
            takePicture.setEnabled(true);
        }
    }

    @Click({R.id.picture1, R.id.picture2, R.id.picture3})
    void Click(View v) {
        int idx = pictures.indexOf(v);
        if (idx < 0 || idx >= images.length) return;
        Intent intent = new Intent(INTENT_VIEW_PICTURE).setDataAndType(images[idx].uri, "image/jpg");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private static class ImageStruct {
        public String fileName = null;
        public String path;
        public Uri uri;

        public ImageStruct(String fileName, String path, Uri uri) {
            this.fileName = fileName;
            this.path = path;
            this.uri = uri;
        }
    }
}
