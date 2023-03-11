package com.epfl.drawyourpath.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R


/**
 * A simple [Fragment] subclass.
 */
class RegisterActions : Fragment(R.layout.fragment_register_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        val registerButton = view.findViewById<Button>(R.id.BT_Login);

        registerButton?.setOnClickListener { viewModel.showLoginUI() }
    }
}