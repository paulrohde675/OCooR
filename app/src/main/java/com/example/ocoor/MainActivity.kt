package com.example.ocoor


import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.Model.ItemModel
import com.example.ocoor.Utils.AppDatabase
import com.example.ocoor.Utils.User
import com.example.ocoor.databinding.MainActivityBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {

    // views
    private lateinit var binding: MainActivityBinding
    lateinit var button_capture:Button;
    lateinit var button_gallary:Button;
    lateinit var button_copy:Button;
    lateinit var button_share:Button;
    //lateinit var message:TextView;
    lateinit var itemRecyclerView:RecyclerView;

    // variables
    lateinit var bitmap: Bitmap
    var scanned_text:String = ""

    // intent codes
    private val GALLERY_REQUEST_CODE = 1234
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034

    val APP_TAG = "MyCustomApp"
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    // adapter
    lateinit var itemAdapter:ItemAdapter

    // permissions
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // success
                Log.d("RPMT", "success")
            } else {
                Log.d("RPMT", "failure")
                // failure
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get view binding
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get views from binding
        button_capture = binding.buttonCapture
        button_copy = binding.buttonCopy
        button_gallary = binding.buttonGallery
        button_share = binding.buttonShare
        //message = binding.textview
        itemRecyclerView = binding.itemRecyclerView

        // hide actionbar
        supportActionBar?.hide()

        // buttons
        //------------------------------------------------------------------------------------------
        // button to take picture witch camera
        button_capture.setOnClickListener{

            // get permission to sue camera
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            onLaunchCamera()
        }
        // button to pick pickture from galery
        button_gallary.setOnClickListener {
            pickFromGallery()
        }
        // button to copy the scanned text to clipboard
        button_copy.setOnClickListener {
            copyToClipBoard(scanned_text)
        }

        // button to share scanned text to other apps
        button_share.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, scanned_text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }

        // recycler view
        //------------------------------------------------------------------------------------------
        itemAdapter = ItemAdapter(mutableListOf())
        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = itemAdapter


        // handle incomming data from other apps
        //------------------------------------------------------------------------------------------
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
        println("Test 01")
        // Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        println("Test 02")
        val userDao = db.userDao()
        userDao.insertAll(User(1, "Annika", "Böhme"))
        val users: List<User> = userDao.getAll()
        println(users)

    }

    //----------------------------------------------------------------------------------------------
    // catch incoming data from other apps
    //----------------------------------------------------------------------------------------------
    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Update UI to reflect text being shared
            Toast.makeText(this, "Text sharing not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendImage(intent: Intent) {
        println("hadel send image")
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            launchImageCrop(it)
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // Update UI to reflect multiple images being shared
            Toast.makeText(this, "Multiple image sharing not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    //----------------------------------------------------------------------------------------------
    // button functions
    //----------------------------------------------------------------------------------------------
    @SuppressLint("QueryPermissionsNeeded")
    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {

            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
         } else {
            println("No photoFile found")
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }


    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun copyToClipBoard(text:String){
        val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip:ClipData = ClipData.newPlainText("Copied data", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    //----------------------------------------------------------------------------------------------
    // onActivityResult
    //----------------------------------------------------------------------------------------------
    // handels action after an activity returned a result
    // activities are (gallery, camera, cropping)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){

            // when image cropped use forward text
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->{
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) run {
                    var resultUri: Uri = result.getUri()
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    getTextFromImage(bitmap)
                } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop Error: ${result.error}")
                }
            }

            // when picture taken (with cam) start cropping
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    // by this point we have the camera photo on disk
                    val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                    val uri = Uri.fromFile( File(photoFile!!.absolutePath))
                    launchImageCrop(uri)
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    //val ivPreview: ImageView = findViewById(R.id.imageView)
                    //ivPreview.setImageBitmap(takenImage)
                } else { // Result was a failure
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
                }
            }

            // when picture choosen (from gallery) start cropping
            GALLERY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK){
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                } else {
                    Log.e(TAG, "Image selection Error")
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // cropping
    //----------------------------------------------------------------------------------------------
    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            //.setAutoZoomEnabled(true)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setBorderCornerColor(Color.RED)
            //.setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }


    //----------------------------------------------------------------------------------------------
    // OCR - functions
    //----------------------------------------------------------------------------------------------
    private fun getTextFromImage(bitmap:Bitmap) {

        println("getTextFromImage")
        val image = InputImage.fromBitmap(bitmap, 0)

        //val recognizer:TextRecognizer = TextRecognizer.Builder(this).build()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        println("recognizer build")

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_text]
                for (block in visionText.textBlocks) {
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text

                    for (line in block.lines) {
                        // ...
                        for (element in line.elements) {
                            // ...
                        }
                    }
                }
                processTextBlock(visionText)
                // [END get_text]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        // [END run_detector]

    }

    private fun processTextBlock(result: Text) {
        // [START mlkit_process_text_block]
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox

            // add new item to recyclerView from text block
            val newItem:ItemModel = ItemModel()
            newItem.itemText = block.text
            itemAdapter.addItem(newItem)

            println("-------")
            println("Block")
            println(blockText)
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                println("-------")
                println("Line")
                println(lineText)
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }

        //message.text = resultText
        // scanned_text = message.text.toString()
        // [END mlkit_process_text_block]
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { recipeId ->
                Uri.parse("content://com.recipe_app/recipe/")
                    .buildUpon()
                    .appendPath(recipeId)
                    .build().also { appData ->
                        //showRecipe(appData)
                    }
            }
        }
    }

}


