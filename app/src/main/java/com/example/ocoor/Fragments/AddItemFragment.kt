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
import com.example.ocoor.Utils.Item
import com.example.ocoor.databinding.FragmentAddItemBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFragment : Fragment() {
    // TODO: Rename and change types of parameters


    // views
    private lateinit var binding: FragmentAddItemBinding
    private lateinit var add_item: Button;
    private lateinit var edit_text: EditText;

    // main activity
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
        println("onCreate Frag")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = requireActivity() as MainActivity

        // Inflate the layout for this fragment
        // get view binding
        binding = FragmentAddItemBinding.inflate(layoutInflater)
        add_item = binding.addItemButton
        edit_text = binding.textInputEditText

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
            var text = edit_text.text.toString()
            if (text == "") {
                Toast.makeText(mainActivity, "You did not enter a username", Toast.LENGTH_SHORT).show()
            }
            else{
                mainActivity.mItemViewModel.addItem(Item(id=0, status="False", itemText=text))
                edit_text.text.clear()
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddItemFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}