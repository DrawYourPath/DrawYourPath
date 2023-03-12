package com.epfl.drawyourpath.login

import com.epfl.drawyourpath.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

interface RegisterActivityListener {
    fun registerWithGoogle();
    fun registerAnonymously()
    fun registerWithEmailAndPassword(email: String, password: String)
}

class RegisterActions : LoginActivityFragment(R.layout.fragment_register_actions) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        val emailInput    = view.findViewById<EditText>(R.id.ET_Email)
        val passwordInput = view.findViewById<EditText>(R.id.ET_Password)

        val registerButton = view.findViewById<Button>(R.id.BT_Login);
        registerButton?.setOnClickListener {
            viewModel.showLoginUI()
        }

        val registerWithGoogleButton = view.findViewById<Button>(R.id.BT_RegisterGoogle)
        registerWithGoogleButton.setOnClickListener {
            getLoginActivity<RegisterActivityListener>().registerWithGoogle()
        }

        val registerWithEmailButton = view.findViewById<Button>(R.id.BT_RegisterEmail)
        registerWithEmailButton.setOnClickListener {
            getLoginActivity<RegisterActivityListener>().registerWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString())
        }

        val registerAnonymousButton = view.findViewById<Button>(R.id.BT_RegisterAnonymous)
        registerAnonymousButton.setOnClickListener {
            getLoginActivity<RegisterActivityListener>().registerAnonymously()
        }
    }
}