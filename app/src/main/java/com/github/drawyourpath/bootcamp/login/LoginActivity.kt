package com.github.drawyourpath.bootcamp.login;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.replace
import androidx.fragment.app.commit
import com.github.drawyourpath.bootcamp.R

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            showRegisterUI()
        }
    }

    fun showLoginUI() {
        supportFragmentManager.commit {
            replace<LoginActions>(R.id.fragment_container_view)
        }
    }

    fun showRegisterUI() {
        supportFragmentManager.commit {
            replace<RegisterActions>(R.id.fragment_container_view)
        }
    }
}