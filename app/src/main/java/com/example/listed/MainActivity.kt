package com.example.ocoor


//import java.util.*

import Bars.BottomBar
import Bars.TopBar
import android.annotation.SuppressLint
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources.Theme
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.listed.Fragments.ListOfListsFragment
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.Fragments.ActiveRecyclerViewFragment
import com.example.ocoor.Fragments.AddItemFragment
import com.example.ocoor.Fragments.AddListFragment
import com.example.ocoor.Units.BaseUnit
import com.example.ocoor.Utils.*
import com.example.ocoor.databinding.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    // views
    lateinit var binding: MainActivityBinding
    lateinit var binding_main_frag:MainFragmentBinding
    lateinit var binding_add_item_frag:FragmentAddItemBinding
    lateinit var binding_active_recyler_view_frag:ActiveRecyclerViewFragmentBinding
    lateinit var list_of_lists_frag:FragmentListOfListsBinding

    lateinit var button_add: FloatingActionButton;
    lateinit var itemRecyclerView:RecyclerView;

    // fragments
    lateinit var addItemFragment:AddItemFragment
    lateinit var addListFragment: AddListFragment
    lateinit var activeRecyclerViewFragment:ActiveRecyclerViewFragment
    lateinit var listOfListsFragment:ListOfListsFragment

    // variables
    lateinit var bitmap: Bitmap
    var scanned_text:String = ""
    //var selectedListId:Int? = null

    // intent codes
    private val GALLERY_REQUEST_CODE = 1234
    //private val REQUEST_IMAGE_CAPTURE = 1
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034

    val APP_TAG = "MyCustomApp"
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    // adapter
    //lateinit var itemAdapter:ItemAdapter

    // data_base
    lateinit var mItemViewModel: ItemViewModel
    lateinit var mItemListViewModel: ItemListViewModel
    lateinit var settingViewModel: SettingViewModel

    // tags
    val ADD_ITEM_TAG = "ADD_ITEM_TAG"
    val ADD_LIST_TAG = "ADD_LIST_TAG"
    val MOD_ITEM_TAG = "MOD_ITEM_TAG"
    val LIST_OF_LISTS_TAG = "LIST_OF_LISTS_TAG"

    // colors
    var myColor : Int = Color.WHITE

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

        // get colors from theme
        val typedValue = TypedValue()
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true)
        myColor = typedValue.data


        // get view binding
        binding = MainActivityBinding.inflate(layoutInflater)
        binding_main_frag = MainFragmentBinding.inflate(layoutInflater)
        binding_add_item_frag = FragmentAddItemBinding.inflate(layoutInflater)
        binding_active_recyler_view_frag = ActiveRecyclerViewFragmentBinding.inflate(layoutInflater)
        list_of_lists_frag = FragmentListOfListsBinding.inflate(layoutInflater)
        button_add = binding.buttonAdd

        // init fragments
        addItemFragment = AddItemFragment()
        addListFragment = AddListFragment()
        listOfListsFragment = ListOfListsFragment()
        activeRecyclerViewFragment = ActiveRecyclerViewFragment()

        //listOfListsFragment.initAdapter()

        val view = binding.root
        setContentView(view)

        // hide actionbar
        supportActionBar?.hide()

        // get main database viewModel
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        mItemListViewModel = ViewModelProvider(this).get(ItemListViewModel::class.java)



        // Set Fragments
        //-----------------------------------------------------------
        //set active_recycler_view_fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_main, activeRecyclerViewFragment)
            commit()
        }

        // update settings recyclerView whenever the database is modified
        settingViewModel.readAllData.observe(this, Observer { settings ->
            if(mItemViewModel.readAllData.value != null){
                activeRecyclerViewFragment.itemAdapter.setData(mItemViewModel.readAllData.value!!.filter {item -> item.status == "False" && item.list_id == settings.selected_list_id})
                activeRecyclerViewFragment.itemAdapter.setData(mItemViewModel.readAllData.value!!.filter {item -> item.status == "False" && item.list_id == settings.selected_list_id})
            }
        })

        // update recyclerView whenever the database is modified
         mItemViewModel.readAllData.observe(this, Observer { items ->
             activeRecyclerViewFragment.itemAdapter.setData(items.filter {item -> item.status == "False" && item.list_id == settingViewModel.readAllData.value!!.selected_list_id})
        })

        // update inactive recyclerView whenever the database is modified
        mItemViewModel.readAllData.observe(this, Observer { items ->
            activeRecyclerViewFragment.inactiveItemAdapter.setData(items.filter {item -> item.status == "True" && item.list_id == settingViewModel.readAllData.value!!.selected_list_id})
        })

        // update settings recyclerView whenever the database is modified
        mItemListViewModel.readAllData.observe(this, Observer { itemLists ->
            if (itemLists.isEmpty()){
                mItemListViewModel.addItemList(ItemList(1, "My List"))

                //val setting = settingViewModel.readAllData.value
                //setting!!.selected_list_id = 1
                //settingViewModel.updateSettings(setting)
            }


            // this might cause problems
            // maybe find a different way to only update this once
            //mItemListViewModel.readAllData.removeObservers(this)
        })

        // handle incoming data from other apps
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
            intent?.action == Intent.ACTION_VIEW  -> {
                println(" ---> View Intent   <----")
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

        // init top bar
        TopBar(this).setTopBarMainScreen()

        // init bottom bar
        BottomBar(this).setBottomBarMainScreen()

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


    fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    fun copyToClipBoard(text:String){
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
            SpeechRecognizerModule(this).RQ_SPEECH_REC -> {
                val t2s_result : Array<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.toTypedArray()
                val textSeq = t2s_result?.get(0)?.split(" ")?.toTypedArray()
                println("t2s: ${t2s_result?.get(0).toString()}")
                text2Item(textSeq)
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
            val edit_text = block.text
            val textSeq = edit_text.split(" ").toTypedArray()
            text2Item(textSeq)

            /*
            val item = Item(id=0, status="False", itemText=block.text)
            for (line in block.lines) {
                println("-------")

                // Seperate string into amount -- unit -- good

                for (element in line.elements) {
                    val elementText = element.text

                    if(elementText.toFloatOrNull() != null){
                        item.amount = elementText.toFloat()
                        println("amount: $elementText (${item.amount})")
                    }
                    else if(BaseUnit.isUnit(elementText)) {
                        item.unit = elementText
                        println("unit: $elementText (${item.unit})")
                    } else {
                        item.good += "$elementText "
                        println("good: $elementText (${item.good})")
                    }
                }
            }


            // try to merge item with list
            if(!mItemViewModel.mergeItemWithList(item)){
                // else: add Item to database
                mItemViewModel.addItem(item)
            }
            */
        }

        //message.text = resultText
        // scanned_text = message.text.toString()
        // [END mlkit_process_text_block]
    }

    fun text2Item(textArrayList: Array<String>?, itemID:Int = 0){

        if (textArrayList != null) {

            var item:Item? = null
            println("New Item String: ${textArrayList.joinToString(" ") }")
            if(settingViewModel.readAllData.value != null){
                item = Item(id=itemID, itemText=textArrayList.joinToString(" "), list_id = settingViewModel.readAllData.value!!.selected_list_id)
            } else {
                Toast.makeText(this,"No List Seleted", Toast.LENGTH_SHORT).show()
                return
            }

            val splitTextList = mutableListOf<String>()
            // split amount and unit
            for (elementText in textArrayList) {
                //println("split ele: $elementText")
                val matcher: Matcher = Pattern.compile("\\d+|\\D+").matcher(elementText)
                while (matcher.find()) {
                    splitTextList.add(matcher.group().trim())
                    //println("Split: ${matcher.group()}")
                }
            }

            // check for identical units
            for (i in 0..splitTextList.lastIndex){
                /*
                if(i < splitTextList.lastIndex-1){
                    val newUnit = BaseUnit.convertEquiUnit("${splitTextList[i]} ${splitTextList[i+1]}".lowercase())
                    if(newUnit != null){
                        splitTextList[i] = newUnit
                        splitTextList[i+1] = ""
                    }
                }
                */
                //println("new unit: ${splitTextList[i]}")
                val newUnit = BaseUnit.convertEquiUnit(splitTextList[i].lowercase())
                if(newUnit != null){
                    splitTextList[i] = newUnit
                }
            }

            // sort into amount - unit - good
            for (elementText in splitTextList) {
                if(elementText.toFloatOrNull() != null){
                    item?.amount = elementText.toFloat()
                    //println("amount: $elementText (${item.amount})")
                }
                else if(BaseUnit.isUnit(elementText.lowercase())) {
                    item?.unit = elementText
                    //println("unit: $elementText (${item.unit})")
                } else {
                    item?.good += "$elementText "
                    //println("good: $elementText (${item.good})")
                }
            }

            if(itemID == 0){
                // try to merge item with list
                if(!mItemViewModel.mergeItemWithList(item!!)){
                    // else: add Item to database
                    mItemViewModel.addItem(item)
                }
            } else { // if there is an itemID it means the item should be overwritten
                mItemViewModel.addItem(item!!)
            }
        }
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

    // fetch colors from theme file
    fun fetchColorOnSecondary(): Int {
        val typedValue = TypedValue()
        val a: TypedArray = obtainStyledAttributes(typedValue.data, intArrayOf(com.google.android.material.R.attr.colorOnSecondary))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

}


