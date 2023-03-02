package com.github.drawyourpath.bootcamp.authentication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.drawyourpath.bootcamp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class GoogleAuthBootcampActivity : AppCompatActivity() {

    private val authProvider: AuthProvider = GoogleSignInAuthProvider(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bootcamp_google_auth)

        val signInButton = findViewById<Button>(R.id.bc_google_signin)

        signInButton.setOnClickListener { authProvider.signIn() }

        authProvider.setUserCallback { user, error -> onSignInResult(user, error) }
    }

    override fun onDestroy() {
        authProvider.clearUserCallback()

        super.onDestroy()
    }

    private fun onSignInResult(user: AuthUser?, error: Exception?) {
        if (error != null)
        {
            setSignInText("Failed to sign in: " + error.localizedMessage)
        }
        else if (user == null)
        {
            setSignInText("Not signed.")
        }
        else
        {
            setSignInText("Signed in as " + user.getDisplayName())
        }
    }

    private fun setSignInText(text: String) {
        val signInText = findViewById<TextView>(R.id.sign_in_status)
        signInText.text = text
    }
}