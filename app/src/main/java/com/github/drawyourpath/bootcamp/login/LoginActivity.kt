package com.github.drawyourpath.bootcamp.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.authentication.Auth
import com.github.drawyourpath.bootcamp.authentication.FirebaseAuth
import com.github.drawyourpath.bootcamp.mainpage.MainActivity

class LoginActivity : AppCompatActivity(R.layout.activity_login), RegisterActivityListener,
    LoginActivityListener {
    private val viewModel: LoginViewModel by viewModels()

    private val auth: Auth = FirebaseAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth.onActivityCreate(this, savedInstanceState)

        if (savedInstanceState == null) {
            showRegisterUI()
        }

        viewModel.setViewListener { view ->
            when (view) {
                ELoginView.Register -> showRegisterUI()
                ELoginView.Login -> showLoginUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // If a user is available now, it was restored from keychain.
        if (FirebaseAuth.getUser() != null) {
            openMainMenu()
        }

        else {
            auth.launchOneTapGoogleSignIn(this) {
                _, error ->
                when (error) {
                    null -> openMainMenu()
                }
            }
        }
    }

    /**
     * @note The non-deprecated way doesn't catch results of Google Sign-In.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        auth.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun showLoginUI() {
        switchFragment<LoginActions>()
    }

    private fun showRegisterUI() {
        switchFragment<RegisterActions>()
    }

    private inline fun <reified T : Fragment> switchFragment() {
        supportFragmentManager.commit {
            //setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<T>(R.id.fragment_container_view)
        }
    }

    private fun openAccountRegistration() {
        // TODO: Launch account registration activity.
    }

    private fun openMainMenu() {
        val mainMenuIntent = Intent(this, MainActivity::class.java)
        this.startActivity(mainMenuIntent)
    }

    private fun showError(error: java.lang.Exception) {
        Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    override fun registerWithGoogle() {
        auth.registerWithGoogle(this) { _, error ->
            when (error) {
                null -> openAccountRegistration()
                else -> showError(error)
            }
        }
    }

    override fun loginWithGoogle() {
        auth.loginWithGoogle(this) { _, error ->
            when (error) {
                null -> openMainMenu()
                else -> showError(error)
            }
        }
    }
}