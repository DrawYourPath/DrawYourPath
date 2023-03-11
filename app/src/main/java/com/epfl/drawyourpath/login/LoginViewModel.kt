package com.epfl.drawyourpath.login

import androidx.lifecycle.ViewModel

enum class ELoginView {
    Login,
    Register,
}

typealias ChangeLoginViewCallback = (newView: ELoginView) -> Unit;

class LoginViewModel : ViewModel() {
    private var listener: ChangeLoginViewCallback? = null;

    /**
     * Fires the view listener for the login UI.
     */
    fun showLoginUI() {
        listener?.let {
            it(ELoginView.Login);
        }
    }

    /**
     * Fires the view listener for the register UI.
     */
    fun showRegisterUI() {
        listener?.let {
            it(ELoginView.Register);
        }
    }

    /**
     * Sets a new listener for view changes requests.
     */
    fun setViewListener(inListener: ChangeLoginViewCallback) {
        listener = inListener;
    }

    fun loginWithEmail(email: String, password: String) {
        // TODO:
    }

    fun loginWithGoogle() {
        // TODO:
    }

    fun registerWithEmail() {
        // TODO:
    }

    fun registerWithGoogle() {
        // TODO:
    }

    fun registerAnonymously() {
        // TODO:
    }

}