package com.epfl.drawyourpath.authentication

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import com.epfl.drawyourpath.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

private const val REQ_ONE_TAP = 9993
private const val REQ_GSI = 9994

class FirebaseAuth : Auth {
    private val auth = FirebaseAuth.getInstance()

    companion object {

        /**
         * Adapter for a FirebaseUser class to a User class.
         */
        private fun convertUser(user: FirebaseUser?): User? {
            if (user == null) {
                return null
            }
            return object : User {
                override fun getDisplayName(): String {
                    return user.displayName ?: ""
                }

                override fun getEmail(): String {
                    return user.email ?: ""
                }

                override fun getPhoneNumber(): String {
                    return user.phoneNumber ?: ""
                }

                override fun getPhotoUrl(): Uri? {
                    return user.photoUrl
                }

                override fun getUid(): String {
                    return user.uid
                }

                override fun isAnonymous(): Boolean {
                    return user.isAnonymous
                }
            }
        }


        /**
         * Gets the currently logged in user if any or null otherwise.
         * @return the currently logged in user if any or null otherwise.
         */
        fun getUser(): User? {
            return convertUser(FirebaseAuth.getInstance().currentUser)
        }
    }


    // Callback fired when an intent with sign-in result is received.
    private var currCallback: AuthCallback? = null

    private fun setCurrCallback(callback: AuthCallback): Boolean {
        if (currCallback != null) {
            callback(null, Exception("An operation is still pending."))
            return false
        }
        currCallback = callback
        return true
    }

    private fun consumeCurrCallback(user: User?, error: Exception?) {
        if (currCallback != null) {
            currCallback!!(user, error)
            currCallback = null
        }
    }

    override fun getUser(): User? {
        return convertUser(FirebaseAuth.getInstance().currentUser)
    }

    override fun loginWithGoogle(activity: Activity, callback: AuthCallback) {

        // If an intent is still pending, we fails the sign in attemp.
        if (!setCurrCallback(callback)) {
            return
        }


        // Creates the intent to launch the Google Sign-In flow.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.server_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, gso)

        // Launches the intent.
        client.signInIntent.also {
            activity.startActivityForResult(it, REQ_GSI)
        }
    }

    override fun loginWithEmail(email: String, password: String, callback: AuthCallback) {

        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    callback(convertUser(it.user), null)
                }
                .addOnFailureListener {
                    callback(null, it)
                }
        } catch (e: Exception) {
            callback(null, e)
        }
    }

    override fun loginAnonymously(callback: AuthCallback) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                callback(convertUser(it.user!!), null)
            }
            .addOnFailureListener {
                callback(null, it)
            }
    }

    override fun registerWithEmail(email: String, password: String, callback: AuthCallback) {
        if (email.isBlank()) {
            callback(null, Exception("The email can't be empty."))
            return
        }

        if (password.isBlank()) {
            callback(null, Exception("The password can't be emptyeee."))
            return
        }

        try {
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    callback(convertUser(it.user!!), null)
                }
                .addOnFailureListener {
                    callback(null, it)
                }
        } catch (e: Exception) {
            callback(null, e)
        }
    }

    override fun registerWithGoogle(activity: Activity, callback: AuthCallback) {
        loginWithGoogle(activity, callback)
    }


    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onAuthStateChanged(callback: AuthCallback) {
        if (authStateListener != null) {
            clearListener()
        }

        authStateListener = FirebaseAuth.AuthStateListener {
            callback(getUser(), null)
        }

        auth.addAuthStateListener(authStateListener!!)
    }

    override fun clearListener() {
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener!!)

            authStateListener = null
        }
    }

    override fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback) {

        // If an intent is still pending, we fails the sign in attemp.
        if (!setCurrCallback(callback)) {
            return
        }

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                try {
                    activity.startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    consumeCurrCallback(null, e)
                }
            }
            .addOnFailureListener(activity) { e ->
                consumeCurrCallback(null, e)
            }
    }

    override fun signOut() {
        auth.signOut()
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?) {
        // Configures the One-Tap Sign-In objects for later use.
        oneTapClient = Identity.getSignInClient(activity)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.server_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun onGoogleSignInResult(activity: Activity, data: Intent?) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            if (account != null && account.idToken != null) {
                signInWithGoogleAuthToken(activity, account.idToken!!)
            }
        } catch (e: java.lang.Exception) {
            consumeCurrCallback(null, e)
        }
    }

    private fun onOneTapSignInResult(activity: Activity, data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    signInWithGoogleAuthToken(activity, idToken)
                }
            }
        } catch (e: ApiException) {
            consumeCurrCallback(null, e)
        }
    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        // Handles responses from launched intents.
        // Note that intents from other requests might be received and should be discarded.
        when (requestCode) {
            REQ_GSI -> onGoogleSignInResult(activity, data)
            REQ_ONE_TAP -> onOneTapSignInResult(activity, data)
        }
    }


    /**
     * Signs in against the Firebase backend with an Auth Token received from
     * Google.
     */
    private fun signInWithGoogleAuthToken(activity: Activity, token: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    consumeCurrCallback(convertUser(auth.currentUser), null)
                } else {
                    consumeCurrCallback(null, task.exception)
                }
            }
    }
}