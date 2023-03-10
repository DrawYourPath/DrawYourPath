package com.github.drawyourpath.bootcamp.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

/**
 * A user object with basic authentication data.
 */
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
    /**
     * Gets the current user object.
     */
    fun getUser(): User?

    /**
     * Logs the user with Google Sign-In.
     */
    fun loginWithGoogle(activity: Activity, callback: AuthCallback)

    /**
     * Logs the user with an email and password.
     */
    fun loginWithEmail(email: String, password: String, callback: AuthCallback)

    /**
     * Logs the user anonymously.
     */
    fun loginAnonymously(callback: AuthCallback)

    /**
     * Registers the user with an email and password.
     */
    fun registerWithEmail(email: String, password: String, callback: AuthCallback)

    /**
     * Registers the user with Google Sign-In.
     */
    fun registerWithGoogle(activity: Activity, callback: AuthCallback)

    /**
     * Adds a listener to auth state changes.
     */
    fun onAuthStateChanged(callback: AuthCallback)

    /**
     * Remove the listener previously passed to onAuthStateChanged.
     */
    fun clearListener()

    /**
     * Launches a one tap sign-in.
     */
    fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback)

    /**
     * Signs out the current user.
     */
    fun signOut()

    /**
     * Utility methods to setup authentication data.
     * It only needs to be forwarded if authentication methods are called.
      */
    fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?)

    /**
     * Utility methods to forward intents result back to the provider.
     * It only needs to be forwarded if authentication methods are called.
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?)
}