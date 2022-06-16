package Authentication

import FireBase.FireBaseUtil
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.SingletonHolder
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginGoogle(val context: Context) {
    companion object : SingletonHolder<LoginGoogle, Context>(::LoginGoogle)
    val mainActivity = context as MainActivity
    private var gAuth = mainActivity.gAuth
    var REQ_ONE_TAP = 101

    // start signin
    fun signIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("967690084390-rtcfpcopo2q85hd7qle05ttbj43bmm6j.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(mainActivity, gso)

        // init intent
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(mainActivity, signInIntent, REQ_ONE_TAP, null)
    }

    // signout
    fun signOut() {
        gAuth.signOut()
    }

    // signin process
    fun firebaseAuthWithGoogle(idToken: String) {
        // beeing called during the intent
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        gAuth.signInWithCredential(credential)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    // store logged user
                    mainActivity.userID = gAuth.currentUser?.email
                    Log.i("login", "Logging in User: ${mainActivity.userID}")

                    // init firebase
                    mainActivity.fireBaseUtil = FireBaseUtil(mainActivity)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
}
