package com.epfl.drawyourpath.authentication

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

        fun getUser(): User? {
            return convertUser(FirebaseAuth.getInstance().currentUser)
        }
    }

    private var currCallback: AuthCallback? = null

    private fun setCurrCallback(callback: AuthCallback): Boolean
    {
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
        if (!setCurrCallback(callback)) {
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.server_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, gso)

        client.signInIntent.also {
            activity.startActivityForResult(it, REQ_GSI)
        }
    }

    override fun loginWithEmail(email: String, password: String, callback: AuthCallback) {
        TODO("Not yet implemented")
    }

    override fun loginAnonymously(callback: AuthCallback) {
        TODO("Not yet implemented")
    }

    override fun registerWithEmail(email: String, password: String, callback: AuthCallback) {
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {res ->
                callback(convertUser(res.user!!), null)
            }
            .addOnFailureListener { exc ->
                callback(null, exc)
            }
    }

    override fun registerWithGoogle(activity: Activity, callback: AuthCallback) {
        loginWithGoogle(activity, callback)
    }

    private var authStateListener: FirebaseAuth.AuthStateListener? = null;

    override fun onAuthStateChanged(callback: AuthCallback) {
        if (authStateListener != null)
        {
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
            authStateListener = null;
        }
    }

    override fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback) {
        if (!setCurrCallback(callback)) {
            return
        }

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(activity) { result ->
                try {
                    activity.startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("GSI", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(activity) { e ->
                Log.d("GSI", e.localizedMessage!!)
            }
    }

    override fun signOut() {
        auth.signOut()
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?) {
        oneTapClient = Identity.getSignInClient(activity)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.server_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun onGoogleSignInResult(activity: Activity, data: Intent?) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            if (account != null && account.idToken != null) {
                signInWithGoogleAuthToken(activity, account.idToken!!)
            }
        }
        catch (e: java.lang.Exception) {
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

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_GSI     -> onGoogleSignInResult(activity, data)
            REQ_ONE_TAP -> onOneTapSignInResult(activity, data)
        }
    }

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