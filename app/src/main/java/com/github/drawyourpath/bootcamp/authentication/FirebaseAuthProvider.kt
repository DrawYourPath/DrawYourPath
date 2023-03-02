package com.github.drawyourpath.bootcamp.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class FirebaseAuthProvider : AuthProvider {
    protected fun makeFirebaseUserAdapter(user: FirebaseUser?): AuthUser? {
        if (user == null) {
            return null;
        }

        return object : AuthUser {
            override fun getEmail(): String {
                return user.email ?: ""
            }

            override fun isAnonymous(): Boolean {
                return user.isAnonymous
            }

            override fun getUid(): String {
                return user.uid
            }

            override fun getPhoneNumber(): String {
                return user.phoneNumber ?: ""
            }

            override fun getDisplayName(): String {
                return user.displayName ?: ""
            }
        }
    }

    override fun getCurrentUser(): AuthUser? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null;
        return makeFirebaseUserAdapter(user);
    }

    override fun clearUserCallback() {
        setUserCallback(null);
    }

    override fun setUserCallback(callback: AuthUserCallback?) {
        if (callback == null) {
            if (firebaseUserCallback != null)
            {
                FirebaseAuth.getInstance().removeAuthStateListener(authListener);
            }
            firebaseUserCallback = null;
            return;
        }

        clearUserCallback();

        firebaseUserCallback = callback;
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    protected var firebaseUserCallback: AuthUserCallback? = null

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        firebaseUserCallback?.let {
            it(makeFirebaseUserAdapter(auth.currentUser), null)
        }
    }

    override fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}