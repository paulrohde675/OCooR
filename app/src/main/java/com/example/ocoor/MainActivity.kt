package com.example.ocoor


import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ocoor.databinding.MainActivityBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var button_capture:Button;
    lateinit var button_copy:Button;
    lateinit var message:TextView;
    private lateinit var binding: MainActivityBinding
    lateinit var bitmap: Bitmap

    private val GALLERY_REQUEST_CODE = 1234
    private val REQUEST_IMAGE_CAPTURE = 1

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
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        button_capture = binding.buttonCapture
        button_copy = binding.buttonCopy
        message = binding.message

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        button_capture.setOnClickListener{
            //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this@MainActivity)
            takePicture()
        }

        button_copy.setOnClickListener {
            //var scanned_text:String = message.text.toString()
            //copyToClipBoard(scanned_text)
            pickFromGallery()
        }

    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No Camera found")
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->{
                println("test1")
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) run {
                    println("Result ok")
                    var resultUri: Uri = result.getUri()
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    getTextFromImage(bitmap)
                } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop Error: ${result.error}")
                }
            }

            REQUEST_IMAGE_CAPTURE -> {
                println("REQUEST_IMAGE_CAPTURE")
                if (resultCode == RESULT_OK){
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val uri:Uri = getImageUri(this, imageBitmap)
                        launchImageCrop(uri)
                } else {
                    Log.e(TAG, "Image selection Error")
                }
            }

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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 1000, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun launchImageCrop(uri: Uri){
        println("test")
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }

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
                // [END get_text]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
        processTextBlock(result)
    }

    private fun processTextBlock(result: Text) {
        // [START mlkit_process_text_block]
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
        // [END mlkit_process_text_block]
    }


    //    binding.message.setText(stringBuilder.toString())
     //   button_capture.setText("Retake")
    //   button_copy.visibility = View.VISIBLE



    private fun copyToClipBoard(text:String){
        val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip:ClipData = ClipData.newPlainText("Copied data", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }
}


