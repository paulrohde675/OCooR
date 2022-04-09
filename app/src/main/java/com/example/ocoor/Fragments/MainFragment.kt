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
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        // Inflate the layout for this fragment
        // get view binding
        binding = MainFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("View created")
    }
}