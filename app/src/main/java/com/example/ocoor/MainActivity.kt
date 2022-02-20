package com.example.ocoor


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.ocoor.databinding.MainActivityBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class MainActivity : AppCompatActivity() {

    lateinit var button_capture:Button;
    lateinit var button_copy:Button;
    lateinit var message:TextView;
    private lateinit var binding: MainActivityBinding
    lateinit var bitmap: Bitmap

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
            println("Test1")
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this@MainActivity)

        }

        button_copy.setOnClickListener {
            var scanned_text:String = message.text.toString()
            copyToClipBoard(scanned_text)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("Test2")
        super.onActivityResult(requestCode, resultCode, data)
        println("Test3")
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            var result:CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK) run {
                var resultUri: Uri = result.getUri()
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                getTextFromImage(bitmap)
            }else{
                println("RESULT_NOT_OK")
            }
        }else{
            println("Wrong requestCode")
            println(requestCode)
        }
    }
    private fun getTextFromImage(bitmap:Bitmap){
        val recognizer:TextRecognizer = TextRecognizer.Builder(this@MainActivity).build()
        if(!recognizer.isOperational){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        }else {
            val frame:Frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlockSparseArray: SparseArray<TextBlock> = recognizer.detect(frame)
            val stringBuilder:StringBuilder = StringBuilder()
            for (i in 0..textBlockSparseArray.size()){
                val textBlock:TextBlock = textBlockSparseArray.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
            binding.message.setText(stringBuilder.toString())
            button_capture.setText("Retake")
            button_copy.visibility = View.VISIBLE
        }
    }

    private fun copyToClipBoard(text:String){
        val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip:ClipData = ClipData.newPlainText("Copied data", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }
}


