package org.bspb.smartbirds.pro.ui.fragment

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.InstanceState
import org.androidannotations.annotations.OnActivityResult
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.OptionsMenu
import org.androidannotations.annotations.OptionsMenuItem
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.events.CreateImageFile
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetImageFile
import org.bspb.smartbirds.pro.events.ImageFileCreated
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed
import org.bspb.smartbirds.pro.events.ImageFileEvent
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

@EFragment(R.layout.fragment_form_pictures)
@OptionsMenu(R.menu.form_pictures)
open class NewEntryPicturesFragment : BaseFormFragment() {

    companion object {
        const val TAG = SmartBirdsApplication.TAG + ".PicFrm"
        protected val INTENT_TAKE_PICTURE = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        protected val INTENT_VIEW_PICTURE = Intent(Intent.ACTION_VIEW)
        protected const val REQUEST_TAKE_PICTURE = 2
    }


    private val pictures: List<ImageView> by lazy {
        listOf(
            view?.findViewById(R.id.picture1) as ImageView,
            view?.findViewById(R.id.picture2) as ImageView,
            view?.findViewById(R.id.picture3) as ImageView
        )
    }


    protected val eventBus: EEventBus by lazy { EEventBus.getInstance() }

    @OptionsMenuItem(R.id.take_picture)
    protected lateinit var takePicture: MenuItem

    @JvmField
    @InstanceState
    protected var images = arrayOfNulls<ImageStruct>(3)

    @JvmField
    @InstanceState
    protected var currentImage: ImageStruct? = null

    @JvmField
    @InstanceState
    var picturesCount = 0

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult

            currentImage?.path?.apply {
                val file = File(this)
                FileUtils.copyUriContentToFile(requireContext(), uri, file)
            }

            images[picturesCount] = currentImage
            displayPicture(currentImage, pictures[picturesCount])
            picturesCount++
            updateTakePicture()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @AfterInject
    protected open fun registerBus() {
        eventBus.register(this)
    }

    override fun onStart() {
        super.onStart()
        for (i in pictures.indices) {
            pictures[i].visibility = if (i < picturesCount) View.VISIBLE else View.INVISIBLE
            if (i < picturesCount) {
                displayPicture(images[i], pictures[i])
            } else {
                if (images[i] != null) {
                    Reporting.logException(IllegalStateException("Hiding picture that actually exists!"))
                }
                hidePicture(pictures[i])
            }
        }
    }

    override fun onDetach() {
        eventBus.unregister(this)
        super.onDetach()
    }

    public override fun serialize(): HashMap<String, String> {
        Log.d(TAG, String.format(Locale.ENGLISH, "serializing: %d", picturesCount))
        val data = super.serialize()
        for (i in images.indices) {
            data["Picture$i"] = if (images[i] != null) images[i]!!.fileName else ""
        }
        return data
    }

    override fun deserialize(data: HashMap<String, String?>) {
        super.deserialize(data)
        Log.d(TAG, String.format(Locale.ENGLISH, "deserializing: %d", picturesCount))
        picturesCount = 0
        for (i in images.indices) {
            val fileName = data["Picture$i"]
            if (TextUtils.isEmpty(fileName)) continue
            if (images[picturesCount] != null) {
                if (TextUtils.equals(images[picturesCount]!!.fileName, fileName)) {
                    picturesCount++
                    continue
                }
            }
            eventBus.post(GetImageFile(monitoringCode, fileName))
        }
        updateTakePicture()
    }

    open fun onEventMainThread(event: ImageFileEvent) {
        if (images[picturesCount] != null) {
            Reporting.logException(IllegalStateException("Overriding existing picture!"))
        }
        images[picturesCount] = ImageStruct(event.imageFileName, event.imagePath, event.uri)
        displayPicture(images[picturesCount], pictures[picturesCount])
        picturesCount++
        updateTakePicture()
    }

    @OptionsItem(R.id.take_picture)
    open fun onTakePicture(item: MenuItem) {
        if (picturesCount >= pictures.size) {
            return
        }

        showPopup()
    }

    private fun showPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(
            R.array.attach_image_options
        ) { _, which ->
            when (which) {
                0 -> {
                    if (INTENT_TAKE_PICTURE.resolveActivity(requireActivity().packageManager) != null) {
                        eventBus.post(CreateImageFile(monitoringCode, "take_picture"))
                    }
                }

                1 -> {
                    eventBus.post(CreateImageFile(monitoringCode, "pick_image"))
                }
            }
        }
        builder.create().show()
    }

    open fun onEventMainThread(event: ImageFileCreatedFailed?) {
        Toast.makeText(activity, R.string.image_file_create_error, Toast.LENGTH_SHORT).show()
    }

    open fun onEventMainThread(event: ImageFileCreated) {
        currentImage = ImageStruct(event.imageFileName, event.imagePath, event.uri)
        if (event.action == "take_picture") {
            val intent = Intent(INTENT_TAKE_PICTURE).putExtra(MediaStore.EXTRA_OUTPUT, event.uri)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                intent.clipData = ClipData.newRawUri("", event.uri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(intent, REQUEST_TAKE_PICTURE)
        } else if (event.action == "pick_image") {
            pickImage.launch("image/*")
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        updateTakePicture()
    }

    private fun updateTakePicture() {
        if (this::takePicture.isInitialized) {
            if (readOnly) {
                takePicture.isVisible = false
                return
            }
            // sometimes pictures is null
            takePicture.isVisible = pictures.isNotEmpty()
            takePicture.isEnabled = picturesCount < pictures.size
        }
    }

    @OnActivityResult(REQUEST_TAKE_PICTURE)
    open fun onTakePictureResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            requireView().post {
                if (images[picturesCount] != null) {
                    Reporting.logException(IllegalStateException("Overriding existing picture!"))
                }
                images[picturesCount] = currentImage
                currentImage = null
                downscaleImage(images[picturesCount])
                displayPicture(images[picturesCount], pictures[picturesCount])
                picturesCount++
                updateTakePicture()
            }
        } else {
            currentImage = null
        }
        if (this::takePicture.isInitialized) {
            takePicture.isEnabled = picturesCount < pictures.size
        }
    }

    private fun downscaleImage(image: ImageStruct?) {
        if (image == null) {
            Reporting.logException(IllegalStateException("Bitmap is null!"))
            return
        }
        var bmp = BitmapFactory.decodeFile(image.path, BitmapFactory.Options())
        if (bmp.width > Configuration.IMAGE_DOWNSCALE_SIZE || bmp.height > Configuration.IMAGE_DOWNSCALE_SIZE) {
            bmp = if (bmp.width > bmp.height) {
                Bitmap.createScaledBitmap(
                    bmp, Configuration.IMAGE_DOWNSCALE_SIZE,
                    (bmp.height * Configuration.IMAGE_DOWNSCALE_SIZE / bmp.width), false
                )
            } else {
                Bitmap.createScaledBitmap(
                    bmp,
                    (bmp.width * Configuration.IMAGE_DOWNSCALE_SIZE / bmp.height),
                    Configuration.IMAGE_DOWNSCALE_SIZE,
                    false
                )
            }
            try {
                val out = FileOutputStream(images[picturesCount]!!.path)
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    open fun displayPicture(image: ImageStruct?, picture: ImageView) {
        // in some cases the fragment is not attached
        if (!isAdded) {
            return
        }

        // Get the dimensions of the View
        val displayMetrics = resources.displayMetrics

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(image!!.path, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = min(
            photoW / (displayMetrics.widthPixels / 3).toFloat().roundToInt(),
            photoH / (displayMetrics.widthPixels / 3).toFloat().roundToInt()
        )

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        val bitmap = BitmapFactory.decodeFile(image.path, bmOptions)
        picture.setImageBitmap(bitmap)
        picture.visibility = View.VISIBLE
    }

    open fun hidePicture(picture: ImageView) {
        picture.visibility = View.INVISIBLE
    }

    @Click(R.id.picture1, R.id.picture2, R.id.picture3)
    open fun onPictureClick(v: View?) {
        val idx = pictures.indexOf(v)
        if (idx < 0 || idx >= images.size) return
        if (images[idx] == null) return

        val uri = FileProvider.getUriForFile(
            requireContext(), SmartBirdsApplication.FILES_AUTHORITY,
            File(images[idx]!!.path!!)
        )
        val intent =
            Intent(INTENT_VIEW_PICTURE).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "image/jpg")
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun isNewEntry(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    open fun setReadOnly(readOnly: Boolean) {
        this.readOnly = readOnly
        updateTakePicture()
    }

    open class ImageStruct() : Parcelable {
        var fileName: String? = null
        var path: String? = null
        var uri: Uri? = null

        constructor(parcel: Parcel) : this() {
            fileName = parcel.readString()
            path = parcel.readString()
            uri = parcel.readParcelable(Uri::class.java.classLoader)
        }

        constructor(fileName: String, path: String, uri: Uri) : this() {
            this.fileName = fileName
            this.path = path
            this.uri = uri
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(fileName)
            parcel.writeString(path)
            parcel.writeParcelable(uri, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Creator<ImageStruct> {
            override fun createFromParcel(parcel: Parcel): ImageStruct {
                return ImageStruct(parcel)
            }

            override fun newArray(size: Int): Array<ImageStruct?> {
                return arrayOfNulls(size)
            }
        }
    }
}