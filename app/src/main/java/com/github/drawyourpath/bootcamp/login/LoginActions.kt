package com.github.drawyourpath.bootcamp.login

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.github.drawyourpath.bootcamp.R
/**
 * A simple [Fragment] subclass.
 * Use the [LoginActions.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginActions : Fragment(R.layout.fragment_login_actions) {
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = view.findViewById<Button>(R.id.BT_Register);
        registerButton?.setOnClickListener { viewModel.showRegisterUI() }
    }
}