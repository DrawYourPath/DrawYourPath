package com.epfl.drawyourpath.mainpage.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.IV_Tropy).setImageResource(R.drawable.award)
        view.findViewById<ImageView>(R.id.IV_ProfilePicture).setImageResource(R.drawable.profile_placholderpng)
    }
}