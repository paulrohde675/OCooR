package com.example.ocoor.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Adapter.InactiveItemAdapter
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.MainActivity
import com.example.ocoor.databinding.ActiveRecyclerViewFragmentBinding
import java.util.Observer

class ActiveRecyclerViewFragment : Fragment() {

    // binding
    private lateinit var binding: ActiveRecyclerViewFragmentBinding

    lateinit var itemRecyclerView: RecyclerView;
    lateinit var inactiveItemRecyclerView: RecyclerView;
    lateinit var itemAdapter:ItemAdapter
    lateinit var inactiveItemAdapter:InactiveItemAdapter

    // main activity
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = requireActivity() as MainActivity

        binding = ActiveRecyclerViewFragmentBinding.inflate(layoutInflater)

        itemRecyclerView = binding.activeRecyclerView
        itemAdapter = ItemAdapter(mutableListOf(), mainActivity.mItemViewModel, mainActivity)
        itemRecyclerView.layoutManager = LinearLayoutManager(context)
        itemRecyclerView.adapter = itemAdapter

        inactiveItemRecyclerView = binding.inactiveRecyclerView
        inactiveItemAdapter = InactiveItemAdapter(mutableListOf(), mainActivity.mItemViewModel, mainActivity)
        inactiveItemRecyclerView.layoutManager = LinearLayoutManager(context)
        inactiveItemRecyclerView.adapter = inactiveItemAdapter

        return binding.root
    }
}