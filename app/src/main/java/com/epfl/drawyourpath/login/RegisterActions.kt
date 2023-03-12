package com.epfl.drawyourpath.login

import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

interface RegisterActivityListener {
    fun registerWithGoogle();
    fun registerAnonymously()
    fun registerWithEmailAndPassword(email: String, password: String)
}

class RegisterActions : Fragment(R.layout.fragment_register_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    private fun getRegisterActivity(): RegisterActivityListener
    {
        return activity as RegisterActivityListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        // Assumes the activity implements the RegisterActivity interface to forward
        // events.
        assert(activity is RegisterActivityListener);

        val emailInput    = view.findViewById<EditText>(R.id.ET_Email)
        val passwordInput = view.findViewById<EditText>(R.id.ET_Password)

        val registerButton = view.findViewById<Button>(R.id.BT_Login);
        registerButton?.setOnClickListener {
            viewModel.showLoginUI()
        }

        val registerWithGoogleButton = view.findViewById<Button>(R.id.BT_RegisterGoogle)
        registerWithGoogleButton.setOnClickListener {
            getRegisterActivity().registerWithGoogle()
        }

        val registerWithEmailButton = view.findViewById<Button>(R.id.BT_RegisterEmail)
        registerWithEmailButton.setOnClickListener {
            getRegisterActivity().registerWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString())
        }
    }
}