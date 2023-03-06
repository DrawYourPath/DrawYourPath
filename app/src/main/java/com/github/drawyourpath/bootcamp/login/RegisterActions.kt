package com.github.drawyourpath.bootcamp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.drawyourpath.bootcamp.R


/**
 * A simple [Fragment] subclass.
 */
class RegisterActions : Fragment(R.layout.fragment_register_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState);

        val registerButton = view?.findViewById<Button>(R.id.BT_Login);

        registerButton?.setOnClickListener { viewModel.showLoginUI() }

        return view
    }
}