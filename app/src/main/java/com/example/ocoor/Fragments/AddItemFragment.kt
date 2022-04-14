package com.example.ocoor.Fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.ocoor.MainActivity
import com.example.ocoor.Units.BaseUnit
import com.example.ocoor.Utils.Item
import com.example.ocoor.databinding.FragmentAddItemBinding
import java.util.ArrayList
import java.util.regex.Pattern

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
    var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        println("Create AddItem Fragment")
        // Inflate the layout for this fragment
        // get view binding
        binding = FragmentAddItemBinding.inflate(layoutInflater)
        add_item = binding.addItemButton
        edit_text = binding.textInputEditText

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
                val textSeq = text.split(" ").toTypedArray()
                //val textSeq = pattern.split(text).toTypedArray()
                mainActivity.text2Item(textSeq, itemID=itemID)
                edit_text.text.clear()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.textInputEditText.setText(initText)
    }
}