package com.epfl.drawyourpath.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.*
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfileCreation.UserProfileCreationActivity

const val LOG_LOGIN_KEY = "DYP_Login"

/**
 * Base class for any fragment displayed in the login activity.
 */
abstract class LoginActivityFragment(@LayoutRes layout: Int) : Fragment(layout) {
    protected val viewModel: LoginViewModel by activityViewModels()
    protected inline fun <reified T> getLoginActivity(): T {
        return activity as T
    }
}

fun launchLoginActivity(activity: Activity) {
    val intent = Intent(activity, LoginActivity::class.java)
    activity.startActivity(intent)
}

/**
 * Class used to display an authentication UI to the user and perform the auth
 * operations through the Auth object.
 */
class LoginActivity :
    AppCompatActivity(R.layout.activity_login),
    RegisterActivityListener,
    LoginActivityListener {
    private val viewModel: LoginViewModel by viewModels()

    // The auth object used to authenticate the user.
    private lateinit var auth: Auth

    // If we should use onetap sign-in when the login activity is launched
    // Can be controlled with ENABLE_ONETAP_SIGNIN in the intent.
    private var useOneTapSignIn: Boolean = true

    // If we should restore user information from the keychain to automatically
    // log the user in.
    // Can be controlled with RESTORE_USER_IN_KEYCHAIN in the intent.
    private var restoreUserFromKeychain: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Overwrites the one tap sign-in behavior if set in the intent.
        useOneTapSignIn = intent.getBooleanExtra(ENABLE_ONETAP_SIGNIN, useOneTapSignIn)

        // Overwrites the user keychain restoration behavior if set in the intent.
        restoreUserFromKeychain =
            intent.getBooleanExtra(RESTORE_USER_IN_KEYCHAIN, restoreUserFromKeychain)

        // Creates the auth object depending on the mock data in the intent.
        auth = createAuth(intent.extras, false, restoreUserFromKeychain)

        auth.onActivityCreate(this, savedInstanceState)

        viewModel.setViewListener { view ->
            when (view) {
                ELoginView.Register -> showRegisterUI()
                ELoginView.Login -> showLoginUI()
            }
        }

        // When the user changed. i.e. signed out or signed in.
        auth.onAuthStateChanged { _, _ ->
        }

        showRegisterUI()
    }

    override fun onStart() {
        super.onStart()

        // If a user is available now, it was restored from keychain.
        if (restoreUserFromKeychain && auth.getUser() != null) {
            Log.i(LOG_LOGIN_KEY, "User restored from keychain.")
            openMainMenu()
        }

        // Otherwise, we try a one-tap sign-in.
        else if (useOneTapSignIn) {
            Log.i(LOG_LOGIN_KEY, "Attempting One-Tap Sign-In.")
            auth.launchOneTapGoogleSignIn(this) { _, error ->
                when (error) {
                    null -> openMainMenu()
                }
            }
        }
    }

    override fun onDestroy() {
        auth.clearListener()

        super.onDestroy()
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

    private inline fun <reified T : LoginActivityFragment> switchFragment() {
        supportFragmentManager.commit {
            // setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<T>(R.id.fragment_container_view)
        }
    }

    private fun openAccountRegistration() {
        val registrationScreen = Intent(this, UserProfileCreationActivity::class.java)
        this.startActivity(registrationScreen)
    }

    private fun openMainMenu() {
        val mainMenuIntent = Intent(this, MainActivity::class.java)
        mainMenuIntent.putExtra(MainActivity.EXTRA_USER_ID, auth.getUser()?.getUid())
        this.startActivity(mainMenuIntent)
    }

    private fun showError(error: java.lang.Exception) {
        Log.w("Login", "Error: ${error.localizedMessage}")
        Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun onRegistrationResult(user: User?, error: Exception?) {
        when (error) {
            null -> {
                Log.i("Login", "Registered, redirecting to main activity.")
                openAccountRegistration()
            }
            else -> showError(error)
        }
    }

    private fun onLoginResult(user: User?, error: Exception?) {
        when (error) {
            null -> {
                Log.i("Login", "Logged in, redirecting to main activity.")
                openMainMenu()
            }
            else -> showError(error)
        }
    }

    override fun registerWithGoogle() {
        Log.i("Login", "Trying to register with Google.")
        auth.registerWithGoogle(this) { user, error -> onRegistrationResult(user, error) }
    }

    override fun registerAnonymously() {
        Log.i("Login", "Trying to login anonymously.")
        auth.loginAnonymously { user, error -> onRegistrationResult(user, error) }
    }

    override fun loginWithGoogle() {
        Log.i("Login", "Trying to login with Google.")
        auth.loginWithGoogle(this) { user, error -> onLoginResult(user, error) }
    }

    override fun loginWithEmailAndPassword(email: String, password: String) {
        Log.i("Login", "Trying to login with email.")
        auth.loginWithEmail(email, password) { user, error -> onLoginResult(user, error) }
    }

    override fun registerWithEmailAndPassword(email: String, password: String) {
        Log.i("Login", "Trying to register with email.")
        auth.registerWithEmail(email, password) { user, error -> onRegistrationResult(user, error) }
    }
}
