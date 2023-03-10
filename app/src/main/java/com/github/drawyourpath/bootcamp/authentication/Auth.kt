package com.github.drawyourpath.bootcamp.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

interface User {
    fun getDisplayName(): String
    fun getEmail(): String
    fun getPhoneNumber(): String
    fun getPhotoUrl(): Uri?
    fun getUid(): String
    fun isAnonymous(): Boolean
}

typealias AuthCallback = (user: User?, error: Exception?) -> Unit

interface Auth {
    fun getUser(): User?

    fun loginWithGoogle(activity: Activity, callback: AuthCallback)

    fun loginWithEmail(email: String, password: String, callback: AuthCallback)

    fun loginAnonymously(callback: AuthCallback)

    fun registerWithEmail(email: String, password: String, callback: AuthCallback)

    fun registerWithGoogle(activity: Activity, callback: AuthCallback)

    fun onAuthStateChanged(callback: AuthCallback)

    fun removeListener(callback: AuthCallback)

    fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback)

    // Utility methods to forward intents result back to the provider.
    fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?)
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?)
}