package com.github.drawyourpath.bootcamp.authentication.provider

import com.github.drawyourpath.bootcamp.authentication.AuthUser

typealias AuthUserCallback = (user: AuthUser?, error: Exception?) -> Unit;

interface AuthProvider {
    /**
     * Signs in the user against this provider.
     */
    fun signIn();

    /**
     * Signs out the user.
     */
    fun signOut();

    /**
     * Gets the current user if any.
     */
    fun getCurrentUser(): AuthUser?;

    /**
     * Sets the callback to listen for auth changes.
     * @param callback The function called when the user changed.
     */
    fun setUserCallback(callback: AuthUserCallback?);

    /**
     * Clears the user callback.
     */
    fun clearUserCallback();
}