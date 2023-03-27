package com.epfl.drawyourpath.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

class MockAuth(
    private val failing: Boolean = false,
    userInKeyChain: Boolean = false,
    private val withOneTapSignIn: Boolean = false,
) : Auth {
    companion object {
        val MOCK_USER = object : User {
            override fun getDisplayName(): String {
                return "Clark Kent"
            }

            override fun getEmail(): String {
                return "mockuser@mockdomain.org"
            }

            override fun getPhoneNumber(): String {
                return "+0000000000"
            }

            override fun getPhotoUrl(): Uri? {
                return Uri.parse("https://www.epfl.ch/about/overview/wp-content/uploads/2020/07/logo-epfl-1024x576.png")
            }

            override fun getUid(): String {
                return "aUyFLWgYxmoELRUr3jWYie61jbKO"
            }

            override fun isAnonymous(): Boolean {

                return false
            }
        }
    }


    private var isLogged = userInKeyChain

    private fun mockLogin(callback: AuthCallback) {
        //Timer().schedule(1500){
        isLogged = !failing
        when (failing) {
            true -> callback(null, Exception("Mock failing"))
            false -> callback(MOCK_USER, null)
        }
        //}
    }

    override fun getUser(): User? {
        if (isLogged) return MOCK_USER

        return null
    }

    override fun loginWithGoogle(activity: Activity, callback: AuthCallback) {
        mockLogin(callback)
    }

    override fun loginWithEmail(email: String, password: String, callback: AuthCallback) {
        mockLogin(callback)
    }

    override fun loginAnonymously(callback: AuthCallback) {
        mockLogin(callback)
    }

    override fun registerWithEmail(email: String, password: String, callback: AuthCallback) {
        mockLogin(callback)
    }

    override fun registerWithGoogle(activity: Activity, callback: AuthCallback) {
        mockLogin(callback)
    }

    override fun onAuthStateChanged(callback: AuthCallback) {
        callback(getUser(), null)
    }

    override fun clearListener() {

    }

    override fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback) {
        //mockLogin(callback)
        if (withOneTapSignIn) {
            mockLogin(callback)
        } else {
            callback(null, Exception("Mock Error"))
        }
    }

    override fun signOut() {
        isLogged = false
    }

    override fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
    }
}