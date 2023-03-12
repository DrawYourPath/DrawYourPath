package com.epfl.drawyourpath.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R

interface RegisterActivityListener {
    fun registerWithGoogle();
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

        val registerButton = view.findViewById<Button>(R.id.BT_Login);
        registerButton?.setOnClickListener { viewModel.showLoginUI() }

        val registerWithGoogleButton = view.findViewById<Button>(R.id.BT_RegisterGoogle)
        registerWithGoogleButton.setOnClickListener {
            getRegisterActivity().registerWithGoogle()
        }
    }
}