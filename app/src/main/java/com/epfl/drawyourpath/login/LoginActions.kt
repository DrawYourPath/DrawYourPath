package com.epfl.drawyourpath.login

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import android.widget.Button
import android.os.Bundle
import android.widget.EditText
import com.epfl.drawyourpath.R

interface LoginActivityListener {
    fun loginWithGoogle()
    fun loginWithEmailAndPassword(email: String, password: String)
}

class LoginActions : LoginActivityFragment(R.layout.fragment_login_actions) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput    = view.findViewById<EditText>(R.id.ET_Email)
        val passwordInput = view.findViewById<EditText>(R.id.ET_Password)

        val registerButton = view.findViewById<Button>(R.id.BT_Register)
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        val loginWithGoogleButton = view.findViewById<Button>(R.id.BT_LoginGoogle)
        loginWithGoogleButton.setOnClickListener {
            getLoginActivity<LoginActivityListener>().loginWithGoogle()
        }

        val loginWithEmailButton = view.findViewById<Button>(R.id.BT_LoginEmail)
        loginWithEmailButton.setOnClickListener {
            getLoginActivity<LoginActivityListener>().loginWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString()
            )
        }
    }
}