package com.example.listed.Bars

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.listed.Fragments.ListOfListsFragment
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.SingletonHolder
import kotlinx.android.synthetic.main.main_activity.*

class TopBar(context: Context) {
    val mainActivity = context as MainActivity


    companion object : SingletonHolder<TopBar, Context>(::TopBar)

    fun setTopBarMainScreen() {
        // top app bar buttons
        mainActivity.topAppBar.setNavigationOnClickListener{

            // If addItem Fragment is open, close it
            if (mainActivity.supportFragmentManager
                    .findFragmentByTag(mainActivity.ADD_ITEM_TAG) != null
            ) {
                mainActivity.onBackPressed();
            }

            //open up add_item_fragment (if not already open)
            if(mainActivity.getSupportFragmentManager().findFragmentByTag(mainActivity.LIST_OF_LISTS_TAG) !is ListOfListsFragment){
                mainActivity.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_main, mainActivity.listOfListsFragment, mainActivity.LIST_OF_LISTS_TAG)
                    addToBackStack(null)
                    commit()
                }
            } else {
                mainActivity.onBackPressed();
            }
        }

        mainActivity.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.button_share -> {
                    // Handle share icon press
                    // button to share scanned text to other apps
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, mainActivity.scanned_text)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    mainActivity.startActivity(shareIntent)

                    Toast.makeText(mainActivity, "Share Button", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.button_copy -> {
                    // Handle copy icon press
                    // button to copy the scanned text to clipboard

                    mainActivity.copyToClipBoard(mainActivity.scanned_text)
                    Toast.makeText(mainActivity, "Copy Button", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}