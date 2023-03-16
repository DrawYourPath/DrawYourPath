package com.epfl.drawyourpath.login


import android.view.View
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

        val registerButton = view.findViewById<Button>(R.id.BT_Register)
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        val loginWithGoogleButton = view.findViewById<Button>(R.id.BT_LoginGoogle)
        loginWithGoogleButton.setOnClickListener {

            getLoginActivity<LoginActivityListener>().loginWithGoogle()
        }

        val loginWithEmailButton = view.findViewById<Button>(R.id.BT_LoginEmail)
        loginWithEmailButton.setOnClickListener {
            getLoginActivity<LoginActivityListener>().loginWithEmailAndPassword(
                view.findViewById<EditText>(R.id.ET_Email).text.toString(),
                view.findViewById<EditText>(R.id.ET_Password).text.toString()
            )
        }
    }
}