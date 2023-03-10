package com.github.drawyourpath.bootcamp.login

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.github.drawyourpath.bootcamp.R

interface LoginActivityListener {
    fun loginWithGoogle();
}

class LoginActions : Fragment(R.layout.fragment_login_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    private fun getLoginActivity(): LoginActivityListener
    {
        return activity as LoginActivityListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = view.findViewById<Button>(R.id.BT_Register);
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        val loginWithGoogleButton = view.findViewById<Button>(R.id.BT_LoginGoogle)
        loginWithGoogleButton.setOnClickListener {
            getLoginActivity().loginWithGoogle();
        }
    }
}