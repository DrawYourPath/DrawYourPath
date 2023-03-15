package com.epfl.drawyourpath.userProfileCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R

class EndProfileCreationFragment : Fragment(R.layout.fragment_end_profile_creation) {
    private var username = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //retrieve the isRunTestValue and userName from the PersonalInfoFragment
        val argsFromLastFrag: Bundle? = arguments
        if (argsFromLastFrag != null) {
            username = argsFromLastFrag.getString("userName").toString()
        }
        val welcomeMessage: String =
            "We are happy to welcome you, " + username + " in the DrawYourPath app." +
                    " Please click on the BEGIN NOW button to discover the app !"
        val welcomeOutputTex: TextView =
            view.findViewById(R.id.endProfileCreation_text_userProfileCreation)
        welcomeOutputTex.text = welcomeMessage


        //go to the app main activity when click on the BEGIN NOW button
        val beginButton: Button = view.findViewById(R.id.goToMainActivity)
        beginButton.setOnClickListener {
            //TODO
            //adc some lines code to go to main app activity
        }
    }
}