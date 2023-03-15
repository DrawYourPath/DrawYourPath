package com.epfl.drawyourpath.login

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
import com.epfl.drawyourpath.authentication.Auth
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.mainpage.MainActivity
import com.epfl.drawyourpath.userProfileCreation.UserProfileCreationActivity

const val USE_MOCK_AUTH_KEY             = "useMockAuth"
const val MOCK_AUTH_FAIL                = "useMockAuthFailing"
const val RESTORE_USER_IN_KEYCHAIN      = "restoreUserInKeychain"
const val ENABLE_ONETAP_SIGNIN          = "enableOneTapSignIn"

const val LOG_LOGIN_KEY = "DYP_Login"

abstract class LoginActivityFragment(@LayoutRes layout: Int) : Fragment(layout) {
    protected val viewModel: LoginViewModel by activityViewModels()
    protected inline fun <reified T> getLoginActivity(): T {
        return activity as T
    }
}

/**
 * Class used to display an authentication UI to the user and perform the auth
 * operations through the Auth object.
 */
class LoginActivity : AppCompatActivity(R.layout.activity_login), RegisterActivityListener,
    LoginActivityListener {
    private val viewModel: LoginViewModel by viewModels()

    // The auth object used to authenticate the user.
    private lateinit var auth: Auth

    private var useOneTapSignIn: Boolean = false;
    private var restoreUserFromKeychain: Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        useOneTapSignIn         = intent.getBooleanExtra(ENABLE_ONETAP_SIGNIN,     useOneTapSignIn);
        restoreUserFromKeychain = intent.getBooleanExtra(RESTORE_USER_IN_KEYCHAIN, restoreUserFromKeychain)

        val useMockAuthProvider = intent.getBooleanExtra(USE_MOCK_AUTH_KEY, false)
        auth = when (useMockAuthProvider) {
            true  -> MockAuth(
                failing          = intent.getBooleanExtra(MOCK_AUTH_FAIL, false),
                userInKeyChain   = restoreUserFromKeychain,
                withOneTapSignIn = useOneTapSignIn
            )
            false -> FirebaseAuth()
        }

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
            auth.launchOneTapGoogleSignIn(this) {
                _, error ->
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
            //setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<T>(R.id.fragment_container_view)
        }
    }

    private fun openAccountRegistration() {

        val registrationScreen = Intent(this, UserProfileCreationActivity::class.java)
        this.startActivity(registrationScreen)
    }

    private fun openMainMenu() {
        val mainMenuIntent = Intent(this, MainActivity::class.java)
        this.startActivity(mainMenuIntent)
    }

    private fun showError(error: java.lang.Exception) {
        Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun onRegistrationResult(user: User?, error: Exception?) {
        when (error) {
            null -> openAccountRegistration()
            else -> showError(error)
        }
    }

    private fun onLoginResult(user: User?, error: Exception?) {
        when (error) {
            null -> openMainMenu()
            else -> showError(error)
        }
    }

    override fun registerWithGoogle() {
        auth.registerWithGoogle(this) { user, error -> onRegistrationResult(user, error) }
    }

    override fun registerAnonymously() {
        auth.loginAnonymously { user, error -> onRegistrationResult(user, error) }
    }

    override fun loginWithGoogle() {
        auth.loginWithGoogle(this) { user, error -> onLoginResult(user, error) }
    }

    override fun loginWithEmailAndPassword(email: String, password: String) {
        auth.loginWithEmail(email, password) { user, error -> onLoginResult(user, error) }
    }

    override fun registerWithEmailAndPassword(email: String, password: String) {
        auth.registerWithEmail(email, password) { user, error -> onRegistrationResult(user, error) }
    }
}