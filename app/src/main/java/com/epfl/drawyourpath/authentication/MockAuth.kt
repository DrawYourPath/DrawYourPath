package com.epfl.drawyourpath.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import java.util.concurrent.CompletableFuture

class MockAuth(
    private val failing: Boolean = false,
    userInKeyChain: Boolean = false,
    private val withOneTapSignIn: Boolean = false,
    private val forceSigned: Boolean = false,
) : Auth {
    companion object {
        val MOCK_USER = object : User {
            override fun getDisplayName(): String = "Clark Kent"

            override fun getEmail(): String = "mockuser@mockdomain.org"

            override fun getPhoneNumber(): String = "+0000000000"

            override fun getPhotoUrl(): Uri? =
                Uri.parse("https://www.epfl.ch/about/overview/wp-content/uploads/2020/07/logo-epfl-1024x576.png")

            override fun getUid(): String = "aUyFLWgYxmoELRUr3jWYie61jbKO"

            override fun isAnonymous(): Boolean = false

            override fun updatePassword(password: String): CompletableFuture<Void> {
                if (password.isEmpty() || password.length < 5) {
                    val res = CompletableFuture<Void>()
                    res.completeExceptionally(Exception("Password is empty"))
                    return res
                }
                return CompletableFuture.completedFuture(null)
            }
        }
    }

    private var isLogged = userInKeyChain || forceSigned

    private fun mockLogin(callback: AuthCallback) {
        // Timer().schedule(1500){
        isLogged = !failing
        when (failing) {
            true -> callback(null, Exception("Mock failing"))
            false -> callback(MOCK_USER, null)
        }
        // }
    }

    override fun getUser(): User? = if (isLogged) MOCK_USER else null

    override fun loginWithGoogle(activity: Activity, callback: AuthCallback) = mockLogin(callback)

    override fun loginWithEmail(email: String, password: String, callback: AuthCallback) = mockLogin(callback)

    override fun loginAnonymously(callback: AuthCallback) = mockLogin(callback)

    override fun registerWithEmail(email: String, password: String, callback: AuthCallback) = mockLogin(callback)

    override fun registerWithGoogle(activity: Activity, callback: AuthCallback) = mockLogin(callback)

    override fun onAuthStateChanged(callback: AuthCallback) = callback(getUser(), null)

    override fun clearListener() {}

    override fun launchOneTapGoogleSignIn(activity: Activity, callback: AuthCallback) =
        // mockLogin(callback)
        if (withOneTapSignIn) {
            mockLogin(callback)
        } else {
            callback(null, Exception("Mock Error"))
        }

    override fun signOut() {
        isLogged = false
    }

    override fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {}
}
