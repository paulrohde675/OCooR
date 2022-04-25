package com.example.ocoor.Fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.databinding.FragmentAddItemBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.active_recycler_view_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddListFragment: Fragment() {

    // views
    lateinit var binding: FragmentAddItemBinding
    lateinit var add_item: Button;
    lateinit var edit_text: EditText;

    // main activity
    lateinit var mainActivity: MainActivity

    // variables
    var initText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("   ------- > on create addItem")

        mainActivity = requireActivity() as MainActivity

        // Inflate the layout for this fragment
        // get view binding
        binding = FragmentAddItemBinding.inflate(layoutInflater)
        add_item = binding.addItemButton
        edit_text = binding.textInputEditText

        // Handel the Enter Key
        edit_text.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Enter triggers Add Item Button
                add_item.performClick()

                return@OnKeyListener true
            }
            false
        })

        // set clickListener
        setListClickListener()

        /*
        mainActivity.settingViewModel.readAllData.observe(this){

            val itemListAdapter = mainActivity.listOfListsFragment.itemListAdapter
            val itemList = itemListAdapter.itemListOfLists.filter { itemList -> itemList.id == it.selected_list_id }.getOrNull(0)

            println("ItemList - Test 1 ")
            if(itemList != null){
                println("ItemList - Test 2 ")
                val position = itemListAdapter.itemListOfLists.indexOf(itemList)
                val itemListView = mainActivity.listOfListsFragment.itemListRecyclerView[position]
                itemListAdapter.deactivateItemFrame()
                itemListAdapter.activateItemFrame(itemListView as MaterialCardView)
            }
        }
        */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("on create view")
        return binding.root
    }

    fun setListClickListener(){

        println("list click listener")

        binding.addItemButton.setOnClickListener {
            val text = edit_text.text.toString()

            // check if string is empty
            if (text.isNullOrBlank()) {
                Toast.makeText(mainActivity, "You did not enter a listname", Toast.LENGTH_SHORT).show()
            } // else: add item
            else{
                val textSeq = text.trim()
                val itemList = ItemList(0, textSeq)

                // Dispatch to add a new list and add the list id to settings data
                mainActivity.lifecycleScope.launch (Dispatchers.IO) {
                    var id = mainActivity.mItemListViewModel.addItemListNoCR(itemList)
                    val settings =  mainActivity.settingViewModel.readAllData.value
                    if(settings != null){
                        println("itemList ID:  ${id}")
                        settings.selected_list_id = id
                        mainActivity.settingViewModel.updateSettings(settings)
                    }
                }

                edit_text.text.clear()
                initText = ""

                // Move back to main Fragment
                mainActivity.onBackPressed();
                mainActivity.onBackPressed();
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // open up keyboard
        edit_text.requestFocus()

        if (edit_text.requestFocus()) {
            val imm = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            val isShowing = imm.showSoftInput(edit_text, InputMethodManager.SHOW_IMPLICIT)
            if (!isShowing){
                imm.showSoftInput(edit_text, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.textInputEditText.setText(initText)
        println("Set inactive rv invisible")
    }

    override fun onStop() {
        super.onStop()

        // Set the inactive recylerView visible if AddItems is closed
        val layout : RecyclerView = mainActivity.findViewById(R.id.rv_list_of_list)
        layout.visibility = View.VISIBLE
    }
}