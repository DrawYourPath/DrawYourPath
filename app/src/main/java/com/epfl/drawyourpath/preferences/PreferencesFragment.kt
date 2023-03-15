package com.epfl.drawyourpath.preferences

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.login.LoginActivity

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setupDisconnect()
    }

    private fun setupDisconnect() {
        // Adds the listener to log out and return to the login screen
        findPreference<Preference>("disconnect")?.setOnPreferenceClickListener {
            val auth = FirebaseAuth()
            auth.signOut()
            this.startActivity(Intent(this.context, LoginActivity::class.java))
            true
        }
    }
    //TODO: handle preference
}




