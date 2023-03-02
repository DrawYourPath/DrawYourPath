package com.github.drawyourpath.bootcamp.authentication.provider

import com.github.drawyourpath.bootcamp.authentication.AuthUser

class MockAuthProvider : AuthProvider {

    private var userCallback: AuthUserCallback? = null;

    private val fakeValidUser: AuthUser = object : AuthUser {
        override fun getEmail(): String {
            return "test@test.org"
        }

        override fun isAnonymous(): Boolean {
            return false;
        }

        override fun getUid(): String {
            return "007";
        }

        override fun getPhoneNumber(): String {
            return "+41790000000"
        }

        override fun getDisplayName(): String {
            return "John Doe";
        }
    }

    private var isSigned: Boolean = false;

    override fun signIn() {
        isSigned = true;
        userCallback?.let { it(fakeValidUser, null) }
    }

    override fun signOut() {
        isSigned = false;
    }

    override fun getCurrentUser(): AuthUser? {
        return if (isSigned) fakeValidUser else null;
    }

    override fun setUserCallback(callback: AuthUserCallback?) {
        userCallback = callback;
        userCallback?.let { it(null, null) }
    }

    override fun clearUserCallback() {
        setUserCallback(null);
    }

}