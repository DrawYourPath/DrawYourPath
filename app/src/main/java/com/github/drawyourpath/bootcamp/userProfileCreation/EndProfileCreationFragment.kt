package com.github.drawyourpath.bootcamp.userProfileCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.drawyourpath.bootcamp.R

class EndProfileCreationFragment : Fragment() {
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_end_profile_creation, container, false)

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

        return view
    }
}