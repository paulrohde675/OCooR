package Authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.SingletonHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class PwEmail(val context: Context) {
    companion object : SingletonHolder<PwEmail, Context>(::PwEmail)
    val mainActivity = context as MainActivity
    private var gAuth = mainActivity.gAuth


    private fun createUser(email:String, password:String) {

        gAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("createUserWithEmail:success")
                    val user = gAuth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("createUserWithEmail:failure")
                    Toast.makeText(
                        mainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }

}

