package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
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
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.GetImageFile;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed;
import org.bspb.smartbirds.pro.events.ImageFileEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by groupsky on 27.01.17.
 */

@EFragment(R.layout.fragment_form_pictures)
@OptionsMenu(R.menu.form_pictures)
public class NewEntryPicturesFragment extends BaseFormFragment {

    private static final String TAG = SmartBirdsApplication.TAG + ".PicFrm";

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
    protected int picturesCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @AfterInject
    protected void registerBus() {
        eventBus.register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (eventBus != null) registerBus();
    }

    @Override
    public void onStart() {
        super.onStart();

        for (int i = 0; i < pictures.size(); i++) {
            pictures.get(i).setVisibility(i < picturesCount ? View.VISIBLE : View.INVISIBLE);
            if (i < picturesCount) {
                displayPicture(images[i], pictures.get(i));
            } else {
                if (images[i] != null) {
                    logException(new IllegalStateException("Hiding picture that actually exists!"));
                }
                hidePicture(pictures.get(i));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        eventBus.unregister(this);
        super.onDetach();
    }

    @Override
    protected HashMap<String, String> serialize() {
        Log.d(TAG, String.format(Locale.ENGLISH, "serializing: %d", picturesCount));
        HashMap<String, String> data = super.serialize();
        for (int i = 0; i < images.length; i++) {
            data.put("Picture" + i, images[i] != null ? images[i].fileName : "");
        }
        return data;
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        Log.d(TAG, String.format(Locale.ENGLISH, "deserializing: %d", picturesCount));
        picturesCount = 0;
        for (int i = 0; i < images.length; i++) {
            String fileName = data.get("Picture" + i);
            if (isEmpty(fileName)) continue;
            if (images[picturesCount] != null) {
                if (TextUtils.equals(images[picturesCount].fileName, fileName)) {
                    picturesCount++;
                    continue;
                }
            }
            eventBus.post(new GetImageFile(monitoringCode, fileName));
        }
        updateTakePicture();
    }

    public void onEventMainThread(ImageFileEvent event) {
        if (images[picturesCount] != null) {
            logException(new IllegalStateException("Overriding existing picture!"));
        }
        images[picturesCount] = new ImageStruct(event.imageFileName, event.imagePath, event.uri);
        if (pictures != null) displayPicture(images[picturesCount], pictures.get(picturesCount));
        picturesCount++;
        updateTakePicture();
    }

    @OptionsItem(R.id.take_picture)
    void onTakePicture(MenuItem item) {
        if (picturesCount >= pictures.size()) {
            return;
        }
        if (INTENT_TAKE_PICTURE.resolveActivity(getActivity().getPackageManager()) != null) {
            item.setEnabled(false);
            eventBus.post(new CreateImageFile(monitoringCode));
        }
    }

    public void onEventMainThread(@SuppressWarnings("UnusedParameters") ImageFileCreatedFailed event) {
        Toast.makeText(getActivity(), R.string.image_file_create_error, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(ImageFileCreated event) {
        currentImage = new ImageStruct(event.imageFileName, event.imagePath, event.uri);
        Intent intent = new Intent(INTENT_TAKE_PICTURE).putExtra(MediaStore.EXTRA_OUTPUT, event.uri);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClipData(ClipData.newRawUri("", event.uri));
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateTakePicture();
    }

    private void updateTakePicture() {
        if (takePicture != null) {
            // sometimes pictures is null
            takePicture.setVisible(pictures != null && !pictures.isEmpty());
            takePicture.setEnabled(pictures != null && picturesCount < pictures.size());
        }
    }

    @OnActivityResult(REQUEST_TAKE_PICTURE)
    void onTakePictureResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    if (images[picturesCount] != null) {
                        logException(new IllegalStateException("Overriding existing picture!"));
                    }
                    images[picturesCount] = currentImage;
                    currentImage = null;
                    displayPicture(images[picturesCount], pictures.get(picturesCount));
                    picturesCount++;
                    updateTakePicture();
                }
            });
        } else {
            currentImage = null;
        }
        if (takePicture != null) {
            takePicture.setEnabled(picturesCount < pictures.size());
        }
    }

    void displayPicture(ImageStruct image, ImageView picture) {
        // in some cases the fragment is not attached
        if (!isAdded()) {
            return;
        }

        // Get the dimensions of the View
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int targetW = (int) Math.round(displayMetrics.widthPixels / 3);
        int targetH = (int) Math.round(displayMetrics.widthPixels / 3);

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

        Bitmap bitmap = BitmapFactory.decodeFile(image.path, bmOptions);
        picture.setImageBitmap(bitmap);
        picture.setVisibility(View.VISIBLE);
    }

    void hidePicture(ImageView picture) {
        picture.setVisibility(View.INVISIBLE);
    }

    @Click({R.id.picture1, R.id.picture2, R.id.picture3})
    void Click(View v) {
        int idx = pictures.indexOf(v);
        if (idx < 0 || idx >= images.length) return;
        Intent intent = new Intent(INTENT_VIEW_PICTURE).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).setDataAndType(images[idx].uri, "image/jpg");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean isNewEntry() {
        throw new UnsupportedOperationException("not implemented");
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
