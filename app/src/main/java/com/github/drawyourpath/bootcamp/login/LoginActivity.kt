package com.github.drawyourpath.bootcamp.login;

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.replace
import androidx.fragment.app.commit
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
        supportFragmentManager.commit {
            //setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<LoginActions>(R.id.fragment_container_view)
        }
    }

    private fun showRegisterUI() {
        supportFragmentManager.commit {
            //setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            replace<RegisterActions>(R.id.fragment_container_view)
        }
    }
}