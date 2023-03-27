package com.epfl.drawyourpath.preferences

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.login.ENABLE_ONETAP_SIGNIN
import com.epfl.drawyourpath.login.LoginActivity

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Specify the layout
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setupDisconnect()
    }

    private fun setupDisconnect() {
        // Adds the listener to log out and return to the login screen
        findPreference<Preference>("disconnect")?.setOnPreferenceClickListener {

            FirebaseAuth().signOut()
            val intent = Intent(this.context, LoginActivity::class.java)

            // Disables OneTap, avoids instantly reconnecting after disconnecting
            intent.putExtra(ENABLE_ONETAP_SIGNIN, false)

            this.startActivity(intent)
            true
        }
    }

    //TODO: handle other preferences
}




