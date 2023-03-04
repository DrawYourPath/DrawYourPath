package com.github.drawyourpath.bootcamp.authentication.provider

import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class GoogleSignInAuthProvider(activity: AppCompatActivity) : FirebaseAuthProvider() {

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        val result = it
        firebaseUserCallback?.let {
            it(makeFirebaseUserAdapter(FirebaseAuth.getInstance().currentUser),
                result.idpResponse?.error)
        }
    }

    override fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }
}