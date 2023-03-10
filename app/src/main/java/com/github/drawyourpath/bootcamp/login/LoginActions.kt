package com.github.drawyourpath.bootcamp.login

import android.view.View
import androidx.fragment.app.activityViewModels
import com.github.drawyourpath.bootcamp.R
import androidx.fragment.app.Fragment
import android.widget.Button
import android.os.Bundle
import android.widget.EditText

interface LoginActivityListener {
    fun loginWithGoogle()
    fun loginWithEmailAndPassword(email: String, password: String)
}

class LoginActions : Fragment(R.layout.fragment_login_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    private fun getLoginActivity(): LoginActivityListener
    {
        return activity as LoginActivityListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assert(activity is LoginActivityListener)

        val emailInput    = view.findViewById<EditText>(R.id.ET_Email)
        val passwordInput = view.findViewById<EditText>(R.id.ET_Password)

        val registerButton = view.findViewById<Button>(R.id.BT_Register)
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        val loginWithGoogleButton = view.findViewById<Button>(R.id.BT_LoginGoogle)
        loginWithGoogleButton.setOnClickListener {
            getLoginActivity().loginWithGoogle()
        }

        val loginWithEmailButton = view.findViewById<Button>(R.id.BT_LoginEmail)
        loginWithEmailButton.setOnClickListener {
            getLoginActivity().loginWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString()
            )
        }
    }
}