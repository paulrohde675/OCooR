package com.example.ocoor.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.databinding.FragmentAddItemBinding
import com.example.ocoor.databinding.MainFragmentBinding
import com.example.ocoor.ui.main.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    // views
    private lateinit var binding: MainFragmentBinding
    lateinit var button_capture: Button;
    lateinit var button_gallary: Button;
    lateinit var button_copy: Button;
    lateinit var button_share: Button;
    lateinit var button_add: FloatingActionButton;
    //private lateinit var viewModel: MainViewModel

    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        // get view binding
        binding = MainFragmentBinding.inflate(layoutInflater)
        println("onCreateView")

        // get views from binding
        button_capture = binding.buttonCapture
        button_copy = binding.buttonCopy
        button_gallary = binding.buttonGallery
        button_share = binding.buttonShare
        button_add = binding.buttonAdd

        button_add.setOnClickListener(){
            findNavController().navigate(R.id.action_mainFragment_to_AddItemFragment)
        }

        mainActivity = requireActivity() as MainActivity

        // buttons
        //------------------------------------------------------------------------------------------
        // button to take picture witch camera
        button_capture.setOnClickListener{

            // get permission to sue camera
            if(ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                mainActivity.requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            mainActivity.onLaunchCamera()
        }
        // button to pick pickture from galery
        button_gallary.setOnClickListener {
            mainActivity.pickFromGallery()
        }
        // button to copy the scanned text to clipboard
        button_copy.setOnClickListener {
            mainActivity.copyToClipBoard(mainActivity.scanned_text)
        }

        // button to share scanned text to other apps
        button_share.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mainActivity.scanned_text)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }


        return binding.root
    }
}