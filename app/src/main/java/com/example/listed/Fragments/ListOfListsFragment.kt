package com.example.listed.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Adapter.InactiveItemAdapter
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.Adapter.ItemListAdapter
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.databinding.ActiveRecyclerViewFragmentBinding
import com.example.ocoor.databinding.FragmentListOfListsBinding
import java.util.Observer

class ListOfListsFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentListOfListsBinding

    lateinit var itemListRecyclerView: RecyclerView;
    lateinit var itemListAdapter: ItemListAdapter

    // main activity
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //arguments?.let {
        //}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        println("-------------> Create List of Lists Frag <-------------------")
        println("")
        mainActivity = requireActivity() as MainActivity

        binding = FragmentListOfListsBinding.inflate(layoutInflater)

        itemListAdapter = ItemListAdapter(mutableListOf(), mainActivity.mItemListViewModel, mainActivity)
        itemListRecyclerView = binding.activeRecyclerView
        itemListRecyclerView.layoutManager = LinearLayoutManager(context)
        itemListRecyclerView.adapter = itemListAdapter

        // update listOfLists recyclerView whenever the database is modified
        mainActivity.mItemListViewModel.readAllData.observe(mainActivity) { items ->
            itemListAdapter.setData(items)
        }

        return binding.root
    }

    fun initAdapter(){
        mainActivity = requireActivity() as MainActivity
        itemListAdapter = ItemListAdapter(mutableListOf(), mainActivity.mItemListViewModel, mainActivity)



    }

}