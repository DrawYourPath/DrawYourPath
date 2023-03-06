package com.github.drawyourpath.bootcamp.login;

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.drawyourpath.bootcamp.R

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            showRegisterUI()
        }

        viewModel.setViewListener { view ->
            when (view) {
                ELoginView.Register -> showRegisterUI()
                ELoginView.Login -> showLoginUI()
            }
        }
    }

    private fun showLoginUI() {
        switchFragment(R.id.fragment_container_view)
    }

    private fun showRegisterUI() {
        switchFragment(R.id.fragment_container_view)
    }

    private fun switchFragment(fragmentId: Int) {
        supportFragmentManager.commit {
            //setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<RegisterActions>(fragmentId)
        }
    }
}