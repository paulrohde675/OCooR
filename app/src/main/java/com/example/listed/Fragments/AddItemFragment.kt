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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.databinding.FragmentAddItemBinding
import kotlinx.android.synthetic.main.active_recycler_view_fragment.*


class AddItemFragment: Fragment() {

    // views
    lateinit var binding: FragmentAddItemBinding
    lateinit var add_item: Button;
    lateinit var edit_text: EditText;

    // main activity
    lateinit var mainActivity: MainActivity

    // variables
    var initText = ""
    var itemID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        println("Create AddItem Fragment")
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // open up keyboard
        edit_text.requestFocus()

        if (edit_text.requestFocus()) {
            println("Req Context")
            val imm = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //imm.toggleSoftInput(0, 0);
            //imm.showSoftInput(edit_text, InputMethodManager.SHOW_IMPLICIT)
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
            val isShowing = imm.showSoftInput(edit_text, InputMethodManager.SHOW_IMPLICIT)
            if (!isShowing){
                imm.showSoftInput(edit_text, InputMethodManager.SHOW_IMPLICIT)
            }

        }

        binding.addItemButton.setOnClickListener {
            val text = edit_text.text.toString()

            // check if string is empty
            if (text.isNullOrBlank()) {
                Toast.makeText(mainActivity, "You did not enter a username", Toast.LENGTH_SHORT).show()
            } // else: add item
            else{
                val textSeq = text.trim().split(" ").toTypedArray()
                //val textSeq = pattern.split(text).toTypedArray()
                mainActivity.text2Item(textSeq, itemID=itemID)
                edit_text.text.clear()
                initText = ""

                // deactivate selected item frames
                mainActivity.activeRecyclerViewFragment.itemAdapter.deactivateItemFrame()
                itemID = 0
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.textInputEditText.setText(initText)
        println("Set inactive rv invisible")

        // Set the inactive recylerView invisible if AddItems is opend
        val layout : RecyclerView = mainActivity.findViewById(R.id.inactiveRecyclerView)
        layout.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()

        // Set the inactive recylerView visible if AddItems is closed
        val layout : RecyclerView = mainActivity.findViewById(R.id.inactiveRecyclerView)
        layout.visibility = View.VISIBLE

        // deactivate selected item frames
        mainActivity.activeRecyclerViewFragment.itemAdapter.deactivateItemFrame()
        itemID = 0
    }

}