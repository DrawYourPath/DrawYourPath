package com.epfl.drawyourpath.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R

interface LoginActivityListener {
    fun loginWithGoogle()
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

        val registerButton = view.findViewById<Button>(R.id.BT_Register)
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        val loginWithGoogleButton = view.findViewById<Button>(R.id.BT_LoginGoogle)
        loginWithGoogleButton.setOnClickListener {
            getLoginActivity().loginWithGoogle()
        }
    }
}