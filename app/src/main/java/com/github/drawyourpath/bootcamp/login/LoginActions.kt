package com.github.drawyourpath.bootcamp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.github.drawyourpath.bootcamp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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