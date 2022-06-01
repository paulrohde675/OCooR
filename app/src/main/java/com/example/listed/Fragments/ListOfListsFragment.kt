package com.example.listed.Fragments

import com.example.listed.Bars.BottomBar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Adapter.ItemListAdapter
import com.example.ocoor.MainActivity
import com.example.ocoor.databinding.FragmentListOfListsBinding

class ListOfListsFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentListOfListsBinding

    lateinit var itemListRecyclerView: RecyclerView;
    lateinit var itemListAdapter: ItemListAdapter

    // main activity
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = requireActivity() as MainActivity

        binding = FragmentListOfListsBinding.inflate(layoutInflater)

        itemListAdapter = ItemListAdapter(mutableListOf(), mainActivity.mItemListViewModel, mainActivity)
        itemListRecyclerView = binding.rvListOfList
        itemListRecyclerView.layoutManager = LinearLayoutManager(context)
        itemListRecyclerView.adapter = itemListAdapter

        // update listOfLists recyclerView whenever the database is modified
        mainActivity.mItemListViewModel.readAllData.observe(mainActivity) { items ->
            itemListAdapter.setData(items)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // switch bars to list setup
        BottomBar(mainActivity).setBottomBarListScreen()
    }

    override fun onStop() {
        super.onStop()

        // switch bars to list setup
        BottomBar(mainActivity).setBottomBarMainScreen()
    }

}