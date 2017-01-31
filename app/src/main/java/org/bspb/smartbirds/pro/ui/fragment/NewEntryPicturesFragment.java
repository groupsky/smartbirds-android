package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewsById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed;

import java.util.HashMap;
import java.util.List;

/**
 * Created by groupsky on 27.01.17.
 */

@EFragment(R.layout.fragment_form_pictures)
@OptionsMenu(R.menu.form_pictures)
public class NewEntryPicturesFragment extends BaseFormFragment {

    protected static final Intent INTENT_TAKE_PICTURE = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    protected static final Intent INTENT_VIEW_PICTURE = new Intent(Intent.ACTION_VIEW);
    protected static final int REQUEST_TAKE_PICTURE = 2;

    @ViewsById({R.id.picture1, R.id.picture2, R.id.picture3})
    protected List<ImageView> pictures;

    @Bean
    protected EEventBus eventBus;

    @OptionsMenuItem(R.id.take_picture)
    MenuItem takePicture;

    @InstanceState
    protected ImageStruct[] images = new ImageStruct[3];
    @InstanceState
    protected ImageStruct currentImage;
    @InstanceState
    protected int picturesCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);

        for (int i=0; i<pictures.size(); i++) {
            pictures.get(i).setVisibility(i < picturesCount ? View.VISIBLE : View.GONE);
            if (i <picturesCount) {
                displayPicture(images[i], pictures.get(i));
            } else {
                hidePicture(pictures.get(i));
            }
        }
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected HashMap<String, String> serialize() {
        HashMap<String, String> data = super.serialize();
        for (int i = 0; i < images.length; i++) {
            data.put("Picture" + i, images[i] != null ? images[i].fileName : "");
        }
        return data;
    }

    @OptionsItem(R.id.take_picture)
    void onTakePicture(MenuItem item) {
        if (picturesCount >= pictures.size()) {
            return;
        }
        if (INTENT_TAKE_PICTURE.resolveActivity(getActivity().getPackageManager()) != null) {
            item.setEnabled(false);
            eventBus.post(new CreateImageFile());
        }
    }

    public void onEvent(ImageFileCreatedFailed event) {
        Toast.makeText(getActivity(), R.string.image_file_create_error, Toast.LENGTH_SHORT).show();
    }

    public void onEvent(ImageFileCreated event) {
        currentImage = new ImageStruct(event.imageFileName, event.imagePath, event.uri);
        Intent intent = new Intent(INTENT_TAKE_PICTURE).putExtra(MediaStore.EXTRA_OUTPUT, event.uri);
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (takePicture != null) {
            takePicture.setVisible(!pictures.isEmpty());
            takePicture.setEnabled(picturesCount < pictures.size());
        }
    }

    @OnActivityResult(REQUEST_TAKE_PICTURE)
    void onTakePictureResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            images[picturesCount] = currentImage;
            currentImage = null;
            displayPicture(images[picturesCount], pictures.get(picturesCount));
            picturesCount++;
        } else {
            currentImage = null;
        }
        takePicture.setEnabled(picturesCount < pictures.size());
    }

    void displayPicture(ImageStruct image, ImageView picture) {
        // Get the dimensions of the View
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int targetW = (int) Math.round(320.0 / displayMetrics.density);
        int targetH = (int) Math.round(320.0 / displayMetrics.density);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(image.path, bmOptions);
        picture.setImageBitmap(bitmap);
        picture.setVisibility(View.VISIBLE);
    }

    void hidePicture(ImageView picture) {
        picture.setVisibility(View.GONE);
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


    static class ImageStruct implements Parcelable {
        public String fileName = null;
        public String path;
        public Uri uri;

        public ImageStruct(String fileName, String path, Uri uri) {
            this.fileName = fileName;
            this.path = path;
            this.uri = uri;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.fileName);
            dest.writeString(this.path);
            dest.writeParcelable(this.uri, flags);
        }

        protected ImageStruct(Parcel in) {
            this.fileName = in.readString();
            this.path = in.readString();
            this.uri = in.readParcelable(Uri.class.getClassLoader());
        }

        public static final Parcelable.Creator<ImageStruct> CREATOR = new Parcelable.Creator<ImageStruct>() {
            @Override
            public ImageStruct createFromParcel(Parcel source) {
                return new ImageStruct(source);
            }

            @Override
            public ImageStruct[] newArray(int size) {
                return new ImageStruct[size];
            }
        };
    }

}