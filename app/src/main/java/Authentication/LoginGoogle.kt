package Authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.SingletonHolder
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth

class LoginGoogle(val context: Context) {
    companion object : SingletonHolder<LoginGoogle, Context>(::LoginGoogle)
    val mainActivity = context as MainActivity
    private var gAuth = mainActivity.gAuth

    lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    var REQ_ONE_TAP = 101

    fun oneClickSetup() {
        oneTapClient = Identity.getSignInClient(mainActivity)

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

        oneTapClient.beginSignIn(signInRequest)
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

    }

}
