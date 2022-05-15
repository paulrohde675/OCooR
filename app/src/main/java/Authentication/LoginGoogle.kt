package Authentication

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


    //private lateinit var signInRequest: BeginSignInRequest

    var REQ_ONE_TAP = 101

    // signin
    fun signIn() {
        val signInIntent = mainActivity.googleSignInClient.signInIntent
        startActivityForResult(mainActivity, signInIntent, REQ_ONE_TAP, null)
    }

    fun signOn() {
        gAuth.signOut()
    }


    // [START auth_with_google]
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        gAuth.signInWithCredential(credential)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = gAuth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    fun oneClickSetup() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("967690084390-rtcfpcopo2q85hd7qle05ttbj43bmm6j.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mainActivity.googleSignInClient = GoogleSignIn.getClient(mainActivity, gso)

        /*

        mainActivity.oneTapClient = Identity.getSignInClient(mainActivity)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("967690084390-v86o6atgodaiomvfsp2tj1vjuvq0arji.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

        mainActivity.oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(mainActivity) { result ->
                try {
                    startIntentSenderForResult(
                        mainActivity,
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(mainActivity) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }

         */

    }

}
