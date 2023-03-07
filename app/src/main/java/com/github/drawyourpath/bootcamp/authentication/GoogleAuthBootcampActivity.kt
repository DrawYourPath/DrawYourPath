package com.github.drawyourpath.bootcamp.authentication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.authentication.provider.AuthProvider
import com.github.drawyourpath.bootcamp.authentication.provider.GoogleSignInAuthProvider
import com.github.drawyourpath.bootcamp.authentication.provider.MockAuthProvider

const val GA_USE_MOCK_AUTH_PROVIDER_KEY = "useMockAuthProvider"

class GoogleAuthBootcampActivity : AppCompatActivity() {

    private var authProvider: AuthProvider = GoogleSignInAuthProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bootcamp_google_auth)

        val signInButton = findViewById<Button>(R.id.bc_google_signin)
        val signOutButton = findViewById<Button>(R.id.bc_google_signout)

        val useMockAuthProvider = intent.getBooleanExtra(GA_USE_MOCK_AUTH_PROVIDER_KEY, false)
        if (useMockAuthProvider) {
            authProvider = MockAuthProvider()
        }

        signInButton.setOnClickListener { authProvider.signIn() }
        signOutButton.setOnClickListener { authProvider.signOut() }

        authProvider.setUserCallback { user, error -> onSignInResult(user, error) }
    }

    override fun onDestroy() {
        authProvider.clearUserCallback()

        super.onDestroy()
    }

    private fun onSignInResult(user: AuthUser?, error: Exception?) {
        if (error != null) {
            setSignInText("Failed to sign in: " + error.localizedMessage)
        } else if (user == null) {
            setSignInText("Not signed.")
        } else {
            setSignInText("Signed in as " + authProvider.getCurrentUser()!!.getDisplayName())
        }
    }

    private fun setSignInText(text: String) {
        val signInText = findViewById<TextView>(R.id.sign_in_status)
        signInText.text = text
    }
}