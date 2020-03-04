package app.vdh.org.vdhapp.core.helpers

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

class AuthHelper {

    companion object {

        val UID: String = FirebaseAuth.getInstance().uid ?: UUID.randomUUID().toString()

        fun Fragment.signIn(requestCode: Int) {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .enableAnonymousUsersAutoUpgrade()
                            .build(), requestCode
            )
        }

        fun signInAnonymously(onSuccess: () -> Unit) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            onSuccess()
                        }
                    }
        }

        fun onAuthResult(
            data: Intent?,
            resultCode: Int,
            onAuthSuccess: (FirebaseUser) -> Unit
        ) {

            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    Log.d("SettingsFragment", "Authentication success $user")
                    onAuthSuccess(user)
                }
            } else {
                Log.e("SettingsFragment", "Authentication error ${response?.error}")
                if (response?.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                    val nonAnonymousCredential = response.credentialForLinking
                    nonAnonymousCredential?.let {
                        FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                    }
                }
            }
        }

        fun Fragment.signOut(onSignOutComplete: () -> Unit) {
            AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnCompleteListener {
                        onSignOutComplete()
                    }
        }
    }
}