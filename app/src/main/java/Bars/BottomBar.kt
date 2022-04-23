package Bars

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.ocoor.Fragments.AddItemFragment
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.SingletonHolder
import com.example.ocoor.Utils.SpeechRecognizerModule
import kotlinx.android.synthetic.main.main_activity.*

class BottomBar(val context: Context) {
    val mainActivity = context as MainActivity


    companion object : SingletonHolder<BottomBar, Context>(::BottomBar)


    fun setBottomBarMainScreen(){
        mainActivity.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.button_cam -> {
                    // Handle cam icon press
                    // button to take picture witch camera

                    // get permission to sue camera
                    if(ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        mainActivity.requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                    mainActivity.onLaunchCamera()

                    Toast.makeText(mainActivity, "Cam Button", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.button_scan -> {
                    // Handle scan icon press
                    mainActivity.pickFromGallery()
                    Toast.makeText(mainActivity, "Scan Button", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.button_mic -> {
                    // Handle speech to text
                    val s2t = SpeechRecognizerModule(mainActivity)
                    s2t.askSpeechInput()

                    Toast.makeText(mainActivity, "Speech to Text", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }


        // floating action button in bottom bar
        mainActivity.button_add.setOnClickListener(){
            mainActivity.addItemFragment.initText = ""
            mainActivity.addItemFragment.itemID = 0


            //open up add_item_fragment (if not already open)
            if(mainActivity.getSupportFragmentManager().findFragmentByTag(mainActivity.ADD_ITEM_TAG) !is AddItemFragment){
                mainActivity.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_add_itemd, mainActivity.addItemFragment, mainActivity.ADD_ITEM_TAG)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

}