package com.example.ocoor.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ocoor.R
import com.example.ocoor.databinding.FragmentAddItemBinding
import com.example.ocoor.databinding.MainFragmentBinding
import com.example.ocoor.ui.main.MainViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    // views
    private lateinit var binding: MainFragmentBinding
    //private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        // get view binding
        binding = MainFragmentBinding.inflate(layoutInflater)
        println("onCreateView")
        binding.buttonAdd.setOnClickListener(){
            println("klick")
            findNavController().navigate(R.id.action_mainFragment_to_AddItemFragment)
        }

        return binding.root
    }
}