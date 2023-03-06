package com.github.drawyourpath.bootcamp.login

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.drawyourpath.bootcamp.R

/**
 * A simple [Fragment] subclass.
 * Use the [LoginActions.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginActions : Fragment(R.layout.fragment_login_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState);

        val registerButton = view?.findViewById<Button>(R.id.BT_Register);
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }

        return view
    }

}