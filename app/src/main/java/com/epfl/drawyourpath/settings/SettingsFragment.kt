package com.epfl.drawyourpath.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.epfl.drawyourpath.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
    //TODO: handle preferences
}