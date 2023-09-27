package com.upnvjt.trashcare.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.ui.auth.AuthViewPagerAdapter
import com.upnvjt.trashcare.ui.main.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToInt


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun ImageView.glide(url: String) {
    Glide.with(this).load(url).into(this)
}

fun Double.toPrice(): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("IDR")

    return format.format(this.roundToInt())
}

fun Fragment.hideBottomNavView() {
    val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.menuBottom)
    val frameLayout: FrameLayout = (activity as MainActivity).findViewById(R.id.frameLayout)
    val fab: FloatingActionButton = (activity as MainActivity).findViewById(R.id.taCycleButton)

    appBar.visibility = View.GONE
    frameLayout.visibility = View.GONE
    fab.visibility = View.GONE
}

fun Fragment.showBottomNavView() {
    val appBar: BottomAppBar = (activity as MainActivity).findViewById(R.id.menuBottom)
    val frameLayout: FrameLayout = (activity as MainActivity).findViewById(R.id.frameLayout)
    val fab: FloatingActionButton = (activity as MainActivity).findViewById(R.id.taCycleButton)

    appBar.visibility = View.VISIBLE
    frameLayout.visibility = View.VISIBLE
    fab.visibility = View.VISIBLE
}

@SuppressLint("InflateParams")
fun Fragment.setUpForgotPasswordDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val etEmail = view.findViewById<EditText>(R.id.etEmailReset)
    val btnCancel = view.findViewById<Button>(R.id.btnCancelReset)
    val btnSend = view.findViewById<Button>(R.id.btnReset)

    btnSend.setOnClickListener {
        val email = etEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}

@SuppressLint("InflateParams", "MissingInflatedId")
fun Fragment.pickPhoto(
    onCameraClick: () -> Unit?,
    onGalleryClick: () -> Unit?
) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.pick_photo_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.BOTTOM)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val btnCamera = view.findViewById<Button>(R.id.btnCamera)
    val btnGallery = view.findViewById<Button>(R.id.btnGallery)

    btnCamera.setOnClickListener {
        onCameraClick()
        dialog.dismiss()
    }

    btnGallery.setOnClickListener {
        onGalleryClick()
        dialog.dismiss()
    }
}

@SuppressLint("InflateParams", "MissingInflatedId")
fun Activity.pickPhoto(
    onCameraClick: () -> Unit?,
    onGalleryClick: () -> Unit?
) {
    val dialog = Dialog(this, android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.pick_photo_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.BOTTOM)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val btnCamera = view.findViewById<Button>(R.id.btnCamera)
    val btnGallery = view.findViewById<Button>(R.id.btnGallery)

    btnCamera.setOnClickListener {
        onCameraClick()
        dialog.dismiss()
    }

    btnGallery.setOnClickListener {
        onGalleryClick()
        dialog.dismiss()
    }
}


@SuppressLint("InflateParams", "MissingInflatedId")
fun Fragment.filterProductDialog(
    hargaTerendah: () -> Unit?,
    hargaTertinggi: () -> Unit?
) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.filter_product_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.BOTTOM)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val filterLowest = view.findViewById<Button>(R.id.filter_lowest)
    val filterHighest = view.findViewById<Button>(R.id.filter_highest)
    val dismissBtn = view.findViewById<Button>(R.id.btn_dismiss)

    filterLowest.setOnClickListener {
        hargaTerendah()
        dialog.dismiss()
    }

    filterHighest.setOnClickListener {
        hargaTertinggi()
        dialog.dismiss()
    }

    dismissBtn.setOnClickListener {
        dialog.dismiss()
    }
}

fun setUpTabLayout(tabLayout: TabLayout, viewPager: ViewPager2, adapter: AuthViewPagerAdapter) {
    tabLayout.addTab(tabLayout.newTab().setText("Login"))
    tabLayout.addTab(tabLayout.newTab().setText("Sign Up"))
    viewPager.adapter = adapter
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab != null) {
                viewPager.currentItem = tab.position
            }

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }
    })

    val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            tabLayout.selectTab(tabLayout.getTabAt(position))
        }
    }
    viewPager.registerOnPageChangeCallback(myPageChangeCallback)
}

fun EditText.string(): String {
    return text.toString()
}

fun validateEmail(email: String): Validation {
    if (email.isEmpty()) {
        return Validation.Failed("Email tidak boleh kosong")
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return Validation.Failed("Harap isi dengan email yang valid")
    }

    return Validation.Success
}

fun validatePassword(password: String, passwordConfirmation: String?): Validation {
    if (password.isEmpty()) {
        return Validation.Failed("Password tidak boleh kosong")
    }

    if (passwordConfirmation != null) {
        if (password != passwordConfirmation) {
            return Validation.Failed("Password tidak sesuai")
        }
    }

    if (password.length < 6) {
        return Validation.Failed("Password harus terdiri dari 6 huruf atau lebih")
    }

    return Validation.Success
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

private const val MAXIMAL_SIZE = 1000000
fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun SearchView.onTextSubmit(onSubmit: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                onSubmit(it)
                clearFocus()
            }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}

@SuppressLint("QueryPermissionsNeeded")
fun Fragment.setUpCamera(): Camera {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    var photoPath: String? = null
    activity?.let { activity ->
        intent.resolveActivity(activity.packageManager)
        createTemporaryFile(requireActivity()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.upnvjt.trashcare.ui.main.commerce",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }
    return Camera(photoPath!!, intent)
}

@SuppressLint("QueryPermissionsNeeded")
fun Activity.setUpCamera(): Camera {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    var photoPath: String?
    this.let { activity ->
        intent.resolveActivity(activity.packageManager)
        createTemporaryFile(this).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.upnvjt.trashcare.ui.main.commerce",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }
    return Camera(photoPath!!, intent)
}

fun setUpGallery(): Intent {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "image/*"
    return Intent.createChooser(intent, "Choose a Picture")
}

fun showPermissionSettingsAlert(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Grant Permission")
    builder.setMessage("Izinkan aplikasi untuk mengakses kamera untuk melanjutkan")
    builder.setPositiveButton("Allow") { _, _ ->
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
    builder.setNeutralButton("Deny") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

fun Fragment.permissionLaunch(arrayPermission: Array<String>, permissionGranted: (Boolean) -> Unit) {
    val permission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isGranted = permissions[Manifest.permission.CAMERA] ?: false
            val readData = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (isGranted && readData) {
                permissionGranted(true)
            }else {
                permissionGranted(false)
            }
        }

    permission.launch(arrayPermission)
}

data class Camera(
    val path: String,
    val intent: Intent
)