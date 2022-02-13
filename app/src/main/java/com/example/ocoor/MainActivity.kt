package com.example.ocoor

import com.theartofdev.edmodo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ocoor.ui.main.MainFragment
import com.example.ocoor.databinding.MainActivityBinding
import com.fenchtose.nocropper.Cropper
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var butten_capture:Button;
    lateinit var butten_copy:Button;
    private lateinit var binding: MainActivityBinding

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

        butten_capture = binding.buttonCapture
        butten_copy = binding.buttonCopy

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        butten_capture.setOnClickListener{
            Cropper
        }
    }
}


